package parade.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void sum() {
        int actual = Utility.Sum(1,2);
        int expected = 3;

        assertEquals(expected, actual);
    }
}