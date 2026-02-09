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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class SubMainStructureValidator {
    
     public List<LexError> validate(AnalysisResult ar) {
        List<LexError> errors = new ArrayList<>();
        if (ar == null || ar.getLines() == null || ar.getLines().isEmpty()) return errors;

        int moduleLine = -1;
        int endModuleLine = -1;

        boolean subOpen = false;
        int subMainCount = 0;
        int subMainLine = -1;

        for (LineRecord lr : ar.getLines()) {
            List<Token> sig = significantTokensStopAtComment(lr.getTokens());
            if (sig.isEmpty()) continue;

            // ubicar Module / End Module para validar posición
            if (moduleLine == -1 && isModuleLine(sig)) {
                moduleLine = lr.getLineNumber();
            }
            if (endModuleLine == -1 && isEndModuleLine(sig)) {
                endModuleLine = lr.getLineNumber();
            }

            // Sub Main()
            if (isSubMainLine(sig)) {
                subMainCount++;
                subMainLine = lr.getLineNumber();

                // Debe estar después de Module y antes de End Module (si existen)
                if (moduleLine == -1 || subMainLine < moduleLine) {
                    errors.add(new LexError("SUB001", "'Sub Main()' debe aparecer después de 'Module'.", subMainLine, 1));
                }
                if (endModuleLine != -1 && subMainLine > endModuleLine) {
                    errors.add(new LexError("SUB003", "'Sub Main()' debe aparecer antes de 'End Module'.", subMainLine, 1));
                }

                if (subMainCount > 1) {
                    errors.add(new LexError("SUB004", "Solo se permite un 'Sub Main()' en el archivo.", subMainLine, 1));
                }

                // Validar firma exacta mínima: Sub Main ( ) y nada extra
                errors.addAll(validateSubMainSignature(sig));

                // Abrimos bloque
                subOpen = true;
                continue;
            }

            // End Sub
            if (isEndSubLine(sig)) {
                if (!subOpen) {
                    errors.add(new LexError("SUB020", "Se encontró 'End Sub' sin un 'Sub Main()' abierto.", lr.getLineNumber(), 1));
                } else {
                    subOpen = false;
                }
            }
        }

        if (subMainCount == 0) {
            errors.add(new LexError("SUB000", "No se encontró la sentencia 'Sub Main()' en el archivo.", 1, 1));
        }

        if (subOpen) {
            int line = (subMainLine != -1) ? subMainLine : 1;
            errors.add(new LexError("SUB002", "Falta 'End Sub' para el 'Sub Main()' iniciado.", line, 1));
        }

        return errors;
    }

    private List<LexError> validateSubMainSignature(List<Token> sig) {
        List<LexError> errors = new ArrayList<>();

        // Esperado (significativo): Sub, Main, (, )
        // Permitimos solo eso (nada extra)
        if (sig.size() < 4) {
            errors.add(new LexError("SUB010", "Firma inválida. Formato esperado: Sub Main()", sig.get(0).getLine(), sig.get(0).getColumn()));
            return errors;
        }

        Token sub = sig.get(0);
        Token main = sig.get(1);
        Token p1 = sig.get(2);
        Token p2 = sig.get(3);

        if (!(sub.getType() == TokenType.KEYWORD && "Sub".equalsIgnoreCase(sub.getLexeme()))) {
            errors.add(new LexError("SUB011", "Se esperaba la palabra reservada 'Sub'.", sub.getLine(), sub.getColumn()));
        }

        if (!(main.getType() == TokenType.IDENTIFIER && "Main".equalsIgnoreCase(main.getLexeme()))) {
            errors.add(new LexError("SUB012", "Se esperaba 'Main' después de 'Sub'.", main.getLine(), main.getColumn()));
        }

        if (!(p1.getType() == TokenType.PUNCTUATION && "(".equals(p1.getLexeme()))) {
            errors.add(new LexError("SUB013", "Se esperaba '(' en 'Sub Main()'.", p1.getLine(), p1.getColumn()));
        }

        if (!(p2.getType() == TokenType.PUNCTUATION && ")".equals(p2.getLexeme()))) {
            errors.add(new LexError("SUB014", "Se esperaba ')' en 'Sub Main()'.", p2.getLine(), p2.getColumn()));
        }

        if (sig.size() > 4) {
            Token extra = sig.get(4);
            errors.add(new LexError("SUB015", "Texto extra no permitido en la línea de 'Sub Main()'.", extra.getLine(), extra.getColumn()));
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

    private boolean isModuleLine(List<Token> sig) {
        return sig.size() >= 1
                && sig.get(0).getType() == TokenType.KEYWORD
                && "Module".equalsIgnoreCase(sig.get(0).getLexeme());
    }

    private boolean isEndModuleLine(List<Token> sig) {
        return sig.size() >= 2
                && sig.get(0).getType() == TokenType.KEYWORD
                && "End".equalsIgnoreCase(sig.get(0).getLexeme())
                && sig.get(1).getType() == TokenType.KEYWORD
                && "Module".equalsIgnoreCase(sig.get(1).getLexeme());
    }

    private boolean isSubMainLine(List<Token> sig) {
        return sig.size() >= 2
                && sig.get(0).getType() == TokenType.KEYWORD
                && "Sub".equalsIgnoreCase(sig.get(0).getLexeme())
                && sig.get(1).getType() == TokenType.IDENTIFIER
                && "Main".equalsIgnoreCase(sig.get(1).getLexeme());
    }

    private boolean isEndSubLine(List<Token> sig) {
        return sig.size() >= 2
                && sig.get(0).getType() == TokenType.KEYWORD
                && "End".equalsIgnoreCase(sig.get(0).getLexeme())
                && sig.get(1).getType() == TokenType.KEYWORD
                && "Sub".equalsIgnoreCase(sig.get(1).getLexeme());
    }
    
}
