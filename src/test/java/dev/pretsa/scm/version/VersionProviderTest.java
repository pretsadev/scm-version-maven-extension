package dev.pretsa.scm.version;

import static dev.pretsa.scm.version.Configuration.*;
import static dev.pretsa.scm.version.VersionProviderTest.TestData.of;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class VersionProviderTest {

    static final Path dummyPath = Paths.get("dummy");
    static final String CURRENT_SCM_VERSION = "0.0.0";

    record TestData(String expectedVersion, Configuration configuration) {
        static TestData of(String expectedResolvedVersion, String... keys) {
            Map<String, String> properties = Arrays.stream(keys).collect(Collectors.toMap(key -> key, key -> "true"));
            Configuration config = ConfigurationBuilder.build(dummyPath, properties);
            return new TestData(expectedResolvedVersion, config);
        }
    }

    static List<TestData> nextSnapshot = List.of(
            of("0.0.1-SNAPSHOT", KEY_NEXT),
            of("0.0.1-SNAPSHOT", KEY_NEXT, KEY_SNAPSHOT),
            of("0.0.1-SNAPSHOT", KEY_NEXT, KEY_SNAPSHOT, KEY_PATCH),
            of("0.1.0-SNAPSHOT", KEY_NEXT, KEY_SNAPSHOT, KEY_MINOR),
            of("1.0.0-SNAPSHOT", KEY_NEXT, KEY_SNAPSHOT, KEY_MAJOR));

    static List<TestData> nextSnapshotShorthand = List.of(
            of("0.0.1-SNAPSHOT", KEY_NEXT_PATCH_SNAPSHOT),
            of("0.1.0-SNAPSHOT", KEY_NEXT_MINOR_SNAPSHOT),
            of("1.0.0-SNAPSHOT", KEY_NEXT_MAJOR_SNAPSHOT));

    static List<TestData> nextRelease = List.of(
            of("0.0.1", KEY_NEXT, KEY_RELEASE),
            of("0.0.1", KEY_NEXT, KEY_RELEASE, KEY_PATCH),
            of("0.1.0", KEY_NEXT, KEY_RELEASE, KEY_MINOR),
            of("1.0.0", KEY_NEXT, KEY_RELEASE, KEY_MAJOR));

    static List<TestData> nextReleaseShorthand = List.of(
            of("0.0.1", KEY_NEXT_PATCH_RELEASE),
            of("0.1.0", KEY_NEXT_MINOR_RELEASE),
            of("1.0.0", KEY_NEXT_MAJOR_RELEASE));

    static List<TestData> current = List.of(
            of("0.0.0-SNAPSHOT", KEY_CURRENT),
            of("0.0.0-SNAPSHOT", KEY_CURRENT, KEY_SNAPSHOT),
            of("0.0.0", KEY_CURRENT, KEY_RELEASE));

    @ParameterizedTest
    @FieldSource("nextSnapshot")
    @FieldSource("nextSnapshotShorthand")
    @FieldSource("nextRelease")
    @FieldSource("nextReleaseShorthand")
    @FieldSource("current")
    void validVersion(TestData testData) {
        // when scm return valid version 0.0.0
        VersionProvider versionProvider = new VersionProvider(repositoryDir -> CURRENT_SCM_VERSION);

        // then version matches expected version
        String version = versionProvider.getVersion(testData.configuration());
        assertEquals(testData.expectedVersion(), version);
    }
}
