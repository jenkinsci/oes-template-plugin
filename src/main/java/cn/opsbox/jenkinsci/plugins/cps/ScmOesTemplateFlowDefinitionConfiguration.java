package cn.opsbox.jenkinsci.plugins.cps;

import hudson.Extension;
import hudson.scm.SCM;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;

public class ScmOesTemplateFlowDefinitionConfiguration extends OesTemplateFlowDefinitionConfiguration{

    @Getter
    @Setter
    private final SCM scm;
    @Getter
    @Setter
    private final String scriptPath;

    @DataBoundConstructor
    public ScmOesTemplateFlowDefinitionConfiguration(SCM scm, String scriptPath) {
        this.scm = scm;
        this.scriptPath = scriptPath;
    }

    @Extension
    public static class DescriptorImpl extends OesTemplateFlowDefinitionConfiguration.DescriptorImpl {
        public String getDisplayName(){
            return "From SCM";
        }
    }
}
