package dev.pretsa.scm.version;

/**
 * Qualifier for a version, adds suffix to `qualify` them
 */
public enum VersionQualifier {

    /**
     * A release version is a final version, does not have a suffix
     */
    RELEASE(""),

    /**
     * A snapshot version is a work-in-progress version, have "-SNAPSHOT" suffix
     */
    SNAPSHOT("-SNAPSHOT");

    private final String suffix;

    VersionQualifier(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Get qualifier suffix
     * @return suffix
     */
    public String suffix() {
        return suffix;
    }
}
