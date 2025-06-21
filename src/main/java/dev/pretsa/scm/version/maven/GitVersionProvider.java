package dev.pretsa.scm.version.maven;

import dev.pretsa.scm.version.ScmTagProvider;
import dev.pretsa.scm.version.VersionProvider;
import dev.pretsa.scm.version.git.GitScmTagProvider;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;

/**
 * Compute project version by getting current version from scm via {@link ScmTagProvider} then computing final version based on given configurations
 */
@Named
@Singleton
public class GitVersionProvider extends VersionProvider {

    public GitVersionProvider() {
        super(new GitScmTagProvider());
    }
}
