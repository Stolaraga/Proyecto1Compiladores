/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.validate;


import CRUnedCompiladresProyecto1.model.LexError;
import CRUnedCompiladresProyecto1.model.Token;
import CRUnedCompiladresProyecto1.vb.ReservedWords;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Elias
 */
public class IdentifierValidator {
    
    public List<LexError> validateIdentifier(Token idToken) {
        List<LexError> errors = new ArrayList<>();
        if (idToken == null || idToken.getLexeme() == null) return errors;

        String name = idToken.getLexeme();

        // Reserved word
        if (ReservedWords.isReserved(name)) {
            errors.add(new LexError("ID004",
                    "Identificador inválido: '" + name + "' es una palabra reservada.",
                    idToken.getLine(), idToken.getColumn()));
        }

        // No iniciar con número
        if (!name.isEmpty() && Character.isDigit(name.charAt(0))) {
            errors.add(new LexError("ID001",
                    "Identificador inválido: no puede iniciar con un número ('" + name + "').",
                    idToken.getLine(), idToken.getColumn()));
        }

        // No iniciar con _
        if (!name.isEmpty() && name.charAt(0) == '_') {
            errors.add(new LexError("ID002",
                    "Identificador inválido: no puede iniciar con '_' ('" + name + "').",
                    idToken.getLine(), idToken.getColumn()));
        }

        // No espacios (por seguridad, aunque tokenizer separa whitespace)
        if (name.contains(" ") || name.contains("\t")) {
            errors.add(new LexError("ID003",
                    "Identificador inválido: no puede contener espacios ('" + name + "').",
                    idToken.getLine(), idToken.getColumn()));
        }

        return errors;
    }

    
}
