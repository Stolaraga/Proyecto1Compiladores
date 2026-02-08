/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package CRUnedCompiladresProyecto1;

import CRUnedCompiladresProyecto1.io.PathResolver;
import CRUnedCompiladresProyecto1.io.SourceReader;
import CRUnedCompiladresProyecto1.io.LogWriter;

import java.nio.file.Path;
import java.util.List;


/**
 *
 * @author Elias
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        PathResolver resolver = new PathResolver();
        SourceReader reader = new SourceReader();
        LogWriter writer = new LogWriter();

        try {
            Path workingDir = resolver.getWorkingDir();

            // 1) Determinar archivo .vb (arg o primero encontrado)
            Path vbPath = resolver.resolveInputVb(workingDir, args);

            // 2) Leer líneas (preservando vacías)
            List<String> lines = reader.readAllLines(vbPath);

            // 3) Calcular salida <nombre>-errores.log
            Path outLog = resolver.resolveOutputLog(vbPath);

            // 4) Escribir clon numerado
            writer.writeNumberedClone(outLog, lines);

            System.out.println("OK -> Generado: " + outLog.toAbsolutePath());

        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            System.exit(1);
        }
        
    }
    
}
