/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Elias
 */
public class PathResolver {
    
      public Path getWorkingDir() {
        return Paths.get(System.getProperty("user.dir"));
    }


  
      
    public Path resolveInputVb(Path workingDir, String[] args) throws IOException {
    Path baseDir = (workingDir != null ? workingDir : Paths.get("").toAbsolutePath()).normalize();

    if (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) {
        Path candidate = Paths.get(args[0].trim());

        Path file = candidate.isAbsolute()
                ? candidate.normalize()
                : baseDir.resolve(candidate).normalize();

        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException("No existe el archivo: " + file);
        }

        String fileName = file.getFileName().toString();
        if (!fileName.endsWith(".vb")) {
            throw new IllegalArgumentException("El archivo debe tener extensión exacta .vb: " + fileName);
        }

        return file;
    }

    try (Stream<Path> stream = Files.list(baseDir)) {
        Optional<Path> firstVb = stream
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().endsWith(".vb"))
                .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                .findFirst();

        if (firstVb.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontró ningún archivo con extensión exacta .vb en: " + baseDir.toAbsolutePath()
            );
        }

        return firstVb.get();
    }
}

    public String readSourcePreservingFormat(Path vbFile) throws IOException {
        return Files.readString(vbFile, StandardCharsets.UTF_8);
    }
    



    public Path resolveOutputLog(Path vbPath) {
        String fileName = vbPath.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String base = (dot > 0) ? fileName.substring(0, dot) : fileName;
        return vbPath.getParent().resolve(base + "-errores.log");
    }
    
    
}
