package dev.pretsa.scm.version;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import org.apache.maven.api.ProtoSession;
import org.junit.jupiter.api.Test;

class VersionPropertyContributorTest {

    VersionProvider dummyVersionProvider = new VersionProvider(repositoryDir -> "1.2.3");

    @Test
    void scmVersionPropertyDefined() {
        // given a VersionPropertyContributor
        VersionPropertyContributor versionPropertyContributor = new VersionPropertyContributor(dummyVersionProvider);

        // when the input user properties have scm.version property present
        Map<String, String> inputUserProperties = Map.of(Configuration.KEY_VERSION, "4.5.6");

        // then output user properties should be the same as input user properties, no calculations are done
        Map<String, String> outputUserProperties =
                versionPropertyContributor.contribute(new TestProtoSession(inputUserProperties));
        assertEquals(inputUserProperties, outputUserProperties);
    }

    @Test
    void delegateToVersionProvider() {
        // given a VersionPropertyContributor
        VersionPropertyContributor versionPropertyContributor = new VersionPropertyContributor(dummyVersionProvider);

        // when the input user properties have valid configuration present
        Map<String, String> inputUserProperties = Map.of();

        // then output user properties should contain version computed by version provider
        Map<String, String> outputUserProperties =
                versionPropertyContributor.contribute(new TestProtoSession(inputUserProperties));
        assertNotNull(outputUserProperties);
        assertEquals("1.2.4-SNAPSHOT", outputUserProperties.get(Configuration.KEY_VERSION));
    }

    record TestProtoSession(Map<String, String> userProperties) implements ProtoSession {

        @Override
        public Map<String, String> getUserProperties() {
            return userProperties;
        }

        @Override
        public Map<String, String> getSystemProperties() {
            return Map.of();
        }

        @Override
        public Instant getStartTime() {
            return null;
        }

        @Override
        public Path getTopDirectory() {
            return null;
        }

        @Override
        public Path getRootDirectory() {
            return null;
        }
    }
}
