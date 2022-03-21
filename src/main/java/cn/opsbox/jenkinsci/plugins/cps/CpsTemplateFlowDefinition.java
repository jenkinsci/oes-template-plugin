package cn.opsbox.jenkinsci.plugins.cps;

import hudson.AbortException;
import hudson.Extension;
import hudson.model.*;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import jenkins.scm.api.SCMFileSystem;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.cps.CpsFlowFactoryAction2;
import org.jenkinsci.plugins.workflow.cps.persistence.PersistIn;
import org.jenkinsci.plugins.workflow.flow.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jenkinsci.plugins.workflow.cps.persistence.PersistenceContext.JOB;

@PersistIn(JOB)
public class CpsTemplateFlowDefinition extends FlowDefinition {

  @Getter
  private final SCM scm;
  @Getter
  private final String scriptPath;

  @Getter
  private final String parameters;

  @DataBoundConstructor
  public CpsTemplateFlowDefinition(SCM scm, String scriptPath, String parameters) {
    this.scm = scm;
    this.scriptPath = scriptPath;
    this.parameters = parameters;
  }

  @Override
  public CpsFlowExecution create(
      FlowExecutionOwner owner, TaskListener listener, List<? extends Action> actions)
      throws Exception {

    for (Action a : actions) {
      if (a instanceof CpsFlowFactoryAction2) {
        return ((CpsFlowFactoryAction2) a).create(this, owner, actions);
      }
    }
    Queue.Executable _build = owner.getExecutable();
    if (!(_build instanceof Run)) {
      throw new IOException("can only check out SCM into a Run");
    }
    Run<?,?> build = (Run<?,?>) _build;

    try(SCMFileSystem fs = SCMFileSystem.of(build.getParent(), scm)) {
      if (fs != null) {
        try {
          String script = fs.child(scriptPath).contentAsString();

          Properties paramProps = new Properties();
          try (StringReader stringReader = new StringReader(parameters)) {
            paramProps.load(stringReader);
          } catch (IOException ioe) {
            throw new Exception("Problem occurs on loading content", ioe);
          }

          String jenkinsfile = substitute(script, paramProps);
          listener.getLogger().println("Obtained " + scriptPath + " from " + scm.getKey());

          Queue.Executable exec = owner.getExecutable();
          FlowDurabilityHint hint = (exec instanceof Run) ? DurabilityHintProvider.suggestedFor(((Run)exec).getParent()) : GlobalDefaultFlowDurabilityLevel.getDefaultDurabilityHint();
          return new CpsFlowExecution(jenkinsfile, true, owner, hint);
        } catch (FileNotFoundException e) {
          throw new AbortException("Unable to find " + scriptPath + " from " + scm.getKey());
        }
      }
    }

    return null;
  }

  public String substitute(String template, Properties parameters) {

    Pattern p = Pattern.compile("\\{\\{\\s*([^\\}]+)\\s*\\}}");
    String input = StringUtils.trimToEmpty(template);

    if (input.isEmpty()) {
      return input;
    }

    StringBuffer sb = new StringBuffer();
    Matcher m = p.matcher(input);
    while (m.find()) {
      String[] parts = m.group(1).split(":-");

      String key = parts[0].trim();
      String _default = parts.length > 1 ? parts[1] : null;
      String v = (String) parameters.getOrDefault(key, _default);

      if (v == null) {
        v = "";
      }

      m.appendReplacement(sb, v);
    }

    m.appendTail(sb);
    return sb.toString();
  }


  @Extension
  public static class DescriptorImpl extends FlowDefinitionDescriptor {
    @Override
    @Nonnull
    public String getDisplayName() {
      return "Pipeline script from Template";
    }

    public Collection<? extends SCMDescriptor<?>> getApplicableDescriptors() {
      StaplerRequest req = Stapler.getCurrentRequest();
      Job<?, ?> job = req != null ? req.findAncestorObject(Job.class) : null;
      return SCM._for(job);
    }
  }
}
