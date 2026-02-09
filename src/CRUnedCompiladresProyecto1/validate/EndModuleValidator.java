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
public class EndModuleValidator {

    public List<LexError> validate(AnalysisResult ar) {
        List<LexError> errors = new ArrayList<>();

        if (ar == null || ar.getLines() == null || ar.getLines().isEmpty()) {
            errors.add(new LexError("ENDM000", "El archivo está vacío y no contiene 'End Module'.", 1, 1));
            return errors;
        }

        int totalLines = ar.getLines().size();

        int endModuleCount = 0;
        LineRecord lastEndModule = null;

        // Para ENDM003: End Module antes de Module
        int firstModuleLine = -1;

        for (LineRecord lr : ar.getLines()) {
            List<Token> tokens = lr.getTokens();
            if (tokens == null || tokens.isEmpty()) continue;

            // detectar Module (primer no whitespace)
            if (firstModuleLine == -1 && isModuleLine(tokens)) {
                firstModuleLine = lr.getLineNumber();
            }

            // detectar End Module
            if (isEndModuleCandidate(tokens)) {
                endModuleCount++;
                lastEndModule = lr;

                // Validación exacta de la línea End Module (espacios / extra tokens)
                errors.addAll(validateEndModuleLine(tokens));

                // ENDM003: si todavía no apareció Module, o Module está después
                if (firstModuleLine == -1 || lr.getLineNumber() < firstModuleLine) {
                    errors.add(new LexError(
                            "ENDM003",
                            "'End Module' debe aparecer después de 'Module'.",
                            lr.getLineNumber(),
                            1
                    ));
                }
            }
        }

        if (endModuleCount == 0) {
            errors.add(new LexError("ENDM001", "No se encontró la sentencia 'End Module' en el archivo.", totalLines, 1));
            return errors;
        }

        if (endModuleCount > 1 && lastEndModule != null) {
            errors.add(new LexError("ENDM005", "Solo se permite un 'End Module' en el archivo.", lastEndModule.getLineNumber(), 1));
        }

        // Debe ser literalmente la última línea del archivo (incluye líneas en blanco)
        if (lastEndModule != null && lastEndModule.getLineNumber() != totalLines) {
            errors.add(new LexError(
                    "ENDM002",
                    "'End Module' debe ser la última línea del archivo (no se permiten líneas posteriores, ni vacías).",
                    lastEndModule.getLineNumber(),
                    1
            ));
        }

        return errors;
    }

    private boolean isModuleLine(List<Token> tokens) {
        int idx = firstNonWhitespaceIndex(tokens);
        if (idx == -1) return false;
        Token first = tokens.get(idx);
        return first.getType() == TokenType.KEYWORD
                && first.getLexeme() != null
                && first.getLexeme().equalsIgnoreCase("Module");
    }

    private boolean isEndModuleCandidate(List<Token> tokens) {
        List<Token> sig = significantTokens(tokens);

        if (sig.size() < 2) return false;
        return isKeyword(sig.get(0), "End") && isKeyword(sig.get(1), "Module");
    }

    private List<LexError> validateEndModuleLine(List<Token> tokens) {
        List<LexError> errors = new ArrayList<>();

        int idx = firstNonWhitespaceIndex(tokens);
        if (idx == -1) return errors;

        Token endTok = tokens.get(idx);

        // Esperado: End [WS " "] Module [WS opcional] (y nada más)
        int i = idx + 1;
        if (i >= tokens.size()) {
            errors.add(err("ENDM010", "Sentencia 'End Module' incompleta: falta 'Module'.", endTok));
            return errors;
        }

        Token ws = tokens.get(i);
        if (ws.getType() != TokenType.WHITESPACE) {
            errors.add(err("ENDM011", "Se esperaba exactamente 1 espacio entre 'End' y 'Module'.", ws));
            return errors;
        }
        if (!" ".equals(ws.getLexeme())) {
            errors.add(err("ENDM012", "Debe haber exactamente 1 espacio (no tab ni múltiples espacios) entre 'End' y 'Module'.", ws));
        }

        i++;
        if (i >= tokens.size()) {
            errors.add(err("ENDM010", "Sentencia 'End Module' incompleta: falta 'Module'.", endTok));
            return errors;
        }

        Token modTok = tokens.get(i);
        if (!isKeyword(modTok, "Module")) {
            errors.add(err("ENDM013", "Se esperaba la palabra reservada 'Module' después de 'End'.", modTok));
        }

        // Después de Module: solo whitespace permitido (ni COMMENT, ni otra instrucción)
        for (int j = i + 1; j < tokens.size(); j++) {
            Token t = tokens.get(j);
            if (t.getType() == TokenType.WHITESPACE) continue;
            errors.add(err("ENDM014", "No se permite ninguna instrucción adicional en la misma línea de 'End Module'.", t));
            break;
        }

        return errors;
    }

    private List<Token> significantTokens(List<Token> tokens) {
        List<Token> out = new ArrayList<>();
        for (Token t : tokens) {
            if (t.getType() == TokenType.WHITESPACE) continue;
            out.add(t);
        }
        return out;
    }

    private int firstNonWhitespaceIndex(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() != TokenType.WHITESPACE) return i;
        }
        return -1;
    }

    private boolean isKeyword(Token t, String kw) {
        return t.getType() == TokenType.KEYWORD && t.getLexeme() != null && t.getLexeme().equalsIgnoreCase(kw);
    }

    private LexError err(String code, String msg, Token at) {
        return new LexError(code, msg, at.getLine(), at.getColumn());
    }
}

