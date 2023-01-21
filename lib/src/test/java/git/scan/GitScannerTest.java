package git.scan;

import java.io.IOException;
import java.util.regex.Pattern;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitScannerTest {

  public static Logger log = LoggerFactory.getLogger(GitScannerTest.class);

  @Test
  public void scan() throws IOException {

    String programmers = "Prg_.*.java";
    String leetcode = "Let_.*.java";
    String baekjoon = "Boj_.*.java";
    final int prg = 0;
    final int let = 1;
    final int boj = 2;

    GitScanner gitScanner = new GitScanner();
    gitScanner.scan(
        "/Users/leo/Workspace/study/algorithm-study",
        ".java",
        (git, repository, revWalk, treeWalk) -> {
          String nameString = treeWalk.getNameString();

          int code = 0;
          if (Pattern.matches(programmers, nameString)) {
            code = prg;
          } else if (Pattern.matches(leetcode, nameString)) {
            code = let;
          } else if (Pattern.matches(baekjoon, nameString)) {
            code = boj;
          } else {
            return;
          }

          Iterable<RevCommit> commits = git.log().addPath(treeWalk.getPathString()).call();

          log.info("file name : {}", nameString);
          commits.forEach(
              commit -> {
                log.info(
                    "commit : [{} / {}]",
                    commit.getAuthorIdent().getName(),
                    commit.getAuthorIdent().getWhen());
                log.info("--------------------------------------------------------------");
              });
        });
  }
}
