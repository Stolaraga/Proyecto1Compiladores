package CRUnedCompiladresProyecto1.util;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import CRUnedCompiladresProyecto1.model.LexError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * @author Elias
 */
public final class ErrorUtils {

    private ErrorUtils() {}

    /**
     * Ordena por: line asc, column asc, code asc, message asc
     * y elimina duplicados exactos (mismo line/column/code/message).
     */
    public static List<LexError> sortAndDedup(List<LexError> errors) {
        if (errors == null || errors.isEmpty()) {
            return Collections.emptyList();
        }

        List<LexError> sorted = new ArrayList<>(errors);

        Collections.sort(sorted, new Comparator<LexError>() {
            @Override
            public int compare(LexError a, LexError b) {
                int c = Integer.compare(a.getLine(), b.getLine());
                if (c != 0) return c;

                c = Integer.compare(a.getColumn(), b.getColumn());
                if (c != 0) return c;

                c = safe(a.getCode()).compareTo(safe(b.getCode()));
                if (c != 0) return c;

                return safe(a.getMessage()).compareTo(safe(b.getMessage()));
            }

            private String safe(String s) {
                return s == null ? "" : s;
            }
        });

        // Dedup exacto preservando orden (ya ordenado)
        List<LexError> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (LexError e : sorted) {
            String key = e.getLine() + "|" + e.getCode() + "|" + e.getMessage();
            if (seen.add(key)) {
                out.add(e);
            }
        }

        return out;
    }
}

