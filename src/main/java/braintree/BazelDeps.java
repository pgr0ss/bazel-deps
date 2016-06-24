package braintree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class BazelDeps {

  public static void main(String[] args) throws DependencyCollectionException, CmdLineException {
    new BazelDeps().doMain(args);
  }

  public void doMain(String[] args) throws DependencyCollectionException, CmdLineException {
    Configuration config = new Configuration();
    CmdLineParser parser = new CmdLineParser(config);
    parser.parseArgument(args);

    if (config.artifactNames.isEmpty()) {
      System.out.print("Usage: java -jar bazel-deps-1.0-SNAPSHOT");
      parser.printSingleLineUsage(System.out);
      System.out.println();
      parser.printUsage(System.out);
      System.out.println(
        "\nExample: java -jar bazel-deps-1.0-SNAPSHOT com.fasterxml.jackson.core:jackson-databind:2.5.0");
      System.exit(1);
    }

    System.err.println("Fetching dependencies from maven...\n");

    Map<Artifact, Set<Artifact>> dependencies = fetchDependencies(config.artifactNames);

    Set<Artifact> excludeDependencies =
      config.excludeArtifact != null ? Maven.transitiveDependencies(new DefaultArtifact(config.excludeArtifact))
                                     : ImmutableSet.of();

    printWorkspace(dependencies, excludeDependencies);
    printBuildEntries(dependencies, excludeDependencies);
  }

  private Map<Artifact, Set<Artifact>> fetchDependencies(List<String> artifactNames) {
    Map<Artifact, Set<Artifact>> dependencies = new HashMap<>();

    artifactNames.stream()
      .map(DefaultArtifact::new)
      .forEach(artifact -> dependencies.put(artifact, Maven.transitiveDependencies(artifact)));
    return dependencies;
  }

  private void printWorkspace(Map<Artifact, Set<Artifact>> dependencies,
                              Set<Artifact> excludeDependencies) {
    System.out.println("\n\n--------- Add these lines to your WORKSPACE file ---------\n");
    dependencies.values().stream()
      .flatMap(Collection::stream)
      .filter(artifact -> !excludeDependencies.contains(artifact))
      .sorted(Comparator.comparing(Artifact::getArtifactId))
      .forEach(artifact -> {
        System.out.format("maven_jar(name = \"%s\", artifact = \"%s\")\n", artifactName(artifact),
                          artifact.toString());
      });
  }

  private void printBuildEntries(Map<Artifact, Set<Artifact>> dependencies,
                                 Set<Artifact> excludeDependencies) {
    System.out.println("\n\n--------- Add these lines to your BUILD file ---------\n");
    dependencies.entrySet().stream()
      .sorted((e1, e2) -> e1.getKey().getArtifactId().compareTo(e2.getKey().getArtifactId()))
      .forEach(entry -> printForBuildFile(entry.getKey(), entry.getValue(), excludeDependencies));
  }

  private static void printForBuildFile(Artifact artifact, Set<Artifact> dependencies,
                                        Set<Artifact> excludeDependencies) {
    System.out.println("java_library(");
    System.out.println("  name=\"" + artifact.getArtifactId() + "\",");
    System.out.println("  visibility = [\"//visibility:public\"],");
    System.out.println("  exports = [");

    Sets.difference(dependencies, excludeDependencies).stream()
      .map(d -> String.format("    \"@%s//jar\",", artifactName(d)))
      .sorted()
      .forEach(System.out::println);

    System.out.println("  ],");
    System.out.println(")\n");
  }

  private static String artifactName(Artifact artifact) {
    return sanitizeName(artifact.getGroupId()) + "_" + sanitizeName(artifact.getArtifactId());
  }

  private static String sanitizeName(String name) {
    return name.replaceAll("[-.]", "_");
  }
}
