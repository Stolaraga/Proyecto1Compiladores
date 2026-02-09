/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.io;

import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.util.ErrorUtils;
import java.io.BufferedWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * @author Elias
 */
public class LogWriter {
    
     
    
        /**
     * Crea/reescribe el archivo .log con una copia del código fuente numerada (4 dígitos).
     * Tiene este Formato:
     * 0001 <contenido de línea>
     * 0002 <contenido de línea>
     * Si la línea está vacía, se escribe solo "000X" sin espacio.
     */
    public void writeNumberedClone(Path outLog, List<String> lines) throws IOException {
        if (outLog == null) throw new IllegalArgumentException("outLog no puede ser null");
        if (lines == null) lines = Collections.emptyList();

        try (BufferedWriter bw = Files.newBufferedWriter(
                outLog,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (int i = 0; i < lines.size(); i++) {
                String n = String.format("%04d", i + 1);
                String line = lines.get(i);
                if (line == null) line = "";

                if (line.isEmpty()) {
                    bw.write(n);                 // sin espacio final
                } else {
                    bw.write(n + " " + line);    // separador solo si hay contenido
                }
                bw.newLine();
            }
        }
    }

    /**
     * Agrega al final del .log la lista de errores 
     * Formato por línea:
     * Error <codigo>. Línea 0001. <mensaje>
     *
     * - Ordena por línea, columna, código y mensaje
     * - Elimina duplicados exactos
     * - No borra el contenido previo del .log (APPEND)
     */
    public void appendErrorsAtEnd(Path outLog, List<LexError> errors) throws IOException {
        if (outLog == null) throw new IllegalArgumentException("outLog no puede ser null");
        if (errors == null || errors.isEmpty()) return;

        List<LexError> ordered = ErrorUtils.sortAndDedup(errors);

        List<String> out = new ArrayList<>();
        out.add(""); // línea en blanco para separar del clon numerado

        for (LexError e : ordered) {
            if (e == null) continue;

            String line4 = String.format("%04d", e.getLine());
            String code = (e.getCode() == null || e.getCode().trim().isEmpty()) ? "SIN-CODIGO" : e.getCode().trim();
            String msg  = (e.getMessage() == null) ? "" : e.getMessage().trim();

            out.add("Error " + code + ". Línea " + line4 + ". " + msg);
        }

        Files.write(
                outLog,
                out,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    /**
     * escribe el clon numerado y luego anexa errores en una sola llamada.
     */
    public void writeLog(Path outLog, List<String> sourceLines, List<LexError> errors) throws IOException {
        writeNumberedClone(outLog, sourceLines);
        appendErrorsAtEnd(outLog, errors);
    }
    
}
