/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.util;

import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.model.TokenType;

/**
 *
 * @author Elias
 */
public final class TokenUtils {

    private TokenUtils() {
    }

    public static boolean isIntegerLiteral(Token t) {
        return t != null
            && t.getType() == TokenType.NUMBER
            && t.getLexeme() != null
            && t.getLexeme().matches("\\d+");
    }
}
