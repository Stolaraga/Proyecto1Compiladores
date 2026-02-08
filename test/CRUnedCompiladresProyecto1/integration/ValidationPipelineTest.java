/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.integration;


import CRUnedCompiladresProyecto1.lexer.Lexer;
import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.symbols.SymbolTable;
import CRUnedCompiladresProyecto1.symbols.VbType;
import CRUnedCompiladresProyecto1.validate.ValidationPipeline;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;




/**
 *
 * @author Elias
 */
public class ValidationPipelineTest {
    
     private boolean hasCode(List<LexError> errors, String code) {
        for (LexError e : errors) {
            if (e.getCode().equals(code)) return true;
        }
        return false;
    }

    @Test
    public void validFile_noErrors_andSymbolTableFilled() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Imports System",
                "Module M1",
                "Dim a As Integer = 10",
                "Dim b As Integer = a + 5",
                "End Module"
        ));

        ValidationPipeline p = new ValidationPipeline();
        List<LexError> errors = p.validateAll(ar);

        assertTrue("No deberían haber errores: " + errors, errors.isEmpty());

        SymbolTable st = p.getLastSymbolTable();
        assertEquals(VbType.INTEGER, st.getType("a"));
        assertEquals(VbType.INTEGER, st.getType("b"));
    }

    @Test
    public void collectsMultipleErrorsFromDifferentValidators() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Dim a As Integer",   // Dim antes de Module -> DIM001
                "Module  M1",         // dos espacios -> MOD012
                "Imports System"      // Imports después de Module -> MOD002
                // falta End Module -> ENDM001
        ));

        ValidationPipeline p = new ValidationPipeline();
        List<LexError> errors = p.validateAll(ar);

        assertTrue("Debe existir DIM001: " + errors, hasCode(errors, "DIM001"));
        assertTrue("Debe existir MOD012: " + errors, hasCode(errors, "MOD012"));
        assertTrue("Debe existir MOD002: " + errors, hasCode(errors, "MOD002"));
        assertTrue("Debe existir ENDM001: " + errors, hasCode(errors, "ENDM001"));
    }
    
    
    
}
