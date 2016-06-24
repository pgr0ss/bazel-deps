package bazeldeps;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import static java.lang.System.out;

public class BazelDeps {

  public static void main(String[] args) throws DependencyCollectionException, CmdLineException, FileNotFoundException {
    Configuration config = new Configuration();
    CmdLineParser parser = new CmdLineParser(config);
    parser.parseArgument(args);

    verifyUsage(config, parser);

    System.err.println("Fetching dependencies from maven...\n");

    Map<Artifact, Set<Artifact>> dependencies = fetchDependencies(config.artifactNames);

    BuildWriter.write(getBuildPrintStream(config), dependencies);
    WorkspaceWriter.write(getWorkspacePrintStream(config), dependencies);
  }

  private static void verifyUsage(Configuration config, CmdLineParser parser) {
    if (config.artifactNames.isEmpty()) {
      out.print("Usage: java -jar bazel-deps-1.0-SNAPSHOT");
      parser.printSingleLineUsage(out);
      out.println();
      parser.printUsage(out);
      out.println(
        "\nExample: java -jar bazel-deps-1.0-SNAPSHOT com.fasterxml.jackson.core:jackson-databind:2.5.0");
      System.exit(1);
    }
  }

  private static PrintStream getBuildPrintStream(Configuration config) throws FileNotFoundException {
    if (config.buildOutputFile == null) {
      System.out.println("\n\n--------- Add these lines to your BUILD file ---------\n");
      return System.out;
    } else {
      System.err.println("Writing BUILD file to " + config.buildOutputFile);
      return new PrintStream(new FileOutputStream(config.buildOutputFile));
    }
  }

  private static PrintStream getWorkspacePrintStream(Configuration config) throws FileNotFoundException {
    if (config.workspaceOutputFile == null) {
      System.out.println("\n\n--------- Add these lines to your WORKSPACE file ---------\n");
      return System.out;
    } else {
      System.err.println("Writing WORKSPACE file to " + config.workspaceOutputFile);
      return new PrintStream(new FileOutputStream(config.workspaceOutputFile));
    }
  }

  private static Map<Artifact, Set<Artifact>> fetchDependencies(List<String> artifactNames) {
    Map<Artifact, Set<Artifact>> dependencies = new HashMap<>();

    artifactNames.stream()
      .map(DefaultArtifact::new)
      .forEach(artifact -> dependencies.put(artifact, Maven.transitiveDependencies(artifact)));
    return dependencies;
  }
}
