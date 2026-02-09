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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Elias
 */
public class IfStructureValidatorTest {
    
    @Test
    public void ifWithoutEndIfRaisesIF001() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "If x Then",
                "Console.WriteLine(\"Hola\")"
        ));

        List<LexError> errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF001"));
    }

    @Test
    public void endIfWithoutIfRaisesIF020() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList(
                "End If"
        ));

        List<LexError> errors = new IfStructureValidator().validate(ar);

        assertTrue(hasCode(errors, "IF020"));



    }
    
    private boolean hasCode(List<LexError> errors, String code) {
    for (LexError e : errors) {
        if (code.equals(e.getCode())) return true;
    }
    return false;
}


}
