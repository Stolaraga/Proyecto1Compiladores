/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package CRUnedCompiladresProyecto1;

import CRUnedCompiladresProyecto1.io.LogWriter;
import CRUnedCompiladresProyecto1.io.PathResolver;
import CRUnedCompiladresProyecto1.io.SourceReader;
import CRUnedCompiladresProyecto1.lexer.Lexer;
import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.validate.ValidationPipeline;

import java.nio.file.Path;
import java.util.List;



/**
 *
 * @author Elias
 */
public class App {

    public static void main(String[] args) { // args es el arreglo en posicion 0 java -jar AnalizadorLexicoVB.jar prueba.vb

        PathResolver resolver = new PathResolver();
        SourceReader reader = new SourceReader();
        LogWriter writer = new LogWriter();

        // NUEVO
        Lexer lexer = new Lexer();
        ValidationPipeline pipeline = new ValidationPipeline();

        try {
            Path workingDir = resolver.getWorkingDir();

            // 1) Determinar archivo .vb (arg o primero encontrado)
            Path vbPath = resolver.resolveInputVb(workingDir, args);

            // 2) Leer líneas (preservando vacías)
            List<String> lines = reader.readAllLines(vbPath);

            // 3) Calcular salida <nombre>-errores.log
            Path outLog = resolver.resolveOutputLog(vbPath);

            // 4) Analizar (tokens por línea)
            AnalysisResult ar = lexer.analyze(lines);

            // 5) Validar (genera errores ya ordenados)
            List<LexError> errors = pipeline.validateAll(ar);

            // 6) Escribir clon numerado + anexar errores al final (ordenados)
            writer.writeNumberedClone(outLog, lines);
            writer.appendErrorsAtEnd(outLog, errors);

            System.out.println("OK -> Generado: " + outLog.toAbsolutePath());
            System.out.println("Errores encontrados: " + errors.size());

        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            System.exit(1);
        }
    }

}
