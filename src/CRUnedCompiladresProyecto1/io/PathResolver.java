/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.io;

import java.io.IOException;
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
        if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            Path p = workingDir.resolve(args[0].trim());
            if (!Files.exists(p)) throw new IllegalArgumentException("No existe el archivo: " + p);
            if (!p.getFileName().toString().toLowerCase().endsWith(".vb"))
                throw new IllegalArgumentException("El archivo debe ser .vb: " + p.getFileName());
            return p;
        }

        // Si no hay args: buscar el primer .vb (orden alfabético)
        try (Stream<Path> s = Files.list(workingDir)) {
            Optional<Path> first = s
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".vb"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase()))
                    .findFirst();

            if (first.isEmpty())
                throw new IllegalArgumentException("No se encontró ningún .vb en: " + workingDir.toAbsolutePath());

            return first.get();
        }
    }

    public Path resolveOutputLog(Path vbPath) {
        String fileName = vbPath.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String base = (dot > 0) ? fileName.substring(0, dot) : fileName;
        return vbPath.getParent().resolve(base + "-errores.log");
    }
    
    
}
