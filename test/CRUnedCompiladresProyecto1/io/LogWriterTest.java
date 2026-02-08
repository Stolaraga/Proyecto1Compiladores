/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.io;

/**
 *
 * @author Elias
 */

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class LogWriterTest {
    
    
     @Test
    public void writesNumberedCloneCorrectly() throws Exception {  
        Path out = Files.createTempFile("sprint0-logwriter-", ".log");

        LogWriter writer = new LogWriter();
        writer.writeNumberedClone(out, List.of("X", "", "Y"));

        List<String> got = Files.readAllLines(out, StandardCharsets.UTF_8);

        assertEquals(List.of(
                "0001 X",
                "0002",
                "0003 Y"
        ), got);
    }
    
}
