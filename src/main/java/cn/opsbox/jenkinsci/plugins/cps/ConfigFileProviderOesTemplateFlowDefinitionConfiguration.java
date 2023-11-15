package cn.opsbox.jenkinsci.plugins.cps;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import hudson.Extension;
import hudson.util.ListBoxModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.plugins.configfiles.ConfigFileStore;
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collection;

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

        @SneakyThrows
        public ListBoxModel doFillScriptIdItems() {

            ListBoxModel items = new ListBoxModel();
            try {
                ConfigFileStore store = GlobalConfigFiles.get();
                Collection<Config> configs = store.getConfigs();
                for (Config config : configs) {
                  items.add(config.name, config.id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new StandardListBoxModel().includeEmptyValue();
            }

            return items;
        }
    }
}