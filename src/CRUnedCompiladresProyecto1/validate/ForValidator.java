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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author Elias
 */
public class ForValidator {
    
    private static class ForFrame {
        final int line;
        boolean hasExecutableLine = false;

        ForFrame(int line) {
            this.line = line;
        }
    }

    public List validate(LineRecord lr) {
        List errors = new ArrayList<>();
        List sig = significantTokensStopAtComment(lr.getTokens());

        if (sig.isEmpty()) return errors;

        Token first = (Token) sig.get(0);
        if (!matchesWord(first, "For")) return errors;

        if (sig.size() < 6) {
            errors.add(new LexError(
                "FOR002",
                "La sentencia For debe tener el formato: For variable = entero To entero.",
                first.getLine(),
                first.getColumn()
            ));
            return errors;
        }

        Token variable = (Token) sig.get(1);
        Token equals = (Token) sig.get(2);
        Token start = (Token) sig.get(3);
        Token toToken = (Token) sig.get(4);
        Token end = (Token) sig.get(5);

        boolean invalidHeader = false;

        if (variable.getType() != TokenType.IDENTIFIER) {
            invalidHeader = true;
        }

        if (!(equals.getType() == TokenType.OPERATOR && "=".equals(equals.getLexeme()))) {
            invalidHeader = true;
        }

        if (!matchesWord(toToken, "To")) {
            invalidHeader = true;
        }

        if (invalidHeader) {
            errors.add(new LexError(
                "FOR002",
                "La sentencia For debe tener el formato: For variable = entero To entero.",
                first.getLine(),
                first.getColumn()
            ));
        }

        if (start.getType() != TokenType.NUMBER) {
            errors.add(new LexError(
                "FOR003",
                "El límite inicial del For debe ser un número entero.",
                start.getLine(),
                start.getColumn()
            ));
        }

        if (end.getType() != TokenType.NUMBER) {
            errors.add(new LexError(
                "FOR004",
                "El límite final del For debe ser un número entero.",
                end.getLine(),
                end.getColumn()
            ));
        }

        if (sig.size() > 6) {
            Token extra = (Token) sig.get(6);
            errors.add(new LexError(
                "FOR002",
                "La sentencia For contiene tokens adicionales no permitidos.",
                extra.getLine(),
                extra.getColumn()
            ));
        }

        return errors;
    }

    public List validateStructure(AnalysisResult ar) {
        List errors = new ArrayList<>();
        if (ar == null || ar.getLines() == null) return errors;

        Deque stack = new ArrayDeque<>();

        for (Object obj : ar.getLines()) {
            LineRecord lr = (LineRecord) obj;
            List sig = significantTokensStopAtComment(lr.getTokens());

            if (sig.isEmpty()) continue;

            Token first = (Token) sig.get(0);

            if (matchesWord(first, "For")) {
                if (!stack.isEmpty()) {
                    ((ForFrame) stack.peek()).hasExecutableLine = true;
                }
                stack.push(new ForFrame(first.getLine()));
                continue;
            }

            if (matchesWord(first, "Next")) {
                if (stack.isEmpty()) {
                    errors.add(new LexError(
                        "FOR006",
                        "Se encontró 'Next' sin un 'For' abierto.",
                        first.getLine(),
                        first.getColumn()
                    ));
                } else {
                    ForFrame frame = (ForFrame) stack.pop();
                    if (!frame.hasExecutableLine) {
                        errors.add(new LexError(
                            "FOR005",
                            "El bloque For no puede estar vacío ni contener solo comentarios.",
                            frame.line,
                            1
                        ));
                    }
                }
                continue;
            }

            if (!stack.isEmpty()) {
                ((ForFrame) stack.peek()).hasExecutableLine = true;
            }
        }

        while (!stack.isEmpty()) {
            ForFrame frame = (ForFrame) stack.pop();
            errors.add(new LexError(
                "FOR001",
                "Falta 'Next' para el 'For' iniciado.",
                frame.line,
                1
            ));
        }

        return errors;
    }

    private List significantTokensStopAtComment(List tokens) {
        List out = new ArrayList<>();
        if (tokens == null) return out;

        for (Object obj : tokens) {
            Token t = (Token) obj;
            if (t.getType() == TokenType.COMMENT) break;
            if (t.getType() == TokenType.WHITESPACE) continue;
            out.add(t);
        }

        return out;
    }

    private boolean matchesWord(Token t, String word) {
        return t != null
            && t.getLexeme() != null
            && t.getLexeme().equalsIgnoreCase(word)
            && (t.getType() == TokenType.KEYWORD || t.getType() == TokenType.IDENTIFIER);
    }
    
}
