package dev.pretsa.scm.version;

import java.util.HashMap;
import java.util.Map;
import org.apache.maven.api.ProtoSession;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.apache.maven.api.spi.PropertyContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A maven {@link PropertyContributor} to provide version {@link Configuration#KEY_VERSION} property as a user property, delegates actual version computing to {@link VersionProvider}
 */
@Singleton
@Named
public class VersionPropertyContributor implements PropertyContributor {

    private static final Logger logger = LoggerFactory.getLogger(VersionPropertyContributor.class);

    private final VersionProvider versionProvider;

    /**
     * Construct a {@link VersionPropertyContributor} passing in {@link VersionProvider} that will be used to compute version
     * @param versionProvider provider to compute version
     */
    @Inject
    public VersionPropertyContributor(VersionProvider versionProvider) {
        this.versionProvider = versionProvider;
    }

    @Override
    public Map<String, String> contribute(ProtoSession protoSession) {
        try {
            logger.atInfo().log("scm-version-maven-extension loaded");

            if (protoSession.getUserProperties().containsKey(Configuration.KEY_VERSION)) {
                logger.atInfo()
                        .addArgument(Configuration.KEY_VERSION)
                        .addArgument(protoSession.getUserProperties().get(Configuration.KEY_VERSION))
                        .log(
                                "Found user defined property [{}], value set to [{}]. Nothing will be done! and other scm.* properties will be ignored");
                return protoSession.getUserProperties();
            }

            Configuration configuration =
                    ConfigurationBuilder.build(protoSession.getTopDirectory(), protoSession.getUserProperties());
            String version = versionProvider.getVersion(configuration);

            Map<String, String> result = new HashMap<>(protoSession.getUserProperties());
            result.put(Configuration.KEY_VERSION, version);
            return result;
        } catch (Exception e) {
            logger.atError()
                    .addArgument(Configuration.KEY_VERSION)
                    .addArgument(e.getMessage())
                    .log("Error computing {} property, {}");
            return protoSession.getUserProperties();
        }
    }
}
