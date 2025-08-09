package dev.pretsa.scm.version;

import java.nio.file.Path;

/**
 * Provides version tag from a scm system
 */
public interface ScmTagProvider {

    /**
     * Given a path to a scm repository, find the latest version tag from scm
     * @param directory scm repository directory path
     * @return latest version tag
     */
    String getLatestTag(Path directory);
}
