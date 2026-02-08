/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;

import org.junit.Ignore;

import CRUnedCompiladresProyecto1.lexer.Lexer;
import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;
import CRUnedCompiladresProyecto1.symbols.SymbolTable;
import CRUnedCompiladresProyecto1.symbols.VbType;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 *
 * @author Elias
 */


public class DimValidatorTest {
    
      // --- Helpers ---

    private static boolean isModuleLine(LineRecord lr) {
        List<Token> sig = significantTokens(lr.getTokens());
        if (sig.isEmpty()) return false;

        Token first = sig.get(0);
        return first.getType() == TokenType.KEYWORD
                && first.getLexeme() != null
                && first.getLexeme().equalsIgnoreCase("Module");
    }

    private static List<Token> significantTokens(List<Token> tokens) {
        List<Token> out = new ArrayList<>();
        for (Token t : tokens) {
            if (t.getType() == TokenType.WHITESPACE) continue;
            out.add(t);
        }
        return out;
    }

    /**
     * Ejecuta el DimValidator recorriendo el archivo en orden,
     * actualizando moduleSeen cuando aparece "Module ...".
     */
    private static List<LexError> runDimValidation(List<String> lines, SymbolTable st) {
        Lexer lexer = new Lexer();
        AnalysisResult result = lexer.analyze(lines);

        DimValidator validator = new DimValidator();
        List<LexError> errors = new ArrayList<>();

        boolean moduleSeen = false;
        for (LineRecord lr : result.getLines()) {
            errors.addAll(validator.validate(lr, st, moduleSeen));
            if (!moduleSeen && isModuleLine(lr)) {
                moduleSeen = true;
            }
        }
        return errors;
    }

    private static boolean hasCode(List<LexError> errors, String code) {
        for (LexError e : errors) {
            if (e.getCode().equals(code)) return true;
        }
        return false;
    }

    // --- Tests ---

    @Test
    public void validNumericDimAndUsesPreviousVariable() {
        List<String> lines = Arrays.asList(
                "Module M1",
                "Dim a As Integer = 10",
                "Dim b As Integer = a + 5"
        );

        SymbolTable st = new SymbolTable();
        List<LexError> errors = runDimValidation(lines, st);

        assertTrue("No deberían existir errores, pero salieron: " + errors, errors.isEmpty());
        assertEquals(2, st.size());
        assertEquals(VbType.INTEGER, st.getType("a"));
        assertEquals(VbType.INTEGER, st.getType("b"));
    }

    @Test
    public void errorWhenTypeIsNotAllowed() {
        List<String> lines = Arrays.asList(
                "Module M1",
                "Dim x As Money"
        );

        SymbolTable st = new SymbolTable();
        List<LexError> errors = runDimValidation(lines, st);

        assertTrue("Debe existir DIM007 (tipo no permitido). Errores: " + errors, hasCode(errors, "DIM007"));
    }

    @Test
    public void errorWhenUsingUndeclaredVariableInNumericExpr() {
        List<String> lines = Arrays.asList(
                "Module M1",
                "Dim a As Integer = z + 1"
        );

        SymbolTable st = new SymbolTable();
        List<LexError> errors = runDimValidation(lines, st);

        assertTrue("Debe existir DIM013 (variable no declarada). Errores: " + errors, hasCode(errors, "DIM013"));
    }

    @Test
    public void errorWhenDimAppearsBeforeModule() {
        List<String> lines = Arrays.asList(
                "Dim a As Integer",
                "Module M1"
        );

        SymbolTable st = new SymbolTable();
        List<LexError> errors = runDimValidation(lines, st);

        assertTrue("Debe existir DIM001 (Dim antes de Module). Errores: " + errors, hasCode(errors, "DIM001"));
    }

    @Test
    public void stringDoesNotAllowNumericOperations() {
        List<String> lines = Arrays.asList(
                "Module M1",
                "Dim s As String = 2 + 2"
        );

        SymbolTable st = new SymbolTable();
        List<LexError> errors = runDimValidation(lines, st);

        assertTrue("Debe existir DIM018 (no operaciones en String). Errores: " + errors, hasCode(errors, "DIM018"));
        assertTrue("Debe existir DIM019 (asignación String inválida). Errores: " + errors, hasCode(errors, "DIM019"));
    }
    
}
