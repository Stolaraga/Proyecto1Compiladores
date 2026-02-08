/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;


/**
 *
 * @author Elias
 */
public class LogWriter {
    
     
    
    public void writeNumberedClone(Path outLog, List<String> lines) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(outLog, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            for (int i = 0; i < lines.size(); i++) {
                String n = String.format("%04d", i + 1);
                String line = lines.get(i);

                if (line.isEmpty()) {
                    bw.write(n);                 // sin espacio final
                } else {
                    bw.write(n + " " + line);    // separador solo si hay contenido
                }
                bw.newLine();
            }
        }
    }
    
}
