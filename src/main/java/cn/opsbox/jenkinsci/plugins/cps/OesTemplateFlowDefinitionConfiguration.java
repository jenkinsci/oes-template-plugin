package cn.opsbox.jenkinsci.plugins.cps;

import com.infradna.tool.bridge_method_injector.WithBridgeMethods;
import hudson.DescriptorExtensionList;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

import java.util.List;

public abstract class OesTemplateFlowDefinitionConfiguration extends AbstractDescribableImpl<OesTemplateFlowDefinitionConfiguration> {

    public abstract static class DescriptorImpl extends Descriptor<OesTemplateFlowDefinitionConfiguration> {

        @WithBridgeMethods(List.class)
        public static DescriptorExtensionList<OesTemplateFlowDefinitionConfiguration, DescriptorImpl> all(){

            Jenkins instance = Jenkins.getInstanceOrNull();

            if (instance != null) {
                return instance.getDescriptorList(OesTemplateFlowDefinitionConfiguration.class);
            } else {
                return null;
            }
        }

    }
}
