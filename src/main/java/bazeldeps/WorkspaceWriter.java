package bazeldeps;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.eclipse.aether.artifact.Artifact;

public class WorkspaceWriter {
  public static void write(PrintStream out, Map<Artifact, Set<Artifact>> dependencies) {
    dependencies.values().stream()
      .flatMap(Collection::stream)
      .sorted(Comparator.comparing(Artifact::getArtifactId))
      .forEach(artifact -> {
        out.format("maven_jar(name = \"%s\", artifact = \"%s\")\n", Writer.artifactName(artifact),
                   artifact.toString());
      });
  }
}
