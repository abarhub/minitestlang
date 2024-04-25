package org.minitestlang.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerifyUtilsTest {

    @Test
    void verifyTrue() {
        // ARRANGE

        // ACT
        VerifyUtils.verify(true, "message4");

        // ASSERT
        // no exception throw
    }

    @Test
    void verifyFalse() {
        // ARRANGE

        // ACT
        var res = assertThrows(IllegalArgumentException.class,
                () -> VerifyUtils.verify(false, "message1"));

        // ASSERT
        assertEquals("message1", res.getMessage());
    }

    @Test
    void verifyNotNullTrue() {
        // ARRANGE

        // ACT
        VerifyUtils.verifyNotNull(new Object(), "message3");

        // ASSERT
        // no exception throw
    }

    @Test
    void verifyNotNullFalse() {
        // ARRANGE

        // ACT
        var res = assertThrows(IllegalArgumentException.class,
                () -> VerifyUtils.verifyNotNull(null, "message2"));

        // ASSERT
        assertEquals("message2", res.getMessage());
    }
}