/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Elias
 */
public final class AnalysisResult {
    
    private final List<LineRecord> lines;

    public AnalysisResult(List<LineRecord> lines) {
        Objects.requireNonNull(lines, "lines");
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
    }

    public List<LineRecord> getLines() {
        return lines;
    }

    public int getLineCount() {
        return lines.size();
    }

    public LineRecord getLine(int lineNumber) {
        if (lineNumber < 1 || lineNumber > lines.size()) {
            throw new IllegalArgumentException("lineNumber fuera de rango: " + lineNumber);
        }
        return lines.get(lineNumber - 1);
    }

    public List<Token> getAllTokensFlattened() {
        List<Token> out = new ArrayList<>();
        for (LineRecord lr : lines) {
            out.addAll(lr.getTokens());
        }
        return out;
    }

    
}
