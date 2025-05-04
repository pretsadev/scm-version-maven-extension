package dev.pretsa.scm.version;

import java.nio.file.Path;

/**
 * Provides version from a scm system
 */
public interface ScmVersionProvider {

    /**
     * Given a path to a scm repository, find the latest version from scm tags
     * @param repositoryDir scm repository path
     * @return latest semantic version tag
     */
    String getLatestVersion(Path repositoryDir);
}
