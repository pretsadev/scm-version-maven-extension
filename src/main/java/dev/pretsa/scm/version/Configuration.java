package dev.pretsa.scm.version;

import java.nio.file.Path;

/**
 * Configuration needed to load current version from scm and resolve version
 * @param topDirectory scm repository path
 * @param versionType version type
 * @param nextVersionComponent next version component, only valid when version type is {@link VersionType#NEXT}
 * @param versionQualifier version qualifier
 */
public record Configuration(
        Path topDirectory,
        VersionType versionType,
        SemanticVersion.Component nextVersionComponent,
        VersionQualifier versionQualifier) {

    /**
     * Property key for final version to be used as project version
     */
    public static final String KEY_VERSION = "scm.version";

    /**
     * Property key denoting version type {@link VersionType#CURRENT}
     */
    public static final String KEY_CURRENT = "scm.current";

    /**
     * Property key denoting version type {@link VersionType#NEXT}
     */
    public static final String KEY_NEXT = "scm.next";

    /**
     * Property key denoting next version component {@link SemanticVersion.Component#MAJOR}
     */
    public static final String KEY_MAJOR = "scm.major";

    /**
     * Property key denoting next version component {@link SemanticVersion.Component#MINOR}
     */
    public static final String KEY_MINOR = "scm.minor";

    /**
     * Property key denoting next version component {@link SemanticVersion.Component#PATCH}
     */
    public static final String KEY_PATCH = "scm.patch";

    /**
     * Property key denoting version qualifier {@link VersionQualifier#RELEASE}
     */
    public static final String KEY_RELEASE = "scm.release";

    /**
     * Property key denoting version qualifier {@link VersionQualifier#SNAPSHOT}
     */
    public static final String KEY_SNAPSHOT = "scm.snapshot";

    /**
     * Property key denoting version type {@link VersionType#NEXT}, next version component {@link SemanticVersion.Component#MAJOR} and version qualifier {@link VersionQualifier#RELEASE}
     */
    public static final String KEY_NEXT_MAJOR_RELEASE = "scm.next.major.release";

    /**
     * Property key denoting version type {@link VersionType#NEXT}, next version component {@link SemanticVersion.Component#MINOR} and version qualifier {@link VersionQualifier#RELEASE}
     */
    public static final String KEY_NEXT_MINOR_RELEASE = "scm.next.minor.release";

    /**
     * Property key denoting version type {@link VersionType#NEXT}, next version component {@link SemanticVersion.Component#PATCH} and version qualifier {@link VersionQualifier#RELEASE}
     */
    public static final String KEY_NEXT_PATCH_RELEASE = "scm.next.patch.release";

    /**
     * Property key denoting version type {@link VersionType#NEXT}, next version component {@link SemanticVersion.Component#MAJOR} and version qualifier {@link VersionQualifier#SNAPSHOT}
     */
    public static final String KEY_NEXT_MAJOR_SNAPSHOT = "scm.next.major.snapshot";

    /**
     * Property key denoting version type {@link VersionType#NEXT}, next version component {@link SemanticVersion.Component#MINOR} and version qualifier {@link VersionQualifier#SNAPSHOT}
     */
    public static final String KEY_NEXT_MINOR_SNAPSHOT = "scm.next.minor.snapshot";

    /**
     * Property key denoting version type {@link VersionType#NEXT}, next version component {@link SemanticVersion.Component#PATCH} and version qualifier {@link VersionQualifier#SNAPSHOT}
     */
    public static final String KEY_NEXT_PATCH_SNAPSHOT = "scm.next.patch.snapshot";

    /**
     * Short configuration description text
     * @return configuration description
     */
    public String describe() {
        return switch (versionType) {
            case CURRENT -> "%s %s".formatted(versionType.name(), versionQualifier.name());
            case NEXT -> "%s %s %s".formatted(versionType.name(), nextVersionComponent.name(), versionQualifier.name());
        };
    }
}
