package cn.opsbox.jenkinsci.plugins.cps;

import cn.opsbox.jenkinsci.plugins.cps.scm.SCMSourceCriteriaForOesTemplate;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.branch.MultiBranchProjectFactory;
import jenkins.branch.MultiBranchProjectFactoryDescriptor;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import lombok.Getter;
import org.jenkinsci.plugins.workflow.multibranch.AbstractWorkflowMultiBranchProjectFactory;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.stapler.DataBoundConstructor;

public class OesTemplateWorkflowMultiBranchProjectFactory extends AbstractWorkflowMultiBranchProjectFactory {

  @Getter
  private String localMarker = "";
  @Getter
  protected OesTemplateFlowDefinitionConfiguration configProvider;

  @Getter
  private String parameters;

  private OesTemplateWorkflowMultiBranchProjectFactory() {

  }

  @DataBoundConstructor
  public OesTemplateWorkflowMultiBranchProjectFactory(String localMarker, OesTemplateFlowDefinitionConfiguration configProvider, String parameters) {
    this.localMarker = localMarker;
    this.configProvider = configProvider;
    this.parameters = parameters;
  }

  @Override
  protected SCMSourceCriteria getSCMSourceCriteria(@NonNull SCMSource source) {
    return ((probe, listener) -> SCMSourceCriteriaForOesTemplate.matches(localMarker, probe, listener));
  }

  @Override
  protected void customize(WorkflowMultiBranchProject project) {
    OesTemplateWorkflowBranchProjectFactory projectFactory = new OesTemplateWorkflowBranchProjectFactory(this.localMarker, this.configProvider, this.parameters);
    project.setProjectFactory(projectFactory);
  }

  @Extension
  public static class DescriptorImpl extends MultiBranchProjectFactoryDescriptor {
    public String getDisplayName() {
      return "OES Template";
    }
    @Override
    public MultiBranchProjectFactory newInstance() {
      return new OesTemplateWorkflowMultiBranchProjectFactory();
    }
  }
}
