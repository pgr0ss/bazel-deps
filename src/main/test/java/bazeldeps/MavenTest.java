package bazeldeps;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MavenTest {

  @Test
  public void transitiveDependencies() {
    assertEquals(ImmutableSet.of("org.hamcrest:hamcrest-core:jar:1.3", "junit:junit:jar:4.12"),
                 artifactStrings(Maven.transitiveDependencies(new DefaultArtifact("junit:junit:4.12"))));
  }

  private Set<String> artifactStrings(Set<Artifact> artifacts) {
    return artifacts.stream().map(Artifact::toString).collect(Collectors.toSet());
  }
}
