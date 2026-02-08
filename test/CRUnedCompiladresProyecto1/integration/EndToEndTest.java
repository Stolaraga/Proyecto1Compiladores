/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.integration;



import CRUnedCompiladresProyecto1.io.LogWriter;
import CRUnedCompiladresProyecto1.io.PathResolver;
import CRUnedCompiladresProyecto1.io.SourceReader;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;



/**
 *
 * @author Elias
 */
public class EndToEndTest {
    
    
    @Test
    public void sprint0GoldenLogMatches() throws Exception {

        // Root del proyecto (donde está build.xml)
        Path projectRoot = new File(".").getCanonicalFile().toPath();

        // Ahora test-resources está en la raíz del proyecto
        Path fixtureVb = projectRoot
                .resolve("test-resources")
                .resolve("sprint0")
                .resolve("input_ok.vb");

        Path expected = projectRoot
                .resolve("test-resources")
                .resolve("sprint0")
                .resolve("expected-errores.log");

        assertTrue("No existe input_ok.vb en: " + fixtureVb.toAbsolutePath(), Files.exists(fixtureVb));
        assertTrue("No existe expected-errores.log en: " + expected.toAbsolutePath(), Files.exists(expected));

        // Copiamos a temp para no ensuciar los fixtures
        Path tmpDir = Files.createTempDirectory("sprint0-e2e-");
        Path vbCopy = tmpDir.resolve("input_ok.vb");
        Files.copy(fixtureVb, vbCopy);

        PathResolver resolver = new PathResolver();
        SourceReader reader = new SourceReader();
        LogWriter writer = new LogWriter();

        List<String> lines = reader.readAllLines(vbCopy);
        Path outLog = resolver.resolveOutputLog(vbCopy);
        writer.writeNumberedClone(outLog, lines);

        List<String> got = Files.readAllLines(outLog, StandardCharsets.UTF_8);
        List<String> exp = Files.readAllLines(expected, StandardCharsets.UTF_8);

        assertEquals(exp, got);
    }
    
    
}
