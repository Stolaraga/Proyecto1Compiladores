/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;

import CRUnedCompiladresProyecto1.lexer.Lexer;
import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Elias
 */
public class WhileValidationPipelineTest {
    
    @Test
    public void acceptsValidWhileBlock() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "Dim x As Integer = 1",
            "While x < 10",
            "Console.WriteLine(\"Hola\")",
            "End While",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertFalse("No debería haber errores WHI en un While válido.", hasAnyWhileError(errors));
    }

    @Test
    public void reportsMissingEndWhile() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "Dim x As Integer = 1",
            "While x < 10",
            "Console.WriteLine(\"Hola\")",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar WHI001 cuando falta End While.", hasCode(errors, "WHI001"));
    }

    @Test
    public void reportsEmptyWhileBlock() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "Dim x As Integer = 1",
            "While x < 10",
            "End While",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar WHI021 cuando el bloque While está vacío.", hasCode(errors, "WHI021"));
    }

    @Test
    public void reportsCommentOnlyWhileBlock() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "Dim x As Integer = 1",
            "While x < 10",
            "' comentario 1",
            "' comentario 2",
            "End While",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar WHI021 cuando el bloque While solo tiene comentarios.", hasCode(errors, "WHI021"));
    }

    private boolean hasAnyWhileError(List errors) {
        for (Object obj : errors) {
            LexError e = (LexError) obj;
            if (e.getCode() != null && e.getCode().startsWith("WHI")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCode(List errors, String code) {
        for (Object obj : errors) {
            LexError e = (LexError) obj;
            if (code.equals(e.getCode())) {
                return true;
            }
        }
        return false;
    }
    
    
    @Test
    public void reportsEndWhileWithoutWhile() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "End While",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar WHI020 cuando aparece End While sin While.",
                hasCode(errors, "WHI020"));
    }

    
    
}
