package com.broadridge.brx.matmapparser.util;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class StringUtil {

    private static Set<Character> stringToCharacterSet(final String s) {
        final Set<Character> set = new HashSet<>();
        for (final char c : s.toCharArray()) {
            set.add(c);
        }
        return set;
    }

    public static boolean containsAllChars(final String container, final String containee) {
        return stringToCharacterSet(container).containsAll(stringToCharacterSet(containee));
    }
}
