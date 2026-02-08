/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.symbols;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



/**
 *
 * @author Elias
 */
public class SymbolTable {
    
    private final Map<String, Symbol> symbols = new HashMap<>();

    private String key(String name) {
        return name.toLowerCase();
    }

    public boolean isDeclared(String name) {
        if (name == null) return false;
        return symbols.containsKey(key(name));
    }

    public Symbol get(String name) {
        if (name == null) return null;
        return symbols.get(key(name));
    }

    public VbType getType(String name) {
        Symbol s = get(name);
        return s == null ? VbType.UNKNOWN : s.getType();
    }

    /**
     * Declara un símbolo. Si ya existe, lanza IllegalStateException 
     */
    public void declare(String name, VbType type, int lineNumber) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");

        String k = key(name);
        if (symbols.containsKey(k)) {
            Symbol prev = symbols.get(k);
            throw new IllegalStateException(
                    "La variable '" + name + "' ya fue declarada en la línea " + prev.getDeclaredLine()
            );
        }

        symbols.put(k, new Symbol(name, type, lineNumber));
    }

    public int size() {
        return symbols.size();
    }

    public Map<String, Symbol> asUnmodifiableMap() {
        return Collections.unmodifiableMap(symbols);
    }

    
}
