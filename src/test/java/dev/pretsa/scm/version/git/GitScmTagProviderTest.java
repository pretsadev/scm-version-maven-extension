package dev.pretsa.scm.version.git;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GitScmTagProviderTest {

    @TempDir
    Path tempDir;

    GitScmTagProvider gitScmVersionProvider = new GitScmTagProvider();

    @ParameterizedTest
    @ValueSource(
            strings = {
                "1.2.3",
                "v1.2.3",
                "myapp-1.2.3",
                "myapp-v1.2.3",
            })
    void repoWithSemanticVersionTag(String tag) throws GitAPIException {
        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("First commit").call();
            git.tag()
                    .setAnnotated(true)
                    .setName(tag)
                    .setMessage("First release")
                    .call();

            String version = gitScmVersionProvider.getLatestTag(tempDir);
            assertEquals("1.2.3", version);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "1.2.3",
                "v1.2.3",
                "myapp-1.2.3",
                "myapp-v1.2.3",
            })
    void repoWithMultipleTags(String tag) throws GitAPIException {
        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("First commit").call();
            git.tag()
                    .setAnnotated(true)
                    .setName("1.0.0")
                    .setMessage("First release")
                    .call();

            git.commit().setMessage("Second commit").setAllowEmpty(true).call();
            git.tag()
                    .setAnnotated(true)
                    .setName("1.1.0")
                    .setMessage("Second release")
                    .call();

            git.commit().setMessage("Third commit").setAllowEmpty(true).call();
            git.tag()
                    .setAnnotated(true)
                    .setName("non-semantic")
                    .setMessage("Other tag")
                    .call();

            git.commit().setMessage("Forth commit").setAllowEmpty(true).call();
            git.tag().setAnnotated(false).setName("non-annotated").call();

            git.commit().setMessage("Fifth commit").setAllowEmpty(true).call();
            git.tag()
                    .setAnnotated(true)
                    .setName(tag)
                    .setMessage("Third release")
                    .call();

            String version = gitScmVersionProvider.getLatestTag(tempDir);
            assertEquals("1.2.3", version);
        }
    }

    @Test
    void repoWithMultipleTagsOnSameCommit() throws GitAPIException, InterruptedException {
        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("First commit").call();
            git.tag()
                    .setAnnotated(true)
                    .setName("1.2.3")
                    .setMessage("First release")
                    .call();

            // wait for time to flip to the next second before creating the next tag
            Awaitility.await()
                    .atMost(2, TimeUnit.SECONDS)
                    .until(() ->
                            git.log().call().iterator().next().getCommitTime() < System.currentTimeMillis() / 1000);

            git.tag()
                    .setAnnotated(true)
                    .setName("1.2.4")
                    .setMessage("Second release")
                    .call();

            String version = gitScmVersionProvider.getLatestTag(tempDir);
            assertEquals("1.2.4", version);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "1.2.3",
                "v1.2.3",
                "myapp-1.2.3",
                "myapp-v1.2.3",
            })
    void repoWithNonAnnotatedSemanticVersionTag(String tag) throws GitAPIException {
        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("First commit").call();
            git.tag().setAnnotated(false).setName(tag).call();

            String version = gitScmVersionProvider.getLatestTag(tempDir);
            assertEquals("1.2.3", version);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "1.23",
                "v1.23",
                "myapp-1.23",
                "myapp-v1.23",
            })
    void repoWithNonSemanticVersionTag(String tag) throws GitAPIException {
        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("First commit").call();
            git.tag()
                    .setAnnotated(true)
                    .setName(tag)
                    .setMessage("First release")
                    .call();

            String version = gitScmVersionProvider.getLatestTag(tempDir);
            assertEquals("0.0.0", version);
        }
    }

    @Test
    void repoWithoutTag() throws GitAPIException {
        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("First commit").call();

            String version = gitScmVersionProvider.getLatestTag(tempDir);
            assertEquals("0.0.0", version);
        }
    }

    @Test
    void repoWithoutHead() throws GitAPIException {
        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {

            String version = gitScmVersionProvider.getLatestTag(tempDir);
            assertEquals("0.0.0", version);
        }
    }

    @Test
    void notRepo() {
        String version = gitScmVersionProvider.getLatestTag(tempDir);
        assertEquals("0.0.0", version);
    }
}
