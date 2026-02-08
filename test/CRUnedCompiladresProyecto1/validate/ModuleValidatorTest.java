/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;


import CRUnedCompiladresProyecto1.lexer.Lexer;
import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 *
 * @author Elias
 */


public class ModuleValidatorTest {
    
    
    private boolean hasCode(List<LexError> errors, String code) {
        for (LexError e : errors) {
            if (e.getCode().equals(code)) return true;
        }
        return false;
    }

    @Test
    public void validImportsThenModule_noErrors() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Imports System",
                "Module M1"
        ));

        ModuleValidator v = new ModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("No deberían haber errores: " + errors, errors.isEmpty());
    }

    @Test
    public void importsAfterModule_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Module M1",
                "Imports System"
        ));

        ModuleValidator v = new ModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("Debe existir MOD002: " + errors, hasCode(errors, "MOD002"));
    }

    @Test
    public void moduleWithTwoSpaces_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Module  M1"
        ));

        ModuleValidator v = new ModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("Debe existir MOD012: " + errors, hasCode(errors, "MOD012"));
    }

    @Test
    public void missingModule_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Imports System"
        ));

        ModuleValidator v = new ModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("Debe existir MOD001: " + errors, hasCode(errors, "MOD001"));
    }

    
    
}
