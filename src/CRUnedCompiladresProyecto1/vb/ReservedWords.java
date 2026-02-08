/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.vb;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Elias
 */
public class ReservedWords {
    
    private static final Set<String> WORDS;

    static {
        Set<String> s = new HashSet<>();

        // Estructura básica
        s.add("imports");
        s.add("module");
        s.add("end");
        s.add("dim");
        s.add("as");

        // Console / IO (para tu proyecto se tokenizan como keyword)
        s.add("console");
        s.add("writeline");

        // Tipos permitidos por el enunciado
        s.add("integer");
        s.add("string");
        s.add("boolean");
        s.add("byte");

        WORDS = Collections.unmodifiableSet(s);
    }

    private ReservedWords() {
        // Utility class
    }

    public static boolean isReserved(String lexeme) {
        if (lexeme == null) return false;
        return WORDS.contains(lexeme.toLowerCase());
    }

    public static Set<String> all() {
        return WORDS;
    }

    
}
