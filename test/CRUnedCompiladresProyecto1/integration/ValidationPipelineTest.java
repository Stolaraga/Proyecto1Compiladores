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
    
        @Test
    public void pipelineReturnsErrorsSortedByLineThenColumnThenCode() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "Dim _a As Integer",     // línea 1 -> ID002 (y DIM001 también por antes de Module)
                "Module  M1",            // línea 2 -> MOD012 (dos espacios)
                "Console.WriteLine()",   // línea 3 -> CWL004 (vacío)
                "End  Module"            // línea 4 -> ENDM012 (dos espacios)
        ));

        ValidationPipeline p = new ValidationPipeline();
        List<LexError> errors = p.validateAll(ar);

        assertTrue("Se esperaban errores", errors.size() > 0);

        // Verifica que están ordenados:
        for (int i = 1; i < errors.size(); i++) {
            LexError prev = errors.get(i - 1);
            LexError cur = errors.get(i);

            boolean ok =
                    (prev.getLine() < cur.getLine()) ||
                    (prev.getLine() == cur.getLine() && prev.getColumn() < cur.getColumn()) ||
                    (prev.getLine() == cur.getLine() && prev.getColumn() == cur.getColumn()
                            && prev.getCode().compareTo(cur.getCode()) <= 0);

            assertTrue("Lista no está ordenada entre:\n  " + prev + "\n  " + cur, ok);
        }
    }
    
    
    
}
