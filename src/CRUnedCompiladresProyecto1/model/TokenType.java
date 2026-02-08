/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.model;

/**
 *
 * Tipos de token para el analizador léxico (Sprint 1).
 * En sprints posteriores podemos ampliar o subdividir.
 * @author Elias
 */
public enum TokenType {
    
    // Estructura / clasificación general
    KEYWORD,        // Imports, Module, End, Dim, Console, WriteLine, etc. (case-insensitive) 
    IDENTIFIER,     // Nombres de variables/módulos
                
    NUMBER,         // 123, 45 Solo enteros
    STRING_LITERAL, // "whaaashaaaa mundo" ejemplo
            
    OPERATOR,       // + - * / = 
    PUNCTUATION,    // ( ) . , : 
    
    COMMENT,        // Línea que inicia con '  (para este proyecto, comentario solo si inicia línea)
    WHITESPACE,     // Conservar espacios como tokens;
    UNKNOWN         // Cualquier cosa que no calce 
         
}
