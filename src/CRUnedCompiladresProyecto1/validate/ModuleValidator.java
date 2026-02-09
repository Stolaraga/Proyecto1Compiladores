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
public class ModuleValidator {
    
    public List<LexError> validate(AnalysisResult result) {
        List<LexError> errors = new ArrayList<>();

        if (result == null || result.getLines().isEmpty()) {
            errors.add(new LexError("MOD000", "El archivo está vacío y no contiene 'Module'.", 1, 1));
            return errors;
        }

        boolean moduleSeen = false;
        int moduleCount = 0;

        for (LineRecord lr : result.getLines()) {
            List<Token> tokens = lr.getTokens();
            if (tokens.isEmpty()) continue;

            // Comentario (si línea inicia con ')
            if (tokens.size() == 1 && tokens.get(0).getType() == TokenType.COMMENT) continue;

            int firstNonWs = firstNonWhitespaceIndex(tokens);
            if (firstNonWs == -1) continue; // línea solo whitespace

            Token first = tokens.get(firstNonWs);

            // Imports
            if (isKeyword(first, "Imports")) {
                if (moduleSeen) {
                    errors.add(err("MOD002", "No se permite 'Imports' después de 'Module'.", first));
                } else {
                    // Validación mínima: Imports + 1 espacio + algo (no vacío)
                    errors.addAll(validateImportsLine(tokens, firstNonWs));
                }
                continue;
            }

            // Module
            if (isKeyword(first, "Module")) {
                moduleCount++;

                // Sangría antes de Module
                if (firstNonWs > 0) {
                    Token ws = tokens.get(0);
                    if (ws.getType() == TokenType.WHITESPACE) {
                        errors.add(err("MOD003", "La línea 'Module' no debe tener sangría al inicio.", ws));
                    } else {
                        errors.add(err("MOD003", "La línea 'Module' no debe tener caracteres antes de 'Module'.", first));
                    }
                }

                if (moduleSeen) {
                    errors.add(err("MOD004", "Solo se permite un 'Module' en el archivo.", first));
                } else {
                    moduleSeen = true;
                }

                errors.addAll(validateModuleLine(tokens, firstNonWs));
            }
        }

        if (!moduleSeen) {
            errors.add(new LexError("MOD001", "No se encontró la sentencia 'Module <id>' en el archivo.", 1, 1));
        }

        return errors;
    }

    // -------- Validaciones específicas --------

    private List<LexError> validateModuleLine(List<Token> tokens, int moduleIndex) {
        List<LexError> errors = new ArrayList<>();

        // Esperado: Module [WS " "] Identifier [WS opcional]
        int i = moduleIndex + 1;
        if (i >= tokens.size()) {
            errors.add(err("MOD010", "Falta el identificador después de 'Module'.", tokens.get(moduleIndex)));
            return errors;
        }

        // Debe existir whitespace exacto de 1 espacio
        Token ws = tokens.get(i);
        if (ws.getType() != TokenType.WHITESPACE) {
            errors.add(err("MOD011", "Se esperaba 1 espacio entre 'Module' y el identificador.", ws));
            return errors;
        }
        if (!" ".equals(ws.getLexeme())) {
            errors.add(err("MOD012", "Debe haber exactamente 1 espacio (no tab ni múltiples espacios) entre 'Module' y el identificador.", ws));
        }

        i++;
        if (i >= tokens.size()) {
            errors.add(err("MOD010", "Falta el identificador después de 'Module'.", tokens.get(moduleIndex)));
            return errors;
        }

        Token id = tokens.get(i);

        IdentifierValidator idValidator = new IdentifierValidator();
        errors.addAll(idValidator.validateIdentifier(id));

        if (id.getType() != TokenType.IDENTIFIER) {
            errors.add(err("MOD013", "Identificador de Module inválido: '" + id.getLexeme() + "'.", id));
        }


        // Tokens después del identificador: solo permitimos whitespace (o nada)
        for (int j = i + 1; j < tokens.size(); j++) {
            Token t = tokens.get(j);
            if (t.getType() == TokenType.WHITESPACE) continue;
            errors.add(err("MOD014", "Texto extra no permitido después del identificador del Module.", t));
            break;
        }

        return errors;
    }

    private List<LexError> validateImportsLine(List<Token> tokens, int importsIndex) {
        List<LexError> errors = new ArrayList<>();

        // Esperado mínimo: Imports + WS + algo (IDENTIFIER o KEYWORD o IDENTIFIER '.' IDENTIFIER ...)
        int i = importsIndex + 1;
        if (i >= tokens.size()) {
            errors.add(err("IMP010", "Falta el destino de Imports (ej: Imports System).", tokens.get(importsIndex)));
            return errors;
        }

        Token ws = tokens.get(i);
        if (ws.getType() != TokenType.WHITESPACE) {
            errors.add(err("IMP011", "Se esperaba 1 espacio después de 'Imports'.", ws));
            return errors;
        }
        if (!" ".equals(ws.getLexeme())) {
            errors.add(err("IMP012", "Debe haber exactamente 1 espacio (no tab ni múltiples espacios) después de 'Imports'.", ws));
        }

        // Verificar que haya algo después
        i++;
        boolean hasSomething = false;
        for (int j = i; j < tokens.size(); j++) {
            Token t = tokens.get(j);
            if (t.getType() == TokenType.WHITESPACE) continue;
            hasSomething = true;
            break;
        }

        if (!hasSomething) {
            errors.add(err("IMP010", "Falta el destino de Imports (ej: Imports System).", tokens.get(importsIndex)));
        }

        return errors;
    }

    // -------- Helpers --------

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
