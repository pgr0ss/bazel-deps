package bazeldeps;

import org.eclipse.aether.artifact.Artifact;

public class Writer {
  public static String artifactName(Artifact artifact) {
    return sanitizeName(artifact.getGroupId()) + "_" + sanitizeName(artifact.getArtifactId());
  }

  private static String sanitizeName(String name) {
    return name.replaceAll("[-.]", "_");
  }
}
