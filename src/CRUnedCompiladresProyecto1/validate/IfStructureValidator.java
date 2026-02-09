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
public class IfStructureValidator {
    
       // Stack para IFs abiertos
    private static class IfFrame {
        final int line;
        boolean elseSeen = false;

        IfFrame(int line) { this.line = line; }
    }

    public List<LexError> validate(AnalysisResult ar) {
        List<LexError> errors = new ArrayList<>();
        if (ar == null || ar.getLines() == null) return errors;

        Deque<IfFrame> stack = new ArrayDeque<>();

        for (LineRecord lr : ar.getLines()) {
            List<Token> sig = significantTokensStopAtComment(lr.getTokens());
            if (sig.isEmpty()) continue;

            Token first = sig.get(0);

            // IF ... THEN
            if (isKeyword(first, "If")) {
                // Debe contener Then en la misma línea
                if (!containsKeyword(sig, "Then")) {
                    errors.add(new LexError("IF002", "La sentencia If debe contener 'Then' en la misma línea.", first.getLine(), first.getColumn()));
                }
                stack.push(new IfFrame(first.getLine()));
                continue;
            }

            // ELSEIF
            if (isKeyword(first, "ElseIf")) {
                if (stack.isEmpty()) {
                    errors.add(new LexError("IF010", "No se permite 'ElseIf' sin un 'If' previo.", first.getLine(), first.getColumn()));
                } else if (stack.peek().elseSeen) {
                    errors.add(new LexError("IF011", "'ElseIf' no puede aparecer después de 'Else' en el mismo If.", first.getLine(), first.getColumn()));
                } else {
                    // Debe contener Then
                    if (!containsKeyword(sig, "Then")) {
                        errors.add(new LexError("IF003", "La sentencia ElseIf debe contener 'Then' en la misma línea.", first.getLine(), first.getColumn()));
                    }
                }
                continue;
            }

            // ELSE
            if (isKeyword(first, "Else")) {
                if (stack.isEmpty()) {
                    errors.add(new LexError("IF012", "No se permite 'Else' sin un 'If' previo.", first.getLine(), first.getColumn()));
                } else if (stack.peek().elseSeen) {
                    errors.add(new LexError("IF013", "Solo se permite un 'Else' por cada If.", first.getLine(), first.getColumn()));
                } else {
                    stack.peek().elseSeen = true;
                }
                continue;
            }

            // END IF (dos keywords: End + If)
            if (isKeyword(first, "End") && sig.size() >= 2 && isKeyword(sig.get(1), "If")) {
                if (stack.isEmpty()) {
                    errors.add(new LexError("IF020", "Se encontró 'End If' sin un 'If' abierto.", first.getLine(), first.getColumn()));
                } else {
                    stack.pop();
                }
            }
        }

        // Ifs no cerrados
        while (!stack.isEmpty()) {
            IfFrame f = stack.pop();
            errors.add(new LexError("IF001", "Falta 'End If' para el 'If' iniciado.", f.line, 1));
        }

        return errors;
    }

    private List<Token> significantTokensStopAtComment(List<Token> tokens) {
        List<Token> out = new ArrayList<>();
        if (tokens == null) return out;
        for (Token t : tokens) {
            if (t.getType() == TokenType.COMMENT) break;
            if (t.getType() == TokenType.WHITESPACE) continue;
            out.add(t);
        }
        return out;
    }

    private boolean isKeyword(Token t, String kw) {
        return t.getType() == TokenType.KEYWORD && t.getLexeme() != null && t.getLexeme().equalsIgnoreCase(kw);
    }

    private boolean containsKeyword(List<Token> sig, String kw) {
        for (Token t : sig) {
            if (isKeyword(t, kw)) return true;
        }
        return false;
    }
    
    
}
