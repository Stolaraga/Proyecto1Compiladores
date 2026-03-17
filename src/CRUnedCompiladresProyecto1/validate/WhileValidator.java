/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;

import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;
import CRUnedCompiladresProyecto1.symbols.SymbolTable;
import CRUnedCompiladresProyecto1.symbols.VbType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author Elias
 */
public class WhileValidator {

    private static class WhileFrame {
        final int line;
        boolean hasExecutableLine = false;

        WhileFrame(int line) {
            this.line = line;
        }
    }

    public List validate(LineRecord lr, SymbolTable st) {
        List errors = new ArrayList<>();
        List<Token> sig = significantTokensStopAtComment(lr.getTokens());

        if (sig.isEmpty()) return errors;

        Token first = sig.get(0);
        if (!isKeyword(first, "While")) return errors;

        if (sig.size() < 4) {
            errors.add(new LexError(
                "WHI002",
                "La condición de While debe tener el formato: While <identificador> <, > o = <entero>.",
                first.getLine(),
                first.getColumn()
            ));
            return errors;
        }

        Token left = sig.get(1);
        Token op = sig.get(2);
        Token right = sig.get(3);

        if (left.getType() != TokenType.IDENTIFIER) {
            errors.add(new LexError(
                "WHI003",
                "La condición de While debe iniciar con una variable declarada.",
                left.getLine(),
                left.getColumn()
            ));
        } else {
            String name = left.getLexeme();

            if (!st.isDeclared(name)) {
                errors.add(new LexError(
                    "WHI004",
                    "La variable '" + name + "' no ha sido declarada antes del While.",
                    left.getLine(),
                    left.getColumn()
                ));
            } else if (st.getType(name) != VbType.INTEGER) {
                errors.add(new LexError(
                    "WHI005",
                    "La variable '" + name + "' debe ser de tipo Integer en la condición del While.",
                    left.getLine(),
                    left.getColumn()
                ));
            }
        }

        if (!(op.getType() == TokenType.OPERATOR
                && ("<".equals(op.getLexeme())
                || ">".equals(op.getLexeme())
                || "=".equals(op.getLexeme())))) {
            errors.add(new LexError(
                "WHI006",
                "El While solo permite los operadores <, > o =.",
                op.getLine(),
                op.getColumn()
            ));
        }

        if (right.getType() != TokenType.NUMBER) {
            errors.add(new LexError(
                "WHI007",
                "El lado derecho de la condición While debe ser un entero.",
                right.getLine(),
                right.getColumn()
            ));
        }

        if (sig.size() > 4) {
            Token extra = sig.get(4);
            errors.add(new LexError(
                "WHI008",
                "La sentencia While contiene tokens adicionales no permitidos.",
                extra.getLine(),
                extra.getColumn()
            ));
        }

        return errors;
    }

    public List validateStructure(AnalysisResult ar) {
        List errors = new ArrayList<>();
        if (ar == null || ar.getLines() == null) return errors;

        Deque<WhileFrame> stack = new ArrayDeque<>();

        for (Object obj : ar.getLines()) {
            LineRecord lr = (LineRecord) obj;
            List<Token> sig = significantTokensStopAtComment(lr.getTokens());

            if (sig.isEmpty()) continue;

            Token first = sig.get(0);

            if (isKeyword(first, "While")) {
                if (!stack.isEmpty()) {
                    stack.peek().hasExecutableLine = true;
                }
                stack.push(new WhileFrame(first.getLine()));
                continue;
            }

            if (isKeyword(first, "End") && sig.size() >= 2 && isKeyword(sig.get(1), "While")) {
                if (stack.isEmpty()) {
                    errors.add(new LexError(
                        "WHI020",
                        "Se encontró 'End While' sin un 'While' abierto.",
                        first.getLine(),
                        first.getColumn()
                    ));
                } else {
                    WhileFrame frame = stack.pop();
                    if (!frame.hasExecutableLine) {
                        errors.add(new LexError(
                            "WHI021",
                            "El bloque While no puede estar vacío ni contener solo comentarios.",
                            frame.line,
                            1
                        ));
                    }
                }
                continue;
            }

            if (!stack.isEmpty()) {
                stack.peek().hasExecutableLine = true;
            }
        }

        while (!stack.isEmpty()) {
            WhileFrame frame = stack.pop();
            errors.add(new LexError(
                "WHI001",
                "Falta 'End While' para el 'While' iniciado.",
                frame.line,
                1
            ));
        }

        return errors;
    }

    private List<Token> significantTokensStopAtComment(List tokens) {
        List<Token> out = new ArrayList<>();
        if (tokens == null) return out;

        for (Object obj : tokens) {
            Token t = (Token) obj;
            if (t.getType() == TokenType.COMMENT) break;
            if (t.getType() == TokenType.WHITESPACE) continue;
            out.add(t);
        }
        return out;
    }

    private boolean isKeyword(Token t, String kw) {
        return t.getType() == TokenType.KEYWORD
                && t.getLexeme() != null
                && t.getLexeme().equalsIgnoreCase(kw);
    }
}