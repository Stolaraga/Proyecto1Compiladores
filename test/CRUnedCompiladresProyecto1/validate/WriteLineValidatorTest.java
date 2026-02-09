package CRUnedCompiladresProyecto1.validate;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Elias
 */

import CRUnedCompiladresProyecto1.lexer.Lexer;
import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.validate.WriteLineValidator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class WriteLineValidatorTest {
    
     private boolean hasCode(List<LexError> errors, String code) {
        for (LexError e : errors) {
            if (e.getCode().equals(code)) return true;
        }
        return false;
    }

    @Test
    public void validConsoleWriteLine_noErrors() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Console.WriteLine(\"Hola\")"
        ));

        LineRecord lr = ar.getLine(1);
        WriteLineValidator v = new WriteLineValidator();
        List<LexError> errors = v.validate(lr);

        assertTrue("No deberían haber errores: " + errors, errors.isEmpty());
    }

    @Test
    public void emptyParentheses_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Console.WriteLine()"
        ));

        WriteLineValidator v = new WriteLineValidator();
        List<LexError> errors = v.validate(ar.getLine(1));

        assertTrue("Debe existir CWL004: " + errors, hasCode(errors, "CWL004"));
    }

    @Test
    public void missingClosingParen_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Console.WriteLine(\"Hola\""
        ));

        WriteLineValidator v = new WriteLineValidator();
        List<LexError> errors = v.validate(ar.getLine(1));

        assertTrue("Debe existir CWL003: " + errors, hasCode(errors, "CWL003"));
    }

    @Test
    public void stringWithoutClosingQuote_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Console.WriteLine(\"Hola)"
        ));

        WriteLineValidator v = new WriteLineValidator();
        List<LexError> errors = v.validate(ar.getLine(1));

        assertTrue("Debe existir CWL005: " + errors, hasCode(errors, "CWL005"));
        assertTrue("Debe existir CWL003 (falta ')'): " + errors, hasCode(errors, "CWL003"));
    }

    @Test
    public void extraCodeAfterParen_isError() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Console.WriteLine(\"Hola\") Dim x As Integer"
        ));

        WriteLineValidator v = new WriteLineValidator();
        List<LexError> errors = v.validate(ar.getLine(1));

        assertTrue("Debe existir CWL006: " + errors, hasCode(errors, "CWL006"));
    }

    @Test
    public void nonWriteLineLine_isIgnored() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Dim a As Integer = 10"
        ));

        WriteLineValidator v = new WriteLineValidator();
        List<LexError> errors = v.validate(ar.getLine(1));

        assertTrue("No debería validar nada si no es WriteLine: " + errors, errors.isEmpty());
    }
    
}
