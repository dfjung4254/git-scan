package git.scan;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitScanner {

  public static Logger log = LoggerFactory.getLogger(GitScanner.class);

  public void scan(String repoPath, String extension, TreeWalkMapper mapper) throws IOException {

    File file = new File(repoPath);
    try (Git git = Git.open(file)) {
      git.checkout()
          .setName("main")
          .call();
      Repository repository = git.getRepository();
      ObjectId lastCommitId = repository.resolve(Constants.HEAD);

      try (RevWalk revWalk = new RevWalk(repository)) {
        RevCommit commit = revWalk.parseCommit(lastCommitId);
        RevTree tree = commit.getTree();

        try (TreeWalk treeWalk = new TreeWalk(repository)) {
          treeWalk.addTree(tree);
          treeWalk.setRecursive(true);
          treeWalk.setFilter(PathSuffixFilter.create(extension));

          while (treeWalk.next()) {
            mapper.apply(git, repository, revWalk, treeWalk);
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FunctionalInterface
  public interface TreeWalkMapper {

    void apply(Git git, Repository repository, RevWalk revWalk, TreeWalk treeWalk)
        throws IOException, GitAPIException;
  }
}
