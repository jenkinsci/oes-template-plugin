package cn.opsbox.jenkinsci.plugins.cps;

import hudson.Extension;
import lombok.Getter;
import org.kohsuke.stapler.DataBoundConstructor;

public class ConsoleOesTemplateFlowDefinitionConfiguration extends OesTemplateFlowDefinitionConfiguration{

    @Getter
    private final String script;

    @DataBoundConstructor
    public ConsoleOesTemplateFlowDefinitionConfiguration(String script) {
        this.script = script;
    }

    @Extension
    public static class DescriptorImpl extends OesTemplateFlowDefinitionConfiguration.DescriptorImpl {
        public String getDisplayName() {
            return "From Console";
        }
    }
}
