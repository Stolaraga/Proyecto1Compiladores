/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.symbols;

/**
 *
 * @author Elias
 */
public enum VbType {
    
    
    INTEGER,
    BYTE,
    STRING,
    BOOLEAN,
    UNKNOWN;

    public boolean isNumeric() {
        return this == INTEGER || this == BYTE;
    }

    /**
     * Convierte un lexema a VbType.
     * Comparación case-insensitive. Si no coincide, devuelve UNKNOWN.
     */
    public static VbType fromLexeme(String lexeme) {
        if (lexeme == null) return UNKNOWN;
        String s = lexeme.trim().toLowerCase();

        switch (s) {
            case "integer":
                return INTEGER;
            case "byte":
                return BYTE;
            case "string":
                return STRING;
            case "boolean":
                return BOOLEAN;
            default:
                return UNKNOWN;
        }
    }
    
}
