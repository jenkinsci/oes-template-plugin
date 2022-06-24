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
  private String localMarker = "";

  @Getter
  private final String parameters;

  @Getter
  protected OesTemplateFlowDefinitionConfiguration configProvider;

  @DataBoundConstructor
  public OesTemplateWorkflowBranchProjectFactory(String localMarker, OesTemplateFlowDefinitionConfiguration configProvider, String parameters) {
    this.localMarker = localMarker;
    this.configProvider = configProvider;
    this.parameters = parameters;
  }

  @Override
  protected SCMSourceCriteria getSCMSourceCriteria(@NonNull SCMSource source) {
    return ((probe, listener) -> SCMSourceCriteriaForOesTemplate.matches(localMarker, probe, listener));
  }

  @Override
  protected FlowDefinition createDefinition() {
    OesTemplateFlowDefinition definition =  new OesTemplateFlowDefinition(parameters);
    definition.setConfigProvider(configProvider);
    return definition;
  }

  @Extension
  public static class DescriptorImpl extends AbstractWorkflowBranchProjectFactoryDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "by OES Template";
    }

    public Collection<? extends SCMDescriptor<?>> getApplicableDescriptors() {
      return SCMFilter.filter();
    }
  }

}
