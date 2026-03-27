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

public class IfStructureValidatorTest {

    @Test
    public void acceptsValidIfWithElse() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "If x Then",
            "Console.WriteLine(\"Hola\")",
            "Else",
            "Console.WriteLine(\"Adios\")",
            "End If"
        ));

        List errors = new IfStructureValidator().validate(ar);

        assertFalse(hasCode(errors, "IF001"));
        assertFalse(hasCode(errors, "IF002"));
        assertFalse(hasCode(errors, "IF004"));
        assertFalse(hasCode(errors, "IF020"));
    }

    @Test
    public void ifWithoutThenRaisesIF002() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "If x",
            "Console.WriteLine(\"Hola\")",
            "End If"
        ));

        List errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF002"));
    }

    @Test
    public void ifWithoutEndIfRaisesIF001() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "If x Then",
            "Console.WriteLine(\"Hola\")"
        ));

        List errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF001"));
    }

    @Test
    public void emptyThenBlockRaisesIF004() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "If x Then",
            "Else",
            "Console.WriteLine(\"Hola\")",
            "End If"
        ));

        List errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF004"));
    }

    @Test
    public void emptyElseBlockRaisesIF004() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "If x Then",
            "Console.WriteLine(\"Hola\")",
            "Else",
            "End If"
        ));

        List errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF004"));
    }

    @Test
    public void endIfWithoutIfRaisesIF020() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "End If"
        ));

        List errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF020"));
    }
    
    @Test
    public void ifWithoutConditionRaisesIF002() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
            "If Then",
            "Console.WriteLine(\"Hola\")",
            "End If"
        ));

        List errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF002"));
    }

    private boolean hasCode(List errors, String code) {
        for (Object obj : errors) {
            LexError e = (LexError) obj;
            if (code.equals(e.getCode())) return true;
        }
        return false;
    }
}




