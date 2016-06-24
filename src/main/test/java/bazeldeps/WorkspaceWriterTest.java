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
      artifact("g1:a1:v1"), ImmutableSet.of(artifact("g2:a2:v2"), artifact("g3:a3:v3")),
      artifact("g4:a4:v4"), ImmutableSet.of(artifact("g5:a5:v5")));

    String expected = "maven_jar(name = \"g2_a2\", artifact = \"g2:a2:jar:v2\")\n"
                      + "maven_jar(name = \"g3_a3\", artifact = \"g3:a3:jar:v3\")\n"
                      + "maven_jar(name = \"g5_a5\", artifact = \"g5:a5:jar:v5\")\n";

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    WorkspaceWriter.write(new PrintStream(byteArrayOutputStream), dependencies);
    String actual = new String(byteArrayOutputStream.toByteArray());

    assertEquals(expected, actual);
  }

  private Artifact artifact(String artifact) {
    return new DefaultArtifact(artifact);
  }
}
