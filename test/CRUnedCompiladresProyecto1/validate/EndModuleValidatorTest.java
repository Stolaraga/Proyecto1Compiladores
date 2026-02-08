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
public class EndModuleValidatorTest {
    
        private boolean hasCode(List<LexError> errors, String code) {
        for (LexError e : errors) {
            if (e.getCode().equals(code)) return true;
        }
        return false;
    }

    @Test
    public void validEndModule_lastSignificantLine_noErrors() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Imports System",
                "Module M1",
                "Dim a As Integer = 10",
                "End Module"
        ));

        EndModuleValidator v = new EndModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("No deberían haber errores: " + errors, errors.isEmpty());
    }

    @Test
    public void missingEndModule_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Module M1",
                "Dim a As Integer"
        ));

        EndModuleValidator v = new EndModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("Debe existir ENDM001: " + errors, hasCode(errors, "ENDM001"));
    }

    @Test
    public void endModuleNotLastSignificant_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Module M1",
                "End Module",
                "Dim a As Integer"
        ));

        EndModuleValidator v = new EndModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("Debe existir ENDM002: " + errors, hasCode(errors, "ENDM002"));
    }

    @Test
    public void endModuleBeforeModule_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "End Module",
                "Module M1"
        ));

        EndModuleValidator v = new EndModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("Debe existir ENDM003: " + errors, hasCode(errors, "ENDM003"));
    }

    @Test
    public void endModuleWithTwoSpaces_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Module M1",
                "End  Module"
        ));

        EndModuleValidator v = new EndModuleValidator();
        List<LexError> errors = v.validate(ar);

        assertTrue("Debe existir ENDM012: " + errors, hasCode(errors, "ENDM012"));
    }

    
}
