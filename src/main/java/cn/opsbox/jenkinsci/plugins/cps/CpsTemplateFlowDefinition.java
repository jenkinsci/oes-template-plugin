package cn.opsbox.jenkinsci.plugins.cps;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.*;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import jenkins.scm.api.SCMFileSystem;
import lombok.Getter;
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
import java.util.Map;
import java.util.Properties;

import static org.jenkinsci.plugins.workflow.cps.persistence.PersistenceContext.JOB;

@PersistIn(JOB)
public class CpsTemplateFlowDefinition extends FlowDefinition {

  @Getter
  private final SCM scm;
  @Getter
  private final String templateId;

  @Getter
  private final String templateConfig;

  @DataBoundConstructor
  public CpsTemplateFlowDefinition(SCM scm, String templateId, String templateConfig) {
    this.scm = scm;
    this.templateId = templateId;
    this.templateConfig = templateConfig;
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

    String scriptPath = String.format("%s/Jenkinsfile", templateId);

    try(SCMFileSystem fs = SCMFileSystem.of(build.getParent(), scm)) {
      if (fs != null) {
        try {
          String script = fs.child(scriptPath).contentAsString();

          Properties properties = new Properties();
          try (StringReader stringReader = new StringReader(templateConfig)) {
            properties.load(stringReader);
          } catch (IOException ioe) {
            throw new Exception("Problem occurs on loading content", ioe);
          }

          EnvVars envVars = ((Run<?, ?>) _build).getEnvironment(listener);
          for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            envVars.put((String) entry.getKey(), (String) entry.getValue());
          }
          String jenkinsfile = envVars.expand(script);
          listener.getLogger().println("Obtained " + scriptPath + " from " + scm.getKey());
          Queue.Executable exec = owner.getExecutable();
          FlowDurabilityHint hint = (exec instanceof Run) ? DurabilityHintProvider.suggestedFor(((Run)exec).getParent()) : GlobalDefaultFlowDurabilityLevel.getDefaultDurabilityHint();
          return new CpsFlowExecution(jenkinsfile, true, owner, hint);
        } catch (FileNotFoundException e) {
          throw new AbortException("Unable to find " + scriptPath + " from " + scm.getKey());
        }
      } else {
        listener.getLogger().println("Lightweight checkout support not available, falling back to full checkout.");
      }
    }

    return null;
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
