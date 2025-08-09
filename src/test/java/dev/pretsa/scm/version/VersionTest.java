package dev.pretsa.scm.version;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VersionTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                "0.0.0", "1.2.3",
            })
    void goodVersion(String version) {
        assertDoesNotThrow(() -> Version.parse(version));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "123", "12.3", "1.2.A", "1.2.3A", "A.2.3", "1A.2.3",
            })
    void badVersion(String version) {
        assertThrows(IllegalArgumentException.class, () -> Version.parse(version));
    }
}
