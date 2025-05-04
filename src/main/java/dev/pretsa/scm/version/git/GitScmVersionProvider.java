package dev.pretsa.scm.version.git;

import dev.pretsa.scm.version.ScmVersionProvider;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides version from git scm system
 */
@Singleton
@Named
public class GitScmVersionProvider implements ScmVersionProvider {

    private static final Logger logger = LoggerFactory.getLogger(GitScmVersionProvider.class);

    /**
     * Glob that matches a semantic version with optional v and optional prefix e.g.
     * 1.2.3
     * v1.2.3
     * app-1.2.3
     * app-v1.2.3
     */
    private static final String TAG_WITH_SEMANTIC_VERSION_GLOB = "*[0-9]*.[0-9]*.[0-9]*";

    /**
     * Pattern that matches a semantic version with optional v and optional prefix e.g.
     * 1.2.3
     * v1.2.3
     * app-1.2.3
     * app-v1.2.3
     */
    private static final Pattern TAG_WITH_SEMANTIC_VERSION_PATTERN = Pattern.compile("(.+-)?(v)?(.+)");

    /**
     * Default tag to use when no tag is loaded
     */
    private static final String DEFAULT_TAG = "0.0.0";

    @Override
    public String getLatestVersion(Path gitRepositoryDir) {

        FileRepositoryBuilder repositoryBuilder =
                new FileRepositoryBuilder().readEnvironment().findGitDir(gitRepositoryDir.toFile());
        try (Repository repository = repositoryBuilder.build();
                Git git = Git.wrap(repository)) {

            String tag = git.describe()
                    .setAbbrev(0)
                    .setTags(false)
                    .setMatch(TAG_WITH_SEMANTIC_VERSION_GLOB)
                    .call();
            if (tag == null) {
                throw new IllegalArgumentException(
                        "No git tag matching supported glob [%s]".formatted(TAG_WITH_SEMANTIC_VERSION_GLOB));
            }

            Matcher semanticVersionMatcher = TAG_WITH_SEMANTIC_VERSION_PATTERN.matcher(tag);
            if (semanticVersionMatcher.find()) {
                return semanticVersionMatcher.group(3);
            } else {
                throw new IllegalArgumentException("Git tag [%s] does not match supported pattern [%s]"
                        .formatted(tag, TAG_WITH_SEMANTIC_VERSION_PATTERN.pattern()));
            }

        } catch (Exception e) {
            logger.atWarn()
                    .addArgument(gitRepositoryDir)
                    .addArgument(e.getMessage())
                    .log("Error loading tag at {}: {}");

            logger.atInfo().addArgument(DEFAULT_TAG).log("Using default tag [{}]");
            return DEFAULT_TAG;
        }
    }
}
