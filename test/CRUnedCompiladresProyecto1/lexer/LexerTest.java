/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.lexer;

import org.junit.Ignore;

import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 *
 * @author Elias
 *
 * 
 * 
 * */


public class LexerTest {
    
    
     
        @Test
    public void analyzesModuleLineWithWhitespace() {
        Lexer lexer = new Lexer();

        AnalysisResult result = lexer.analyze(Arrays.asList("Module M1"));
        assertEquals(1, result.getLineCount());

        LineRecord lr = result.getLine(1);
        assertEquals("Module M1", lr.getRawText());

        List<Token> t = lr.getTokens();
        assertEquals(3, t.size());

        assertEquals(TokenType.KEYWORD, t.get(0).getType());
        assertEquals("Module", t.get(0).getLexeme());

        assertEquals(TokenType.WHITESPACE, t.get(1).getType());
        assertEquals(" ", t.get(1).getLexeme());

        assertEquals(TokenType.IDENTIFIER, t.get(2).getType());
        assertEquals("M1", t.get(2).getLexeme());
    }

    @Test
    public void analyzesConsoleWriteLine() {
        Lexer lexer = new Lexer();

        AnalysisResult result = lexer.analyze(Arrays.asList("Console.WriteLine(\"Hola\")"));
        LineRecord lr = result.getLine(1);

        List<Token> t = lr.getTokens();
        assertEquals(6, t.size());

        assertEquals(TokenType.KEYWORD, t.get(0).getType());
        assertEquals("Console", t.get(0).getLexeme());

        assertEquals(TokenType.PUNCTUATION, t.get(1).getType());
        assertEquals(".", t.get(1).getLexeme());

        assertEquals(TokenType.KEYWORD, t.get(2).getType());
        assertEquals("WriteLine", t.get(2).getLexeme());

        assertEquals(TokenType.PUNCTUATION, t.get(3).getType());
        assertEquals("(", t.get(3).getLexeme());

        assertEquals(TokenType.STRING_LITERAL, t.get(4).getType());
        assertEquals("\"Hola\"", t.get(4).getLexeme());

        assertEquals(TokenType.PUNCTUATION, t.get(5).getType());
        assertEquals(")", t.get(5).getLexeme());
    }

    @Test
    public void commentLineBecomesSingleCommentToken() {
        Lexer lexer = new Lexer();

        AnalysisResult result = lexer.analyze(Arrays.asList("' esto es un comentario"));
        LineRecord lr = result.getLine(1);

        List<Token> t = lr.getTokens();
        assertEquals(1, t.size());
        assertEquals(TokenType.COMMENT, t.get(0).getType());
        assertEquals("' esto es un comentario", t.get(0).getLexeme());
    }
    
    @Test
    public void tokenizesInvalidIdentifierStartingWithNumberAsSingleIdentifier() {
    Lexer lexer = new Lexer();
    AnalysisResult result = lexer.analyze(Arrays.asList("Dim 43numero1 As Integer = 7"));
    List<Token> t = result.getLine(1).getTokens();

    assertEquals(TokenType.KEYWORD, t.get(0).getType()); // Dim
    assertEquals(TokenType.WHITESPACE, t.get(1).getType());
    assertEquals(TokenType.IDENTIFIER, t.get(2).getType());
    assertEquals("43numero1", t.get(2).getLexeme());
    
    }
    
    @Test
    public void keywordsAreRecognized() {
        Lexer lexer = new Lexer();
        AnalysisResult r = lexer.analyze(Arrays.asList("If x Then"));
        List<Token> t = r.getLine(1).getTokens();

        assertEquals(TokenType.KEYWORD, t.get(0).getType());
        assertEquals("If", t.get(0).getLexeme());

        assertEquals(TokenType.WHITESPACE, t.get(1).getType());

        assertEquals(TokenType.IDENTIFIER, t.get(2).getType()); // x
        assertEquals("x", t.get(2).getLexeme());

        assertEquals(TokenType.WHITESPACE, t.get(3).getType());

        assertEquals(TokenType.KEYWORD, t.get(4).getType());
        assertEquals("Then", t.get(4).getLexeme());
    }
    
    @Test
    public void tokenizesUnclosedStringAsUnknown() {
        Lexer lexer = new Lexer();
        AnalysisResult r = lexer.analyze(Arrays.asList("Console.WriteLine(\"Hola)"));
        List<Token> t = r.getLine(1).getTokens();

        // Esperamos que el token del string exista y sea UNKNOWN
        assertEquals(TokenType.KEYWORD, t.get(0).getType());
        assertEquals(TokenType.PUNCTUATION, t.get(1).getType());
        assertEquals(TokenType.KEYWORD, t.get(2).getType());
        assertEquals(TokenType.PUNCTUATION, t.get(3).getType());

        assertEquals(TokenType.UNKNOWN, t.get(4).getType());
        assertTrue(t.get(4).getLexeme().startsWith("\"Hola"));
    }



    
    
    
}
