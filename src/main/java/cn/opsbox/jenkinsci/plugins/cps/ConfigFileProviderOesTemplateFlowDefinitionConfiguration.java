package cn.opsbox.jenkinsci.plugins.cps;

import hudson.Extension;
import lombok.Getter;
import org.kohsuke.stapler.DataBoundConstructor;

public class ConfigFileProviderOesTemplateFlowDefinitionConfiguration extends OesTemplateFlowDefinitionConfiguration {
 
    @Getter
    private final String scriptId;

    @DataBoundConstructor
    public ConfigFileProviderOesTemplateFlowDefinitionConfiguration(String scriptId) {
        this.scriptId = scriptId;
    }

    @Extension
    public static class DescriptorImpl extends OesTemplateFlowDefinitionConfiguration.DescriptorImpl {
        public String getDisplayName() {
            return "From Config File";
        }
    }
}
