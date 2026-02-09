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

import java.util.ArrayList;
import java.util.List;

import CRUnedCompiladresProyecto1.util.ErrorUtils;

/**
 *
 * @author Elias
 */
public class ValidationPipeline {
    
        private SymbolTable lastSymbolTable = new SymbolTable();

    public SymbolTable getLastSymbolTable() {
        return lastSymbolTable;
    }

    public List<LexError> validateAll(AnalysisResult ar) {
        List<LexError> errors = new ArrayList<>();
        lastSymbolTable = new SymbolTable();

        if (ar == null) return errors;

        // 1) Estructura
        errors.addAll(new ModuleValidator().validate(ar));
        errors.addAll(new EndModuleValidator().validate(ar));
        errors.addAll(new IfStructureValidator().validate(ar));
        errors.addAll(new SubMainStructureValidator().validate(ar));



        // 2) Dim (requiere saber si ya se vio Module)
        DimValidator dimValidator = new DimValidator();
        WriteLineValidator writeLineValidator = new WriteLineValidator();
        CommentValidator commentValidator = new CommentValidator();


        boolean moduleSeen = false;
        for (LineRecord lr : ar.getLines()) {
            
            errors.addAll(dimValidator.validate(lr, lastSymbolTable, moduleSeen));
            errors.addAll(writeLineValidator.validate(lr));
            errors.addAll(commentValidator.validate(lr));


            
            if (!moduleSeen && isModuleLine(lr)) {
                moduleSeen = true;
            }
        }

        return ErrorUtils.sortAndDedup(errors);
    }

    private boolean isModuleLine(LineRecord lr) {
        List<Token> tokens = lr.getTokens();
        if (tokens == null || tokens.isEmpty()) return false;

        // Ignorar líneas comentario
        if (tokens.size() == 1 && tokens.get(0).getType() == TokenType.COMMENT) return false;

        int idx = firstNonWhitespaceIndex(tokens);
        if (idx == -1) return false;

        Token first = tokens.get(idx);
        return first.getType() == TokenType.KEYWORD
                && first.getLexeme() != null
                && first.getLexeme().equalsIgnoreCase("Module");
    }

    private int firstNonWhitespaceIndex(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() != TokenType.WHITESPACE) return i;
        }
        return -1;
    }

    
}
