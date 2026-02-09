/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;

import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;
import CRUnedCompiladresProyecto1.symbols.SymbolTable;
import CRUnedCompiladresProyecto1.symbols.VbType;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class DimValidator {
    
/**
     * Valida una línea. Si no es una sentencia Dim, devuelve lista vacía.
     *
     * @param moduleSeen si ya apareció "Module" antes (regla del enunciado).
     *                  Si aún no implementas Sprint 2, puedes pasar true para ignorar esa regla por ahora.
     */
    
    public List<LexError> validate(LineRecord lr, SymbolTable st, boolean moduleSeen) {
    List<LexError> errors = new ArrayList<>();

    List<Token> sig = significantTokens(lr.getTokens());
    if (sig.isEmpty()) return errors;

    // Comentario (solo si inicia con ')
    if (sig.size() == 1 && sig.get(0).getType() == TokenType.COMMENT) {
        return errors;
    }

    Token first = sig.get(0);
    if (!(first.getType() == TokenType.KEYWORD && eqi(first.getLexeme(), "Dim"))) {
        return errors; // no es Dim
    }

    if (!moduleSeen) {
        errors.add(err("DIM001", "La sentencia Dim debe aparecer después de Module.", first));
    }

    int i = 1;

    // 1) Identificador
    if (i >= sig.size()) {
        errors.add(err("DIM002", "Falta el identificador después de Dim.", first));
        return errors;
    }

    Token idTok = sig.get(i++);
    // Caso típico de cascada: "Dim As Integer" (se fue directo a As)
    if (idTok.getType() == TokenType.KEYWORD && eqi(idTok.getLexeme(), "As")) {
        errors.add(err("DIM002", "Falta el identificador después de Dim.", first));
        return errors; // corte temprano para evitar cascada
    }

    IdentifierValidator idValidator = new IdentifierValidator();
    List<LexError> idErrors = idValidator.validateIdentifier(idTok);
    errors.addAll(idErrors);

    boolean idTokenIsIdentifier = (idTok.getType() == TokenType.IDENTIFIER);
    boolean idOk = idTokenIsIdentifier && idErrors.isEmpty();

    if (!idTokenIsIdentifier) {
        errors.add(err("DIM003", "Identificador inválido después de Dim: '" + idTok.getLexeme() + "'.", idTok));
    }

    String varName = idOk ? idTok.getLexeme() : null;

    // 2) As (con resincronización)
    if (i >= sig.size()) {
        errors.add(err("DIM004", "Falta 'As' y el tipo. Formato esperado: Dim <id> As <Tipo>.", idTok));
        return errors;
    }

    Token asTok = sig.get(i);
    if (!(asTok.getType() == TokenType.KEYWORD && eqi(asTok.getLexeme(), "As"))) {
        // resincronizar: buscar el próximo "As"
        int asIdx = findNextKeywordIndex(sig, i, "As");
        errors.add(err("DIM005", "Se esperaba 'As' después del identificador en Dim.", asTok));

        if (asIdx == -1) {
            // no hay As -> no podemos seguir parseando de forma confiable
            errors.add(err("DIM004", "Falta 'As' y el tipo. Formato esperado: Dim <id> As <Tipo>.", idTok));
            return errors;
        }

        i = asIdx;
        asTok = sig.get(i); // ahora sí es As
    }
    i++; // consume As

    // 3) Tipo
    if (i >= sig.size()) {
        errors.add(err("DIM006", "Falta el tipo después de 'As'.", asTok));
        return errors;
    }

    Token typeTok = sig.get(i++);
    VbType declaredType = VbType.fromLexeme(typeTok.getLexeme());
    if (declaredType == VbType.UNKNOWN) {
        errors.add(err("DIM007", "Tipo no permitido o desconocido: '" + typeTok.getLexeme()
                + "'. Tipos válidos: Integer, String, Boolean, Byte.", typeTok));
    }

    // 4) Declaración en tabla de símbolos SOLO si el encabezado está perfecto
    boolean headerOk = (varName != null) && (declaredType != VbType.UNKNOWN);

    if (headerOk) {
        if (st.isDeclared(varName)) {
            errors.add(err("DIM008", "La variable '" + varName + "' ya fue declarada.", idTok));
        } else {
            st.declare(varName, declaredType, lr.getLineNumber());
        }
    }

    // 5) ¿Asignación?
    if (i >= sig.size()) {
        return errors; // Dim sin asignación
    }

    Token next = sig.get(i);
    if (!(next.getType() == TokenType.OPERATOR && "=".equals(next.getLexeme()))) {
        // resincronizar: buscar '='
        int eqIdx = findNextOperatorIndex(sig, i, "=");
        errors.add(err("DIM009", "Token inesperado después del tipo. Si hay asignación debe ser '='.", next));

        if (eqIdx == -1) {
            // no hay '=' -> no forzamos más parseos (evita cascada)
            return errors;
        }

        i = eqIdx;
        next = sig.get(i);
    }

    i++; // consume '='

    if (i >= sig.size()) {
        errors.add(err("DIM010", "Falta la expresión después de '='.", next));
        return errors;
    }

    List<Token> expr = sig.subList(i, sig.size());

    // Si no podemos confiar en el encabezado, no hacemos chequeo fuerte de expresión (evita cascada)
    if (!headerOk) {
        errors.addAll(validateExprLoosely(expr));
        return errors;
    }

    // Si el tipo es unknown (aunque headerOk lo evita), mantenemos esto por seguridad
    if (declaredType == VbType.UNKNOWN) {
        errors.addAll(validateExprLoosely(expr));
        return errors;
    }

    // Validación por tipo declarado
    if (declaredType.isNumeric()) {
        errors.addAll(validateNumericExpr(expr, st));
    } else if (declaredType == VbType.STRING) {
        errors.addAll(validateStringExpr(expr, st));
    } else if (declaredType == VbType.BOOLEAN) {
        errors.addAll(validateBooleanExpr(expr, st));
    }

    return errors;
}


    // ---------------- Helpers ----------------

    private List<Token> significantTokens(List<Token> tokens) {
        List<Token> out = new ArrayList<>();
        for (Token t : tokens) {
            if (t.getType() == TokenType.WHITESPACE) continue;
            out.add(t);
        }
        return out;
    }

    private boolean eqi(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private LexError err(String code, String msg, Token at) {
        return new LexError(code, msg, at.getLine(), at.getColumn());
    }

    private boolean isMathOp(Token t) {
        if (t.getType() != TokenType.OPERATOR) return false;
        String x = t.getLexeme();
        return "+".equals(x) || "-".equals(x) || "*".equals(x) || "/".equals(x);
    }

    private List<LexError> validateExprLoosely(List<Token> expr) {
        List<LexError> errors = new ArrayList<>();
        // Solo verifica que no empiece/termine con operador matemático
        if (!expr.isEmpty() && isMathOp(expr.get(0))) {
            // Permitimos + o - como unario al inicio, pero no * /
            String op = expr.get(0).getLexeme();
            if ("*".equals(op) || "/".equals(op)) {
                errors.add(err("DIM011", "La expresión no puede iniciar con operador '" + op + "'.", expr.get(0)));
            }
        }
        if (!expr.isEmpty() && isMathOp(expr.get(expr.size() - 1))) {
            errors.add(err("DIM012", "La expresión no puede terminar con un operador.", expr.get(expr.size() - 1)));
        }
        return errors;
    }

    private List<LexError> validateNumericExpr(List<Token> expr, SymbolTable st) {
        List<LexError> errors = new ArrayList<>();

        boolean expectTerm = true;
        boolean unaryAllowed = true; // al inicio o después de un operador, permitimos + o -

        for (int i = 0; i < expr.size(); i++) {
            Token t = expr.get(i);

            if (expectTerm) {
                // Unario +/-
                if (t.getType() == TokenType.OPERATOR && ("+".equals(t.getLexeme()) || "-".equals(t.getLexeme())) && unaryAllowed) {
                    unaryAllowed = false;
                    continue;
                }

                if (t.getType() == TokenType.NUMBER) {
                    expectTerm = false;
                    unaryAllowed = false;
                    continue;
                }

                if (t.getType() == TokenType.IDENTIFIER || t.getType() == TokenType.KEYWORD) {
                    // KEYWORD aquí puede ser un identificador mal clasificado; igual lo tratamos como nombre para dar error útil
                    String name = t.getLexeme();
                    if (!st.isDeclared(name)) {
                        errors.add(err("DIM013", "Variable no declarada en expresión numérica: '" + name + "'.", t));
                    } else {
                        VbType vt = st.getType(name);
                        if (!vt.isNumeric()) {
                            errors.add(err("DIM014", "La variable '" + name + "' no es numérica (es " + vt + ").", t));
                        }
                    }
                    expectTerm = false;
                    unaryAllowed = false;
                    continue;
                }

                errors.add(err("DIM015", "Se esperaba un número o variable numérica en la expresión.", t));
                // intentamos seguir: marcamos como ya consumido un término para no inundar
                expectTerm = false;
                unaryAllowed = false;
            } else {
                // esperamos operador matemático
                if (isMathOp(t)) {
                    expectTerm = true;
                    unaryAllowed = true;
                    continue;
                }
                errors.add(err("DIM016", "Se esperaba un operador (+,-,*,/) en la expresión.", t));
            }
        }

        if (expectTerm && !expr.isEmpty()) {
            errors.add(err("DIM017", "La expresión termina incompleta (falta un término).", expr.get(expr.size() - 1)));
        }

        return errors;
    }

    private List<LexError> validateStringExpr(List<Token> expr, SymbolTable st) {
        List<LexError> errors = new ArrayList<>();

        
        if (expr.size() != 1) {
            // si hay operadores, reportamos específico
            for (Token t : expr) {
                if (isMathOp(t)) {
                    errors.add(err("DIM018", "No se permiten operaciones numéricas en una variable String.", t));
                }
            }
            errors.add(err("DIM019", "Asignación String inválida. Se esperaba un literal \"...\" o una variable String.", expr.get(0)));
            return errors;
        }

        Token t = expr.get(0);
        if (t.getType() == TokenType.STRING_LITERAL) return errors;

        if (t.getType() == TokenType.IDENTIFIER || t.getType() == TokenType.KEYWORD) {
            String name = t.getLexeme();
            if (!st.isDeclared(name)) {
                errors.add(err("DIM020", "Variable no declarada en asignación String: '" + name + "'.", t));
            } else if (st.getType(name) != VbType.STRING) {
                errors.add(err("DIM021", "La variable '" + name + "' no es String (es " + st.getType(name) + ").", t));
            }
            return errors;
        }

        errors.add(err("DIM022", "Asignación String inválida.", t));
        return errors;
    }

    private List<LexError> validateBooleanExpr(List<Token> expr, SymbolTable st) {
        List<LexError> errors = new ArrayList<>();

        
        if (expr.size() != 1) {
            for (Token t : expr) {
                if (isMathOp(t)) {
                    errors.add(err("DIM023", "No se permiten operaciones numéricas en una variable Boolean.", t));
                }
            }
            errors.add(err("DIM024", "Asignación Boolean inválida. Se esperaba True/False o una variable Boolean.", expr.get(0)));
            return errors;
        }

        Token t = expr.get(0);
        if (isBooleanLiteral(t)) return errors;

        if (t.getType() == TokenType.IDENTIFIER || t.getType() == TokenType.KEYWORD) {
            String name = t.getLexeme();
            if (!st.isDeclared(name)) {
                errors.add(err("DIM025", "Variable no declarada en asignación Boolean: '" + name + "'.", t));
            } else if (st.getType(name) != VbType.BOOLEAN) {
                errors.add(err("DIM026", "La variable '" + name + "' no es Boolean (es " + st.getType(name) + ").", t));
            }
            return errors;
        }

        errors.add(err("DIM027", "Asignación Boolean inválida.", t));
        return errors;
    }

    private boolean isBooleanLiteral(Token t) {
        String x = t.getLexeme();
        return x != null && (x.equalsIgnoreCase("true") || x.equalsIgnoreCase("false"));
    }
    
    private int findNextKeywordIndex(List<Token> sig, int start, String keyword) {
    for (int k = start; k < sig.size(); k++) {
        Token t = sig.get(k);
        if (t.getType() == TokenType.KEYWORD && eqi(t.getLexeme(), keyword)) return k;
    }
    return -1;
}

    private int findNextOperatorIndex(List<Token> sig, int start, String op) {
        for (int k = start; k < sig.size(); k++) {
            Token t = sig.get(k);
            if (t.getType() == TokenType.OPERATOR && op.equals(t.getLexeme())) return k;
        }
        return -1;
    }


    
}
