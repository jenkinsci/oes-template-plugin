package cn.opsbox.jenkinsci.plugins.cps;

import cn.opsbox.jenkinsci.plugins.cps.scm.SCMSourceCriteriaForOesTemplate;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.scm.SCM;
import jenkins.branch.MultiBranchProjectFactory;
import jenkins.branch.MultiBranchProjectFactoryDescriptor;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.multibranch.AbstractWorkflowMultiBranchProjectFactory;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class OesTemplateWorkflowMultiBranchProjectFactory extends AbstractWorkflowMultiBranchProjectFactory {

  private static final String SCRIPT = "Jenkinsfile";
  @Getter
  private String localMarker = "";
  @Getter
  private String scriptPath = SCRIPT;

  @Getter
  private SCM scriptSCM;

  private OesTemplateWorkflowMultiBranchProjectFactory() {

  }

  @DataBoundConstructor
  public OesTemplateWorkflowMultiBranchProjectFactory(String localMarker) {
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
  protected void customize(WorkflowMultiBranchProject project) {
    OesTemplateWorkflowBranchProjectFactory projectFactory = new OesTemplateWorkflowBranchProjectFactory(this.localMarker);
    projectFactory.setScriptPath(scriptPath);
    project.setProjectFactory(projectFactory);
  }

  @Extension
  public static class DescriptorImpl extends MultiBranchProjectFactoryDescriptor {
    public String getDisplayName() {
      return "OES Template Provider";
    }
    @Override
    public MultiBranchProjectFactory newInstance() {
      return new OesTemplateWorkflowMultiBranchProjectFactory();
    }
  }
}
