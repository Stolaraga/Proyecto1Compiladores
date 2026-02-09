/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.util;

import CRUnedCompiladresProyecto1.model.LexError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;



/**
 *
 * @author Elias
 */
public class ErrorUtilsTest {
    
     @Test
    public void sortAndDedupRemovesExactDuplicates() {
        LexError a1 = new LexError("X001", "Mensaje", 2, 5);
        LexError a2 = new LexError("X001", "Mensaje", 2, 5); // duplicado exacto
        LexError b  = new LexError("X000", "Otro", 1, 1);

        List<LexError> out = ErrorUtils.sortAndDedup(Arrays.asList(a1, a2, b));

        // Debe quedar solo 2
        assertEquals(2, out.size());

        // Y ordenado: primero línea 1, luego línea 2
        assertEquals(1, out.get(0).getLine());
        assertEquals(2, out.get(1).getLine());
    }
    
}
