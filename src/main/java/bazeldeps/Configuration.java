package bazeldeps;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Configuration {

  @Option(name = "--build-output-file", usage = "Where to write the resulting BUILD file", metaVar = "BUILD")
  public String buildOutputFile;

  @Option(name = "--workspace-output-file", usage = "Where to write the resulting WORKSPACE file", metaVar = "WORKSPACE")
  public String workspaceOutputFile;

  @Argument(usage = "<artifact id>")
  public List<String> artifactNames = new ArrayList<>();
}
