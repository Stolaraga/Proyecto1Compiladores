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
public class LineRecord {
    
    private final int lineNumber;     
    private final String rawText;     
    private final List<Token> tokens; 

    public LineRecord(int lineNumber, String rawText, List<Token> tokens) {
        if (lineNumber < 1) throw new IllegalArgumentException("lineNumber debe ser >= 1");
        this.lineNumber = lineNumber;
        this.rawText = rawText == null ? "" : rawText;

        Objects.requireNonNull(tokens, "tokens");
        this.tokens = Collections.unmodifiableList(new ArrayList<>(tokens));
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getRawText() {
        return rawText;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public boolean isEmptyLine() {
        return rawText.isEmpty();
    }

    @Override
    public String toString() {
        return "LineRecord{lineNumber=" + lineNumber +
                ", rawText='" + rawText + '\'' +
                ", tokens=" + tokens.size() +
                '}';
    }

    
}
