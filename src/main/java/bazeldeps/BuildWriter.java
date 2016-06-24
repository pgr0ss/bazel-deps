package bazeldeps;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import org.eclipse.aether.artifact.Artifact;

public class BuildWriter {
  public static void write(PrintStream out, Map<Artifact, Set<Artifact>> dependencies) {
    dependencies.entrySet().stream()
      .sorted((e1, e2) -> e1.getKey().getArtifactId().compareTo(e2.getKey().getArtifactId()))
      .forEach(entry -> printForBuildFile(out, entry.getKey(), entry.getValue()));
  }

  private static void printForBuildFile(PrintStream out, Artifact artifact, Set<Artifact> dependencies) {
    out.println("java_library(");
    out.println("  name=\"" + artifact.getArtifactId() + "\",");
    out.println("  visibility = [\"//visibility:public\"],");
    out.println("  exports = [");

    dependencies.stream()
      .map(d -> String.format("    \"@%s//jar\",", Writer.artifactName(d)))
      .sorted()
      .forEach(out::println);

    out.println("  ],");
    out.println(")\n");
  }

}
