/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;

import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.LineRecord;
import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Elias
 */
public class CommentValidator {
    
    public List<LexError> validate(LineRecord lr) {
        List<LexError> errors = new ArrayList<>();
        if (lr == null || lr.getTokens() == null) return errors;

        for (Token t : lr.getTokens()) {
            if (t.getType() == TokenType.COMMENT) {
                // Solo válido si la línea INICIA con '
               int firstNonWsCol = firstNonWhitespaceColumn(lr.getRawText());
                boolean commentAtStartIgnoringIndent = (firstNonWsCol == t.getColumn());

                if (!commentAtStartIgnoringIndent) {
                    errors.add(new LexError(
                        "COM001",
                        "Comentario inválido para este proyecto: el apóstrofe (') solo es válido si la línea inicia con ' (ignorando espacios).",
                        t.getLine(),
                        t.getColumn()
                    ));
                }

                break; // solo ocupamos el primer comentario
            }
        }
        return errors;
    }
    
        private int firstNonWhitespaceColumn(String raw) {
        if (raw == null || raw.isEmpty()) return -1;
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c != ' ' && c != '\t') return i + 1; // col 1-based
        }
        return -1;
    }

    
}
