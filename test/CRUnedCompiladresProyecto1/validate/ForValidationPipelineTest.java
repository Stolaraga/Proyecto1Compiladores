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
public class ForValidationPipelineTest {
    
     @Test
    public void acceptsValidForBlock() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "For i = 1 To 5",
            "Console.WriteLine(\"Hola\")",
            "Next",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertFalse("No debería haber errores FOR en un For válido.", hasAnyForError(errors));
    }

    @Test
    public void reportsMissingTo() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "For i = 1 5",
            "Console.WriteLine(\"Hola\")",
            "Next",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar FOR002 cuando falta la palabra To.", hasCode(errors, "FOR002"));
    }

    @Test
    public void reportsMissingNext() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "For i = 1 To 5",
            "Console.WriteLine(\"Hola\")",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar FOR001 cuando falta Next.", hasCode(errors, "FOR001"));
    }

    @Test
    public void reportsNonIntegerStartLimit() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "For i = \"hola\" To 5",
            "Console.WriteLine(\"Hola\")",
            "Next",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar FOR003 cuando el límite inicial no es entero.", hasCode(errors, "FOR003"));
    }

    @Test
    public void reportsNonIntegerEndLimit() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "For i = 1 To \"hola\"",
            "Console.WriteLine(\"Hola\")",
            "Next",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar FOR004 cuando el límite final no es entero.", hasCode(errors, "FOR004"));
    }

    @Test
    public void reportsEmptyForBlock() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "For i = 1 To 5",
            "Next",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar FOR005 cuando el bloque For está vacío.", hasCode(errors, "FOR005"));
    }

    @Test
    public void reportsCommentOnlyForBlock() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "Module Program",
            "Sub Main()",
            "For i = 1 To 5",
            "' comentario 1",
            "' comentario 2",
            "Next",
            "End Sub",
            "End Module"
        ));

        List errors = new ValidationPipeline().validateAll(ar);

        assertTrue("Debe reportar FOR005 cuando el bloque For solo tiene comentarios.", hasCode(errors, "FOR005"));
    }

    private boolean hasAnyForError(List errors) {
        for (Object obj : errors) {
            LexError e = (LexError) obj;
            if (e.getCode() != null && e.getCode().startsWith("FOR")) {
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
    
}
