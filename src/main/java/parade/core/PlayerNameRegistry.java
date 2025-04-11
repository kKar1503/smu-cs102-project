package parade.core;

import java.util.*;

class PlayerNameRegistry {
    private static final Map<String, Integer> nameCounts = new HashMap<>();

    public static String getUniqueName(String baseName) {
        if (baseName == null || baseName.isEmpty()) {
            throw new IllegalArgumentException("Base name cannot be null or empty");
        }

        String candidate = baseName;
        int count = nameCounts.getOrDefault(baseName, 0);

        if (count > 0) {
            candidate = String.format("%s (%d)", candidate, count);
        }

        nameCounts.put(baseName, ++count);
        return candidate;
    }
}
