package dev.pretsa.scm.version;

/**
 * Version components
 * @param major version {@link Component#MAJOR} component
 * @param minor version {@link Component#MINOR} component
 * @param patch version {@link Component#PATCH} component
 */
public record Version(int major, int minor, int patch) {

    /**
     * version component
     */
    public enum Component {
        /**
         * The major component of a version
         */
        MAJOR,

        /**
         * The minor component of a version
         */
        MINOR,

        /**
         * The patch component of a version
         */
        PATCH
    }

    /**
     * Parse a string representation of a version into {@link Version} instance
     * @param version version string
     * @return {@link Version} instance
     */
    public static Version parse(String version) {
        String[] splitRevision = version.split("\\.");
        if (splitRevision.length != 3) {
            throw new IllegalArgumentException("Not a valid version");
        }

        int major = Integer.parseInt(splitRevision[0]);
        int minor = Integer.parseInt(splitRevision[1]);
        int patch = Integer.parseInt(splitRevision[2]);

        return new Version(major, minor, patch);
    }

    /**
     * Compute next version incrementing a given component
     * @param component version component to increment
     * @return computed {@link Version} instance
     */
    public Version next(Component component) {
        return switch (component) {
            case MAJOR -> new Version(major + 1, 0, 0);
            case MINOR -> new Version(major, minor + 1, 0);
            case PATCH -> new Version(major, minor, patch + 1);
        };
    }

    /**
     * Format version
     * @return formatted version
     */
    public String formatted() {
        return "%s.%s.%s".formatted(major, minor, patch);
    }

    /**
     * Format version adding a suffix
     * @param suffix suffix to be appended
     * @return formated version with suffix appended
     */
    public String formatted(String suffix) {
        return "%s%s".formatted(formatted(), suffix);
    }
}
