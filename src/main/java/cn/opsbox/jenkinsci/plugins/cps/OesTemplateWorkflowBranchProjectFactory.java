package cn.opsbox.jenkinsci.plugins.cps;

import cn.opsbox.jenkinsci.plugins.cps.scm.SCMFilter;
import cn.opsbox.jenkinsci.plugins.cps.scm.SCMSourceCriteriaForOesTemplate;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Collection;

public class OesTemplateWorkflowBranchProjectFactory extends WorkflowBranchProjectFactory {

  static final String SCRIPT = "Jenkinsfile";

  @Getter
  private String scriptPath = "Jenkinsfile";

  @Getter
  private String localMarker = "";

  @Getter
  private SCM scriptSCM;

  @DataBoundConstructor
  public OesTemplateWorkflowBranchProjectFactory(String localMarker) {
    this.localMarker = localMarker;
  }

  @DataBoundSetter
  public void setScriptPath(String scriptPath) {
    if (StringUtils.isEmpty(scriptPath)) {
      this.scriptPath = SCRIPT;
    } else {
      this.scriptPath = scriptPath;
    }
  }

  @DataBoundSetter
  public void setScriptSCM(SCM scriptSCM) {
    this.scriptSCM = scriptSCM;
  }

  @Override
  protected SCMSourceCriteria getSCMSourceCriteria(@NonNull SCMSource source) {
    return ((probe, listener) -> SCMSourceCriteriaForOesTemplate.matches(localMarker, probe, listener));
  }

  @Override
  protected FlowDefinition createDefinition() {
    return new OesTemplateSCMBinder(scriptPath, scriptSCM);
  }

  @Extension
  public static class DescriptorImpl extends AbstractWorkflowBranchProjectFactoryDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "by OES Template Provider";
    }

    public Collection<? extends SCMDescriptor<?>> getApplicableDescriptors() {
      return SCMFilter.filter();
    }
  }

}
