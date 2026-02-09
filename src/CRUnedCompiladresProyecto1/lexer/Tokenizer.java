/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.lexer;

import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;
import CRUnedCompiladresProyecto1.vb.ReservedWords;


import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Elias
 */
public class Tokenizer {
    

    public List<Token> tokenizeLine(String line, int lineNumber) {
        List<Token> tokens = new ArrayList<>();

        if (line == null) line = "";

        // comentario si la línea inicia con '
        if (!line.isEmpty() && line.charAt(0) == '\'') {
            tokens.add(new Token(TokenType.COMMENT, line, lineNumber, 1));
            return tokens;
        }

        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);
            int col = i + 1; // columnas 1-based

            // 1) WHITESPACE (lo contamos y lo tokenizamos para saber la columna)
            if (isWhitespace(c)) {
                int start = i;
                while (i < line.length() && isWhitespace(line.charAt(i))) i++;
                String lex = line.substring(start, i);
                tokens.add(new Token(TokenType.WHITESPACE, lex, lineNumber, start + 1, lex.length()));
                continue;
            }

            // 2) STRING_LITERAL: " No soy perezoso; estoy en modo ahorro de energía "
            if (c == '"') {
                int start = i;
                i++; // consume la primera comilla

                boolean closed = false;
                while (i < line.length()) {
                    char cc = line.charAt(i);

                    if (cc == '"') {
                        // VB suele escapar comillas dobles como "" dentro del string
                        if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                            i += 2; // consume "" (comilla escapada)
                            continue;
                        }
                        i++; // consume la comilla de cierre
                        closed = true;
                        break;
                    }
                    i++;
                }

                // Si no cerró, igual devolvemos el literal hasta el final
                
                
                String lex = line.substring(start, i);
                TokenType type = closed ? TokenType.STRING_LITERAL : TokenType.UNKNOWN;
                tokens.add(new Token(type, lex, lineNumber, start + 1, lex.length()));


                continue;
            }

            // 3) NÚMERO (entero o decimal) o "identificador inválido" que inicia con dígito (ej: 43numero1)
            if (Character.isDigit(c)) {
                int start = i;

                // parte entera
                while (i < line.length() && Character.isDigit(line.charAt(i))) i++;

                // parte decimal opcional: . seguido de dígitos (ej: 12.5)
                if (i + 1 < line.length() && line.charAt(i) == '.' && Character.isDigit(line.charAt(i + 1))) {
                    i++; // consume '.'
                    while (i < line.length() && Character.isDigit(line.charAt(i))) i++;
                }

                // Si luego viene letra o '_', NO es un número puro: es un identificador inválido tipo "43numero1"
                if (i < line.length() && (Character.isLetter(line.charAt(i)) || line.charAt(i) == '_')) {
                    i++; // consume esa letra/_
                    while (i < line.length() && isIdentifierPart(line.charAt(i))) i++;

                    String lex = line.substring(start, i);
                    tokens.add(new Token(TokenType.IDENTIFIER, lex, lineNumber, start + 1, lex.length()));
                    continue;
                }

                String lex = line.substring(start, i);
                tokens.add(new Token(TokenType.NUMBER, lex, lineNumber, start + 1, lex.length()));
                continue;
            }


            // 4) IDENTIFIER / KEYWORD 
            //    
            if (isIdentifierStart(c)) {
                int start = i;
                i++;
                while (i < line.length() && isIdentifierPart(line.charAt(i))) i++;

                String lex = line.substring(start, i);
                TokenType type = isKeyword(lex) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
                tokens.add(new Token(type, lex, lineNumber, start + 1, lex.length()));
                continue;
            }

            // 5) OPERATORS 
            if (isOperator(c)) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c), lineNumber, col, 1));
                i++;
                continue;
            }

            // 6) PUNCTUATION
            if (isPunctuation(c)) {
                tokens.add(new Token(TokenType.PUNCTUATION, String.valueOf(c), lineNumber, col, 1));
                i++;
                continue;
            }

            // 7) Cualquier cosa rara
            tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c), lineNumber, col, 1));
            i++;
        }

        return tokens;
    }

   
    private boolean isKeyword(String lexeme) {
    
        return ReservedWords.isReserved(lexeme); 
        
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isIdentifierPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=';
    }

    private boolean isPunctuation(char c) {
        return c == '(' || c == ')' || c == '.' || c == ',' || c == ':';
    }

    
}
