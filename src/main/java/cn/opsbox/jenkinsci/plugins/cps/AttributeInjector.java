package cn.opsbox.jenkinsci.plugins.cps;

import com.google.common.collect.Maps;
import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TaskListener;
import lombok.SneakyThrows;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.cps.GroovyShellDecorator;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class AttributeInjector extends GroovyShellDecorator {
    private static final Logger LOGGER = Logger.getLogger(AttributeInjector.class.getName());

    @SneakyThrows
    @Override
    public void configureShell(@CheckForNull CpsFlowExecution context, GroovyShell shell) {
        Queue.Executable executable;
        if (context == null)
            return;
        try {
            executable = context.getOwner().getExecutable();
        } catch (IOException x) {
            LOGGER.log(Level.WARNING, null, x);
            return;
        }
        if (!(executable instanceof WorkflowRun)) {
            LOGGER.log(Level.FINE, "unexpected executable: {0}", executable);
            return;
        }

        WorkflowJob job = (WorkflowJob) executable.getParent();
        FlowDefinition definition = job.getDefinition();
        Map<String, Object> attributes = Maps.newHashMap();

        if (definition instanceof OesTemplateFlowDefinition) {

            OesTemplateFlowDefinition oesTemplateFlowDefinition = (OesTemplateFlowDefinition) definition;
            String parameters = oesTemplateFlowDefinition.getParameters();

            Queue.Executable _build = context.getOwner().getExecutable();
            if (!(_build instanceof Run)) {
                throw new IOException("can only check out SCM into a Run");
            }
            Run<?,?> build = (Run<?,?>) _build;

            TaskListener taskListener = context.getOwner().getListener();
            String expandParameters = build.getEnvironment(taskListener).expand(parameters);
            if (!expandParameters.isEmpty()) {
                attributes = new Yaml().load(expandParameters);
            }
        }

        // TODO Multibranch Job

        for (Map.Entry<String, Object> entry : attributes.entrySet())
            shell.setVariable(entry.getKey(), entry.getValue());
    }
}
