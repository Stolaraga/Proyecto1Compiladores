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
public void sortAndDedupRemovesDuplicatesEvenIfColumnDiffers() {
    LexError e1 = new LexError("X001", "Mismo", 2, 1);
    LexError e2 = new LexError("X001", "Mismo", 2, 10); // misma línea, distinto col

    List<LexError> out = ErrorUtils.sortAndDedup(Arrays.asList(e1, e2));
    assertEquals(1, out.size());
}

    
}
