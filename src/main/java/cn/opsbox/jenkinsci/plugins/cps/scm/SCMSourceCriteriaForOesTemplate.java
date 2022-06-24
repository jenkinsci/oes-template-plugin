package cn.opsbox.jenkinsci.plugins.cps.scm;

import hudson.model.TaskListener;
import jenkins.scm.api.SCMProbeStat;
import jenkins.scm.api.SCMSourceCriteria;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class SCMSourceCriteriaForOesTemplate {

  public static boolean matches(String localMarker, SCMSourceCriteria.Probe probe, TaskListener taskListener) throws IOException {
    // Match all if local file is not specified
    if (StringUtils.isEmpty(localMarker)) {
      taskListener.getLogger().println("No local file defined. Skipping Source Code SCM probe, since Jenkinsfile will be provided by Remote Jenkins File Plugin");
      return true;
    }

    SCMProbeStat stat = probe.stat(localMarker);
    switch (stat.getType()) {
      case NONEXISTENT:
        if (stat.getAlternativePath() != null) {
          taskListener.getLogger().format("      ‘%s’ not found (but found ‘%s’, search is case sensitive)%n", localMarker, stat.getAlternativePath());
        } else {
          taskListener.getLogger().format("      ‘%s’ not found%n", localMarker);
        }
        return false;
      case DIRECTORY:
        taskListener.getLogger().format("      ‘%s’ found directory%n", localMarker);
        return true;
      default:
        taskListener.getLogger().format("      ‘%s’ found%n", localMarker);
        return true;
    }
  }
}
