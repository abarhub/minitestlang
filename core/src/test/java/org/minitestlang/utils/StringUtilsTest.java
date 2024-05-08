package org.minitestlang.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  ", "          "})
    void isBlankTrue(String value) {
        assertTrue(StringUtils.isBlank(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "  abc", "abc   "})
    void isBlankFalse(String value) {
        assertFalse(StringUtils.isBlank(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "  abc", "abc   "})
    void isNotBlankTrue(String value) {
        assertTrue(StringUtils.isNotBlank(value));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  ", "          "})
    void isNotBlankFalse(String value) {
        assertFalse(StringUtils.isNotBlank(value));
    }
}