package cn.opsbox.jenkinsci.plugins.cps;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import lombok.Getter;
import org.jenkinsci.plugins.workflow.flow.FlowDefinitionDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;

public class OesTemplateFlowDefinition extends CpsTemplateFlowDefinition implements Serializable {

  @Getter
  private final String parameters;

  @DataBoundConstructor
  public OesTemplateFlowDefinition(String parameters) {
    this.parameters = parameters;
  }

  @DataBoundSetter
  public void setConfigProvider(OesTemplateFlowDefinitionConfiguration configProvider) {
    this.configProvider = configProvider;
  }

  @Extension
  public static class DescriptorImpl extends FlowDefinitionDescriptor {
    @Override
    @NonNull
    public String getDisplayName() {
      return "OES Template";
    }

  }
}
