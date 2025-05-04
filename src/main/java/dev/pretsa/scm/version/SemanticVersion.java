package dev.pretsa.scm.version;

/**
 * Semantic version components
 * @param major Semantic version {@link Component#MAJOR} component
 * @param minor Semantic version {@link Component#MINOR} component
 * @param patch Semantic version {@link Component#PATCH} component
 */
public record SemanticVersion(int major, int minor, int patch) {

    /**
     * Semantic version component
     */
    public enum Component {
        /**
         * The major component of a semantic version
         */
        MAJOR,

        /**
         * The minor component of a semantic version
         */
        MINOR,

        /**
         * The patch component of a semantic version
         */
        PATCH
    }

    /**
     * Parse a string representation of a semantic version into {@link SemanticVersion} instance
     * @param version semantic version string
     * @return {@link SemanticVersion} instance
     */
    public static SemanticVersion parse(String version) {
        String[] splitRevision = version.split("\\.");
        if (splitRevision.length != 3) {
            throw new IllegalArgumentException("Not a semantic version");
        }

        int major = Integer.parseInt(splitRevision[0]);
        int minor = Integer.parseInt(splitRevision[1]);
        int patch = Integer.parseInt(splitRevision[2]);

        return new SemanticVersion(major, minor, patch);
    }

    /**
     * Compute next semantic version incrementing given component
     * @param component semantic version component to increment
     * @return computed {@link SemanticVersion} instance
     */
    public SemanticVersion next(Component component) {
        return switch (component) {
            case MAJOR -> new SemanticVersion(major + 1, 0, 0);
            case MINOR -> new SemanticVersion(major, minor + 1, 0);
            case PATCH -> new SemanticVersion(major, minor, patch + 1);
        };
    }

    /**
     * Format semantic version
     * @return formatted semantic version
     */
    public String formatted() {
        return "%s.%s.%s".formatted(major, minor, patch);
    }

    /**
     * Format semantic version adding a suffix
     * @param suffix suffix to be appended
     * @return formated semantic version with suffix appended
     */
    public String formatted(String suffix) {
        return "%s%s".formatted(formatted(), suffix);
    }
}
