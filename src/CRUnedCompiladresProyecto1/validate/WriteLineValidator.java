/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;

import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;

import java.util.ArrayList;
import java.util.List;


        
/**
 *
 * Valida sentencias Console.WriteLine(...)
 *
 * Reglas (en versión incremental):
 * - Debe ser Console.WriteLine(...)
 * - Debe tener '(' y ')'
 * - No permite paréntesis vacíos: Console.WriteLine()
 * - Si el primer argumento es un STRING_LITERAL, debe cerrar comillas
 * - No debe haber código extra después del ')'
 *
 * Nota: Ignora WHITESPACE para analizar estructura.
 * 
 * @author Elias
 */
public class WriteLineValidator {
    
    public List<LexError> validate(LineRecord lr) {
        List<LexError> errors = new ArrayList<>();
        if (lr == null) return errors;

        List<Token> sig = significantTokens(lr.getTokens());
        if (sig.isEmpty()) return errors;

        // Comentario válido SOLO si la línea inicia con '
        if (sig.size() == 1 && sig.get(0).getType() == TokenType.COMMENT) return errors;

        // Solo validamos si la línea empieza con Console.WriteLine
        if (!looksLikeConsoleWriteLine(sig)) return errors;

        // Estructura base esperada:
        // Console . WriteLine ( ... )
        Token consoleTok = sig.get(0);

        // 1) Debe existir '(' en la posición esperada (index 3)
        if (sig.size() < 4 || !(sig.get(3).getType() == TokenType.PUNCTUATION && "(".equals(sig.get(3).getLexeme()))) {
            Token at = sig.size() > 3 ? sig.get(3) : sig.get(sig.size() - 1);
            errors.add(err("CWL002", "Console.WriteLine debe incluir paréntesis de apertura '(' y cierre ')'.", at));
            return errors;
        }

        int openIdx = 3;

        // 2) Debe existir ')'
        int closeIdx = findClosingParen(sig, openIdx + 1);
        if (closeIdx == -1) {
            errors.add(err("CWL003", "Falta el paréntesis de cierre ')' en Console.WriteLine.", sig.get(openIdx)));
            // seguimos, porque igual podemos detectar string sin comillas cerradas
        }

        // 3) No permite paréntesis vacíos: Console.WriteLine()
        int endForContent = (closeIdx == -1) ? sig.size() : closeIdx;
        boolean hasContent = (openIdx + 1 < endForContent);

        if (!hasContent) {
            if (closeIdx != -1) {
                errors.add(err("CWL004", "Console.WriteLine no permite paréntesis vacíos.", sig.get(openIdx)));
            }
            return errors;
        }

        // 4) Si el primer token dentro de paréntesis es STRING_LITERAL debe cerrar con "
        Token firstInside = sig.get(openIdx + 1);
        if (firstInside.getType() == TokenType.STRING_LITERAL) {
            String lex = firstInside.getLexeme();
            boolean closes = lex.length() >= 2 && lex.startsWith("\"") && lex.endsWith("\"");
            if (!closes) {
                errors.add(err("CWL005", "La cadena de texto en Console.WriteLine debe abrir y cerrar con comillas dobles (\").", firstInside));
            }
        }

        // 5) No debe haber código extra después del ')'
        if (closeIdx != -1 && closeIdx + 1 < sig.size()) {
            errors.add(err("CWL006", "No debe existir código adicional después de ')' en Console.WriteLine.", sig.get(closeIdx + 1)));
        }

        return errors;
    }

    // ---------------- Helpers ----------------

    private List<Token> significantTokens(List<Token> tokens) {
        List<Token> out = new ArrayList<>();
        if (tokens == null) return out;
        for (Token t : tokens) {
            if (t.getType() == TokenType.WHITESPACE) continue;
            out.add(t);
        }
        return out;
    }

    private boolean looksLikeConsoleWriteLine(List<Token> sig) {
        if (sig.size() < 3) return false;

        return isKeyword(sig.get(0), "Console")
                && sig.get(1).getType() == TokenType.PUNCTUATION
                && ".".equals(sig.get(1).getLexeme())
                && isKeyword(sig.get(2), "WriteLine");
    }

    private boolean isKeyword(Token t, String kw) {
        return t.getType() == TokenType.KEYWORD
                && t.getLexeme() != null
                && t.getLexeme().equalsIgnoreCase(kw);
    }

    private int findClosingParen(List<Token> sig, int start) {
        for (int i = start; i < sig.size(); i++) {
            Token t = sig.get(i);
            if (t.getType() == TokenType.PUNCTUATION && ")".equals(t.getLexeme())) return i;
        }
        return -1;
    }

    private LexError err(String code, String msg, Token at) {
        return new LexError(code, msg, at.getLine(), at.getColumn());
    }

    
}
