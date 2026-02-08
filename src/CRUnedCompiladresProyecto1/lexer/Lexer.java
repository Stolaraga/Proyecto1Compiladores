/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.lexer;

import CRUnedCompiladresProyecto1.model.Token;

import CRUnedCompiladresProyecto1.model.AnalysisResult;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.model.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * - Recorre líneas
 * - Usa Tokenizer para obtener tokens por línea
 * - Devuelve una lista "plana" de tokens (cada token ya incluye line/column)

* 
 * @author Elias
 */
public class Lexer {
    
        private final Tokenizer tokenizer;

    public Lexer() {
        this(new Tokenizer());
    }

    public Lexer(Tokenizer tokenizer) {
        if (tokenizer == null) throw new IllegalArgumentException("tokenizer no puede ser null");
        this.tokenizer = tokenizer;
    }
    
    
    /**
     * Analiza todas las líneas del archivo y devuelve tokens por línea.
     */
    public AnalysisResult analyze(List<String> lines) {
        if (lines == null) lines = Collections.emptyList();

        List<LineRecord> records = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String raw = lines.get(i);
            if (raw == null) raw = "";

            List<Token> tokens = tokenizer.tokenizeLine(raw, lineNumber);
            records.add(new LineRecord(lineNumber, raw, tokens));
        }

        return new AnalysisResult(records);
    }
    
    
    
    
    
    /**
     * Tokeniza todas las líneas de un archivo.
     * @param lines Lista de líneas del .vb (incluye líneas vacías).
     * @return Lista plana de tokens en orden.
     */
    public List<Token> tokenizeAll(List<String> lines) {
        if (lines == null) return Collections.emptyList();

        List<Token> out = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i);
            out.addAll(tokenizer.tokenizeLine(line, lineNumber));
        }
        return out;
    }

    /**
     * Tokeniza una sola línea (helper útil para pruebas).
     */
    public List<Token> tokenizeLine(String line, int lineNumber) {
        return tokenizer.tokenizeLine(line, lineNumber);
    }

    
}
