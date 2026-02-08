/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CRUnedCompiladresProyecto1.symbols;


import java.util.Objects;


/**
 *
 * @author Elias
 */
public class Symbol {
    
    private final String name;     
    private final VbType type;
    private final int declaredLine;

    public Symbol(String name, VbType type, int declaredLine) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        if (declaredLine < 1) throw new IllegalArgumentException("declaredLine debe ser >= 1");
        this.declaredLine = declaredLine;
    }

    public String getName() {
        return name;
    }

    public VbType getType() {
        return type;
    }

    public int getDeclaredLine() {
        return declaredLine;
    }

    @Override
    public String toString() {
        return "Symbol{name='" + name + "', type=" + type + ", declaredLine=" + declaredLine + "}";
    }

    
}
