package bazeldeps;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WorkspaceWriterTest {
  @Test
  public void write() {
    Map<Artifact, Set<Artifact>> dependencies = ImmutableMap.of(
      artifact("g1:a1:v1"), ImmutableSet.of(artifact("g1:a1:v1"), artifact("g2:a2:v2")),
      artifact("g3:a3:v3"), ImmutableSet.of(artifact("g3:a3:v3")));

    String expected = "maven_jar(name = \"g1_a1\", artifact = \"g1:a1:jar:v1\")\n"
                      + "maven_jar(name = \"g2_a2\", artifact = \"g2:a2:jar:v2\")\n"
                      + "maven_jar(name = \"g3_a3\", artifact = \"g3:a3:jar:v3\")\n";

    assertEquals(expected, writeWorkspace(dependencies));
  }

  @Test
  public void entriesAreUnique() {
    Map<Artifact, Set<Artifact>> dependencies = ImmutableMap.of(
      artifact("g1:a1:v1"), ImmutableSet.of(artifact("g1:a1:v1")),
      artifact("g2:a2:v2"), ImmutableSet.of(artifact("g1:a1:v1"), artifact("g2:a2:v2")));

    String expected = "maven_jar(name = \"g1_a1\", artifact = \"g1:a1:jar:v1\")\n"
                      + "maven_jar(name = \"g2_a2\", artifact = \"g2:a2:jar:v2\")\n";

    String actual = writeWorkspace(dependencies);

    assertEquals(expected, actual);
  }

  private Artifact artifact(String artifact) {
    return new DefaultArtifact(artifact);
  }

  private String writeWorkspace(Map<Artifact, Set<Artifact>> dependencies) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    WorkspaceWriter.write(new PrintStream(byteArrayOutputStream), dependencies);
    return new String(byteArrayOutputStream.toByteArray());
  }

}
