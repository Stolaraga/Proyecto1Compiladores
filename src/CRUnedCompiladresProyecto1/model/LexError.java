/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.model;

import java.util.Objects;

  
/**
 *
 * @author Elias
 */
public class LexError {
    
    private final String code;     
    private final String message;  
    private final int line;        
    private final int column;      

    public LexError(String code, String message, int line, int column) {
        this.code = Objects.requireNonNull(code, "code");
        this.message = Objects.requireNonNull(message, "message");
        if (line < 1) throw new IllegalArgumentException("line debe ser >= 1");
        if (column < 1) throw new IllegalArgumentException("column debe ser >= 1");
        this.line = line;
        this.column = column;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return code + " (L" + String.format("%04d", line) + ",C" + column + "): " + message;
    }

    
    
}
