package dev.pretsa.scm.version;

import static dev.pretsa.scm.version.Configuration.*;
import static dev.pretsa.scm.version.Configuration.KEY_MINOR;
import static dev.pretsa.scm.version.Configuration.KEY_RELEASE;
import static dev.pretsa.scm.version.Configuration.KEY_SNAPSHOT;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class ConfigurationBuilderTest {

    static final Path dummyPath = Paths.get("dummy");

    static List<List<String>> versionType = List.of(List.of(KEY_CURRENT), List.of(KEY_NEXT));

    static List<List<String>> versionComponent = List.of(List.of(KEY_MAJOR), List.of(KEY_MINOR), List.of(KEY_PATCH));

    static List<List<String>> versionQualifier = List.of(List.of(KEY_RELEASE), List.of(KEY_SNAPSHOT));

    static List<List<String>> versionTypeWithComponent =
            List.of(List.of(KEY_NEXT, KEY_MAJOR), List.of(KEY_NEXT, KEY_MINOR), List.of(KEY_NEXT, KEY_PATCH));

    static List<List<String>> versionTypeWithQualifier = List.of(
            List.of(KEY_CURRENT, KEY_SNAPSHOT),
            List.of(KEY_CURRENT, KEY_RELEASE),
            List.of(KEY_NEXT, KEY_SNAPSHOT),
            List.of(KEY_NEXT, KEY_RELEASE));

    static List<List<String>> versionComponentWithQualifier = List.of(
            List.of(KEY_MAJOR, KEY_SNAPSHOT),
            List.of(KEY_MAJOR, KEY_RELEASE),
            List.of(KEY_MINOR, KEY_SNAPSHOT),
            List.of(KEY_MINOR, KEY_RELEASE),
            List.of(KEY_PATCH, KEY_SNAPSHOT),
            List.of(KEY_PATCH, KEY_RELEASE));

    static List<List<String>> versionTypeWithQualifierAndComponent = List.of(
            List.of(KEY_NEXT, KEY_SNAPSHOT, KEY_MAJOR),
            List.of(KEY_NEXT, KEY_SNAPSHOT, KEY_MINOR),
            List.of(KEY_NEXT, KEY_SNAPSHOT, KEY_PATCH),
            List.of(KEY_NEXT, KEY_RELEASE, KEY_MAJOR),
            List.of(KEY_NEXT, KEY_RELEASE, KEY_MINOR),
            List.of(KEY_NEXT, KEY_RELEASE, KEY_PATCH));

    static List<List<String>> versionShorthand = List.of(
            List.of(KEY_NEXT_MAJOR_SNAPSHOT),
            List.of(KEY_NEXT_MINOR_SNAPSHOT),
            List.of(KEY_NEXT_PATCH_SNAPSHOT),
            List.of(KEY_NEXT_MAJOR_RELEASE),
            List.of(KEY_NEXT_MINOR_RELEASE),
            List.of(KEY_NEXT_PATCH_RELEASE));

    @ParameterizedTest
    @FieldSource("versionType")
    @FieldSource("versionComponent")
    @FieldSource("versionQualifier")
    @FieldSource("versionTypeWithComponent")
    @FieldSource("versionTypeWithQualifier")
    @FieldSource("versionComponentWithQualifier")
    @FieldSource("versionTypeWithQualifierAndComponent")
    @FieldSource("versionShorthand")
    void GoodInput(List<String> keys) {
        // given configuration properties input by user
        Map<String, String> properties = keys.stream().collect(Collectors.toMap(key -> key, key -> "true"));

        // when building configuration then good input is acceptable
        assertDoesNotThrow(() -> ConfigurationBuilder.build(dummyPath, properties));
    }

    static List<List<String>> versionTypeConflict = List.of(
            List.of(KEY_CURRENT, KEY_NEXT),
            List.of(KEY_CURRENT, KEY_NEXT_MAJOR_RELEASE),
            List.of(KEY_CURRENT, KEY_NEXT_MINOR_RELEASE),
            List.of(KEY_CURRENT, KEY_NEXT_PATCH_RELEASE),
            List.of(KEY_CURRENT, KEY_NEXT_MAJOR_SNAPSHOT),
            List.of(KEY_CURRENT, KEY_NEXT_MINOR_SNAPSHOT),
            List.of(KEY_CURRENT, KEY_NEXT_PATCH_SNAPSHOT),
            List.of(KEY_CURRENT, KEY_MAJOR),
            List.of(KEY_CURRENT, KEY_MINOR),
            List.of(KEY_CURRENT, KEY_PATCH));

    static List<List<String>> versionComponentConflict = List.of(
            List.of(KEY_MAJOR, KEY_MINOR),
            List.of(KEY_MAJOR, KEY_PATCH),
            List.of(KEY_MINOR, KEY_PATCH),
            List.of(KEY_MAJOR, KEY_MINOR, KEY_PATCH),
            List.of(KEY_MAJOR, KEY_NEXT_MINOR_SNAPSHOT),
            List.of(KEY_MAJOR, KEY_NEXT_PATCH_SNAPSHOT),
            List.of(KEY_MAJOR, KEY_NEXT_MINOR_RELEASE),
            List.of(KEY_MAJOR, KEY_NEXT_PATCH_RELEASE),
            List.of(KEY_MINOR, KEY_NEXT_MAJOR_SNAPSHOT),
            List.of(KEY_MINOR, KEY_NEXT_PATCH_SNAPSHOT),
            List.of(KEY_MINOR, KEY_NEXT_MAJOR_RELEASE),
            List.of(KEY_MINOR, KEY_NEXT_PATCH_RELEASE),
            List.of(KEY_PATCH, KEY_NEXT_MINOR_SNAPSHOT),
            List.of(KEY_PATCH, KEY_NEXT_MAJOR_SNAPSHOT),
            List.of(KEY_PATCH, KEY_NEXT_MINOR_RELEASE),
            List.of(KEY_PATCH, KEY_NEXT_MAJOR_RELEASE));

    static List<List<String>> versionQualifierConflict = List.of(List.of(KEY_RELEASE, KEY_SNAPSHOT));

    @ParameterizedTest
    @FieldSource("versionTypeConflict")
    @FieldSource("versionComponentConflict")
    @FieldSource("versionQualifierConflict")
    void badInput(List<String> keys) {
        // given configuration properties input by user
        Map<String, String> properties = keys.stream().collect(Collectors.toMap(key -> key, key -> "true"));

        // when building configuration then bad input throws exception
        assertThrows(IllegalArgumentException.class, () -> ConfigurationBuilder.build(dummyPath, properties));
    }
}
