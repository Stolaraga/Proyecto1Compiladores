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
public class SubMainStructureValidatorTest {
    
      @Test
    public void missingEndSub_isError_SUB002() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Imports System",
                "Module M1",
                "Sub Main()",
                "End Module"
        ));

        List<LexError> errors = new SubMainStructureValidator().validate(ar);
        assertTrue(hasCode(errors, "SUB002"));
    }

    @Test
    public void endSubWithoutSub_isError_SUB020() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Imports System",
                "Module M1",
                "End Sub",
                "End Module"
        ));

        List<LexError> errors = new SubMainStructureValidator().validate(ar);
        assertTrue(hasCode(errors, "SUB020"));
    }

    @Test
    public void duplicateSubMain_isError_SUB004() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Imports System",
                "Module M1",
                "Sub Main()",
                "End Sub",
                "Sub Main()",
                "End Sub",
                "End Module"
        ));

        List<LexError> errors = new SubMainStructureValidator().validate(ar);
        assertTrue(hasCode(errors, "SUB004"));
    }

    private boolean hasCode(List<LexError> errors, String code) {
        for (LexError e : errors) {
            if (code.equals(e.getCode())) return true;
        }
        return false;
    }
    
}
