/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.model;

import java.util.Objects;

/**
 *
 * Representa un token léxico encontrado en el código fuente.
 * Inmutable: una vez creado, no cambia.
 * @author Elias
 */
public final class Token {
    
    private final TokenType type;   
    private final String lexeme;    
    private final int line;         
    private final int column;       
    private final int length;       
    
    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, line, column, lexeme != null ? lexeme.length() : 0);
    }
    
        public Token(TokenType type, String lexeme, int line, int column, int length) {
        this.type = Objects.requireNonNull(type, "type");
        this.lexeme = Objects.requireNonNull(lexeme, "lexeme");

        if (line < 1) throw new IllegalArgumentException("line debe ser >= 1");
        if (column < 1) throw new IllegalArgumentException("column debe ser >= 1");
        if (length < 0) throw new IllegalArgumentException("length debe ser >= 0");

        this.line = line;
        this.column = column;
        this.length = length;
    }
        
            public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

    /**
     * Columna final (inclusive) aproximada: column + length - 1
     * Si length=0, devuelve column.
     */
    public int getEndColumn() {
        return length == 0 ? column : (column + length - 1);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", line=" + line +
                ", column=" + column +
                ", length=" + length +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return line == token.line
                && column == token.column
                && length == token.length
                && type == token.type
                && lexeme.equals(token.lexeme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme, line, column, length);
    }

                
}
