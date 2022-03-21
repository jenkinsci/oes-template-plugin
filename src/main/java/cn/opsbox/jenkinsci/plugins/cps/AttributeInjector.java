package cn.opsbox.jenkinsci.plugins.cps;

import com.google.common.collect.Maps;
import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.model.Queue;
import lombok.SneakyThrows;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.cps.GroovyShellDecorator;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
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
            LOGGER.log(Level.WARNING, (String)null, x);
            return;
        }
        if (!(executable instanceof WorkflowRun)) {
            LOGGER.log(Level.FINE, "unexpected executable: {0}", executable);
            return;
        }

        WorkflowJob job = (WorkflowJob) executable.getParent();
        FlowDefinition definition = job.getDefinition();
        Map<String, Object> attributes = Maps.newHashMap();

        if (definition instanceof CpsTemplateFlowDefinition) {

            CpsTemplateFlowDefinition cpsTemplateFlowDefinition = (CpsTemplateFlowDefinition) definition;
            String parameters = cpsTemplateFlowDefinition.getParameters();

            Properties paramProps = new Properties();
            try (StringReader stringReader = new StringReader(parameters)) {
                paramProps.load(stringReader);
            } catch (IOException ignored) {
            }
            attributes = Maps.newHashMap(Maps.fromProperties(paramProps));
        }

        // TODO Multibranch Job

        for (Map.Entry<String, Object> entry : attributes.entrySet())
            shell.setVariable(entry.getKey(), entry.getValue());
    }
}
