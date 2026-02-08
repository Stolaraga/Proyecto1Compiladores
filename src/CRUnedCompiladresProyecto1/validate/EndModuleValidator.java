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
    
    public List<LexError> validate(AnalysisResult result) {
        List<LexError> errors = new ArrayList<>();

        if (result == null || result.getLines().isEmpty()) {
            errors.add(new LexError("ENDM000", "El archivo está vacío y no contiene 'End Module'.", 1, 1));
            return errors;
        }

        int lastSignificantLine = findLastSignificantLineNumber(result);
        boolean moduleSeen = false;

        int endModuleCount = 0;
        Token firstEndToken = null;
        int firstEndLine = -1;

        for (LineRecord lr : result.getLines()) {
            List<Token> tokens = lr.getTokens();

            if (isCommentLine(tokens) || isWhitespaceOnly(tokens)) continue;

            int firstNonWs = firstNonWhitespaceIndex(tokens);
            if (firstNonWs == -1) continue;

            Token first = tokens.get(firstNonWs);

            if (isKeyword(first, "Module")) {
                moduleSeen = true;
                continue;
            }

            if (isKeyword(first, "End")) {
                // ¿es End Module?
                if (looksLikeEndModule(tokens, firstNonWs)) {
                    endModuleCount++;
                    if (endModuleCount == 1) {
                        firstEndToken = first;
                        firstEndLine = lr.getLineNumber();
                    }

                    // Formato exacto: End + 1 espacio + Module y nada más (salvo whitespace final)
                    errors.addAll(validateEndModuleFormat(tokens, firstNonWs));

                    // Debe venir después de Module
                    if (!moduleSeen) {
                        errors.add(err("ENDM003", "'End Module' aparece antes de 'Module'.", first));
                    }

                    // Debe ser la última línea significativa
                    if (lr.getLineNumber() != lastSignificantLine) {
                        errors.add(err("ENDM002", "'End Module' debe ser la última línea significativa del archivo.", first));
                    }
                }
            }
        }

        if (endModuleCount == 0) {
            errors.add(new LexError("ENDM001", "No se encontró la sentencia 'End Module' en el archivo.", 1, 1));
        } else if (endModuleCount > 1 && firstEndToken != null) {
            errors.add(new LexError("ENDM004", "Solo se permite un 'End Module' en el archivo.", firstEndLine, firstEndToken.getColumn()));
        }

        return errors;
    }

    // ------------------- helpers -------------------

    private int findLastSignificantLineNumber(AnalysisResult result) {
        List<LineRecord> lines = result.getLines();
        for (int i = lines.size() - 1; i >= 0; i--) {
            List<Token> tokens = lines.get(i).getTokens();
            if (isCommentLine(tokens)) continue;
            if (isWhitespaceOnly(tokens)) continue;
            return lines.get(i).getLineNumber();
        }
        return 1;
    }

    private boolean isCommentLine(List<Token> tokens) {
        return tokens != null && tokens.size() == 1 && tokens.get(0).getType() == TokenType.COMMENT;
    }

    private boolean isWhitespaceOnly(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) return true;
        for (Token t : tokens) {
            if (t.getType() != TokenType.WHITESPACE) return false;
        }
        return true;
    }

    private int firstNonWhitespaceIndex(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() != TokenType.WHITESPACE) return i;
        }
        return -1;
    }

    private boolean isKeyword(Token t, String kw) {
        return t.getType() == TokenType.KEYWORD
                && t.getLexeme() != null
                && t.getLexeme().equalsIgnoreCase(kw);
    }

    /**
     * Revisa si la línea se parece a End Module (sin validar formato exacto).
     * Sirve para reconocerlo y luego validar detalles.
     */
    private boolean looksLikeEndModule(List<Token> tokens, int endIndex) {
        int i = endIndex + 1;
        // Puede haber whitespace, pero la forma exacta se valida en validateEndModuleFormat
        // Aquí solo verificamos que exista "Module" más adelante
        while (i < tokens.size() && tokens.get(i).getType() == TokenType.WHITESPACE) i++;
        if (i >= tokens.size()) return false;
        Token t = tokens.get(i);
        return isKeyword(t, "Module");
    }

    private List<LexError> validateEndModuleFormat(List<Token> tokens, int endIndex) {
        List<LexError> errors = new ArrayList<>();

        // Esperado: End + WHITESPACE (" ") + Module + (solo whitespace)
        int i = endIndex + 1;
        if (i >= tokens.size()) {
            errors.add(err("ENDM010", "Formato inválido. Se esperaba: End Module.", tokens.get(endIndex)));
            return errors;
        }

        Token ws = tokens.get(i);
        if (ws.getType() != TokenType.WHITESPACE) {
            errors.add(err("ENDM011", "Se esperaba 1 espacio entre 'End' y 'Module'.", ws));
            return errors;
        }
        if (!" ".equals(ws.getLexeme())) {
            errors.add(err("ENDM012", "Debe haber exactamente 1 espacio (no tab ni múltiples) entre 'End' y 'Module'.", ws));
        }

        i++;
        if (i >= tokens.size()) {
            errors.add(err("ENDM010", "Formato inválido. Se esperaba: End Module.", tokens.get(endIndex)));
            return errors;
        }

        Token mod = tokens.get(i);
        if (!isKeyword(mod, "Module")) {
            errors.add(err("ENDM013", "Después de 'End' debe venir 'Module'.", mod));
            return errors;
        }

        // no debe haber nada después excepto whitespace
        for (int j = i + 1; j < tokens.size(); j++) {
            Token t = tokens.get(j);
            if (t.getType() == TokenType.WHITESPACE) continue;
            errors.add(err("ENDM014", "Texto extra no permitido después de 'End Module'.", t));
            break;
        }

        return errors;
    }

    private LexError err(String code, String msg, Token at) {
        return new LexError(code, msg, at.getLine(), at.getColumn());
    }

    
}
