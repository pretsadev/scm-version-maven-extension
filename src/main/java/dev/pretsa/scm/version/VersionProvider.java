package dev.pretsa.scm.version;

import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compute project version by getting current version from scm via {@link ScmVersionProvider} then computing final version based on given configurations
 */
@Named
@Singleton
public class VersionProvider {

    private static final Logger logger = LoggerFactory.getLogger(VersionProvider.class);

    private final ScmVersionProvider scm;

    /**
     * Construct {@link VersionProvider} passing in {@link ScmVersionProvider} that will be used to get current scm version
     * @param scmVersionProvider provider to get current scm version
     */
    @Inject
    public VersionProvider(ScmVersionProvider scmVersionProvider) {
        this.scm = scmVersionProvider;
    }

    /**
     * Compute version based on given configurations
     * @param configuration configurations
     * @return computed version
     */
    public String getVersion(Configuration configuration) {
        String currentVersionTag = scm.getLatestVersion(configuration.topDirectory());
        SemanticVersion currentVersion = SemanticVersion.parse(currentVersionTag);
        SemanticVersion finalVersion =
                switch (configuration.versionType()) {
                    case CURRENT -> currentVersion;
                    case NEXT -> currentVersion.next(configuration.nextVersionComponent());
                };

        String finalVersionString =
                finalVersion.formatted(configuration.versionQualifier().suffix());
        logger.atInfo()
                .addArgument(Configuration.KEY_VERSION)
                .addArgument(finalVersionString)
                .addArgument(configuration.describe())
                .log("{} property computed to [{}] as the [{}]");

        return finalVersionString;
    }
}
