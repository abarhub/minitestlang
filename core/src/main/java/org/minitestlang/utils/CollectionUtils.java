package org.minitestlang.utils;

import java.util.Collection;

public class CollectionUtils {

    public static int size(final Collection<?> c) {
        return c == null ? 0 : c.size();
    }
}
