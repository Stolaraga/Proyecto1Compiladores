/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.io;


import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;



/**
 *
 * @author Elias
 */
public class SourceReaderTest {
    
       @Test
    public void preservesBlankLines() throws Exception {
        Path tmp = Files.createTempFile("sprint0-reader-", ".vb");
        Files.writeString(tmp, "A\n\nB\n", StandardCharsets.UTF_8);

        SourceReader reader = new SourceReader();
        List<String> lines = reader.readAllLines(tmp);

        assertEquals(3, lines.size());
        assertEquals("A", lines.get(0));
        assertEquals("", lines.get(1));   // línea vacía preservada
        assertEquals("B", lines.get(2));
    }
    
    
}
