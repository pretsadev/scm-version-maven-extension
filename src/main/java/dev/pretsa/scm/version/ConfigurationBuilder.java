package dev.pretsa.scm.version;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for {@link Configuration} that accepts a map of properties, parses it to compose a configuration instance
 */
public class ConfigurationBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBuilder.class);

    private static final String VERSION_TYPE = "version type";
    private static final String NEXT_VERSION_COMPONENT = "next version component";
    private static final String VERSION_QUALIFIER = "version qualifier";

    private ConfigurationBuilder() {}

    /**
     * Build a {@link Configuration} with the given path and properties
     * @param directory scm repository directory path
     * @param properties input properties
     * @return configuration instance
     */
    public static Configuration build(Path directory, Map<String, String> properties) {
        logger.atDebug().addArgument(directory).log("Working directory is {}");

        VersionType versionType = getVersionType(properties.keySet());
        Version.Component nextVersionComponent = getNextVersionComponent(versionType, properties.keySet());
        VersionQualifier versionQualifier = getVersionQualifier(properties.keySet());

        return new Configuration(directory, versionType, nextVersionComponent, versionQualifier);
    }

    private static VersionType getVersionType(Set<String> keys) {
        boolean containsNext = containsNext(keys);
        boolean containsCurrent = containsCurrent(keys);

        if (containsNext && containsCurrent) {
            throw new IllegalArgumentException("Multiple version types present, only one is allowed [NEXT, CURRENT].");
        }

        if (containsNext) {
            logUserDefinedProperty(VERSION_TYPE, VersionType.NEXT.name());
            return VersionType.NEXT;
        }

        if (containsCurrent) {
            logUserDefinedProperty(VERSION_TYPE, VersionType.CURRENT.name());
            return VersionType.CURRENT;
        }

        logDefaultProperty(VERSION_TYPE, VersionType.NEXT.name());
        return VersionType.NEXT;
    }

    private static boolean containsCurrent(Set<String> keys) {
        return keys.contains(Configuration.KEY_CURRENT);
    }

    private static boolean containsNext(Set<String> keys) {
        return keys.contains(Configuration.KEY_NEXT)
                || keys.contains(Configuration.KEY_NEXT_MAJOR_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_MINOR_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_PATCH_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_MAJOR_SNAPSHOT)
                || keys.contains(Configuration.KEY_NEXT_MINOR_SNAPSHOT)
                || keys.contains(Configuration.KEY_NEXT_PATCH_SNAPSHOT);
    }

    private static Version.Component getNextVersionComponent(VersionType versionType, Set<String> keys) {
        boolean containsMajor = containsMajor(keys);
        boolean containsMinor = containsMinor(keys);
        boolean containsPatch = containsPatch(keys);

        if ((containsMajor && containsMinor) || ((containsMajor || containsMinor) && containsPatch)) {
            throw new IllegalArgumentException(
                    "Multiple NEXT version components present, only one is allowed [MAJOR, MINOR, PATCH].");
        }

        if (versionType == VersionType.CURRENT && (containsMajor || containsMinor || containsPatch)) {
            throw new IllegalArgumentException("Version NEXT component present, but version type is CURRENT.");
        }

        if (containsMajor) {
            logUserDefinedProperty(NEXT_VERSION_COMPONENT, Version.Component.MAJOR.name());
            return Version.Component.MAJOR;
        }
        if (containsMinor(keys)) {
            logUserDefinedProperty(NEXT_VERSION_COMPONENT, Version.Component.MINOR.name());
            return Version.Component.MINOR;
        }
        if (containsPatch(keys)) {
            logUserDefinedProperty(NEXT_VERSION_COMPONENT, Version.Component.PATCH.name());
            return Version.Component.PATCH;
        }

        logDefaultProperty(NEXT_VERSION_COMPONENT, Version.Component.PATCH.name());
        return Version.Component.PATCH;
    }

    private static boolean containsMajor(Set<String> keys) {
        return keys.contains(Configuration.KEY_MAJOR)
                || keys.contains(Configuration.KEY_NEXT_MAJOR_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_MAJOR_SNAPSHOT);
    }

    private static boolean containsMinor(Set<String> keys) {
        return keys.contains(Configuration.KEY_MINOR)
                || keys.contains(Configuration.KEY_NEXT_MINOR_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_MINOR_SNAPSHOT);
    }

    private static boolean containsPatch(Set<String> keys) {
        return keys.contains(Configuration.KEY_PATCH)
                || keys.contains(Configuration.KEY_NEXT_PATCH_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_PATCH_SNAPSHOT);
    }

    private static VersionQualifier getVersionQualifier(Set<String> keys) {
        boolean containsRelease = containsRelease(keys);
        boolean containsSnapshot = containsSnapshot(keys);

        if (containsRelease && containsSnapshot) {
            throw new IllegalArgumentException(
                    "Multiple version qualifiers present, only one is allowed [RELEASE, SNAPSHOT].");
        }

        if (containsRelease) {
            logUserDefinedProperty(VERSION_QUALIFIER, VersionQualifier.RELEASE.name());
            return VersionQualifier.RELEASE;
        }
        if (containsSnapshot(keys)) {
            logUserDefinedProperty(VERSION_QUALIFIER, VersionQualifier.SNAPSHOT.name());
            return VersionQualifier.SNAPSHOT;
        }

        logDefaultProperty(VERSION_QUALIFIER, VersionQualifier.SNAPSHOT.name());
        return VersionQualifier.SNAPSHOT;
    }

    private static boolean containsRelease(Set<String> keys) {
        return keys.contains(Configuration.KEY_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_MAJOR_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_MINOR_RELEASE)
                || keys.contains(Configuration.KEY_NEXT_PATCH_RELEASE);
    }

    private static boolean containsSnapshot(Set<String> keys) {
        return keys.contains(Configuration.KEY_SNAPSHOT)
                || keys.contains(Configuration.KEY_NEXT_MAJOR_SNAPSHOT)
                || keys.contains(Configuration.KEY_NEXT_MINOR_SNAPSHOT)
                || keys.contains(Configuration.KEY_NEXT_PATCH_SNAPSHOT);
    }

    private static void logUserDefinedProperty(String key, String value) {
        logger.atDebug().addArgument(key).addArgument(value).log("Found user defined property for {}, value set to {}");
    }

    private static void logDefaultProperty(String key, String value) {
        logger.atDebug().addArgument(key).addArgument(value).log("No user defined property for {}, value set to {}");
    }
}
