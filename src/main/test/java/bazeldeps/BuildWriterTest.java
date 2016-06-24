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

public class BuildWriterTest {
  @Test
  public void write() {
    Map<Artifact, Set<Artifact>> dependencies = ImmutableMap.of(
      artifact("g1:a1:v1"), ImmutableSet.of(artifact("g2:a2:v2"), artifact("g3:a3:v3")),
      artifact("g4:a4:v4"), ImmutableSet.of(artifact("g5:a5:v5")));

    String expected = "java_library(\n"
                      + "  name=\"a1\",\n"
                      + "  visibility = [\"//visibility:public\"],\n"
                      + "  exports = [\n"
                      + "    \"@g2_a2//jar\",\n"
                      + "    \"@g3_a3//jar\",\n"
                      + "  ],\n"
                      + ")\n"
                      + "\n"
                      + "java_library(\n"
                      + "  name=\"a4\",\n"
                      + "  visibility = [\"//visibility:public\"],\n"
                      + "  exports = [\n"
                      + "    \"@g5_a5//jar\",\n"
                      + "  ],\n"
                      + ")\n\n";

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BuildWriter.write(new PrintStream(byteArrayOutputStream), dependencies);
    String actual = new String(byteArrayOutputStream.toByteArray());

    assertEquals(expected, actual);
  }

  private Artifact artifact(String artifact) {
    return new DefaultArtifact(artifact);
  }
}
