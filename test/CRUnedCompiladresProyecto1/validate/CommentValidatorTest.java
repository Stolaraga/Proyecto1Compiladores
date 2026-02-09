/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;


import CRUnedCompiladresProyecto1.lexer.Lexer;
import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.validate.CommentValidator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

    
/**
 *
 * @author Elias
 */
public class CommentValidatorTest {
    
        @Test
    public void inlineCommentIsInvalidForThisProject() {
        Lexer lexer = new Lexer();
        AnalysisResult ar = lexer.analyze(Arrays.asList("Dim x As Integer = 10 ' comentario"));
        LineRecord lr = ar.getLine(1);

        List<LexError> errors = new CommentValidator().validate(lr);

        boolean has = false;
        for (LexError e : errors) {
            if ("COM001".equals(e.getCode())) { has = true; break; }
        }
        assertTrue("Debe existir COM001", has);
    }
    
    
    
}
