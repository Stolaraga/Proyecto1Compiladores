/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


/**
 *
 * @author Elias
 */
public class SourceReader {
    
        public List<String> readAllLines(Path vbPath) throws IOException {
        
       // readAllLines preserva líneas vacías devuelve "" en esas líneas 

        return Files.readAllLines(vbPath, StandardCharsets.UTF_8);
    }
    
}
