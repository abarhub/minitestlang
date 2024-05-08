package org.minitestlang.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionUtilsTest {

    @Test
    void sizeNull() {
        assertEquals(0, CollectionUtils.size(null));
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, CollectionUtils.size(List.of()));
    }

    @Test
    void sizeOne() {
        assertEquals(1, CollectionUtils.size(List.of("a")));
    }

    @Test
    void sizeTree() {
        assertEquals(3, CollectionUtils.size(List.of("a", "b", "c")));
    }

}