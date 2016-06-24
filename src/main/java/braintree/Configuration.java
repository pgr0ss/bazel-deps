package braintree;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Configuration {

  @Option(name = "-x", usage = "Exclude a libraries dependencies")
  public String excludeArtifact;

  @Argument(usage = "<artifact id>")
  public List<String> artifactNames = new ArrayList<>();
}
