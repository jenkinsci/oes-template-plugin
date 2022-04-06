package cn.opsbox.jenkinsci.plugins.cps;

import hudson.AbortException;
import hudson.model.*;
import hudson.scm.SCM;
import jenkins.scm.api.SCMFileSystem;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.cps.CpsFlowFactoryAction2;
import org.jenkinsci.plugins.workflow.cps.persistence.PersistIn;
import org.jenkinsci.plugins.workflow.flow.*;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.jenkinsci.plugins.workflow.cps.persistence.PersistenceContext.JOB;

@PersistIn(JOB)
public abstract class CpsTemplateFlowDefinition extends FlowDefinition {

    @Getter
    protected OesTemplateFlowDefinitionConfiguration configProvider;

    @SneakyThrows
    static FlowDurabilityHint determineFlowDurabilityHint(FlowExecutionOwner owner){
        Queue.Executable exec = owner.getExecutable();
        if (!(exec instanceof WorkflowRun)) {
            throw new IllegalStateException("inappropriate context");
        }
        FlowDurabilityHint hint = (exec instanceof Item) ? DurabilityHintProvider.suggestedFor((Item)exec) : GlobalDefaultFlowDurabilityLevel.getDefaultDurabilityHint();
        return hint;
    }

    @Override
    public CpsFlowExecution create(
            FlowExecutionOwner owner, TaskListener listener, List<? extends Action> actions)
            throws Exception {

        for (Action a : actions) {
            if (a instanceof CpsFlowFactoryAction2) {
                return ((CpsFlowFactoryAction2) a).create(this, owner, actions);
            }
        }

        String template = initializePipeline(owner);
        FlowDurabilityHint hint = determineFlowDurabilityHint(owner);
        return new CpsFlowExecution(template, true, owner, hint);
    }

    @SneakyThrows
    String initializePipeline(FlowExecutionOwner owner) {

        String script = "";

        if (configProvider instanceof ConsoleOesTemplateFlowDefinitionConfiguration) {
            ConsoleOesTemplateFlowDefinitionConfiguration console = (ConsoleOesTemplateFlowDefinitionConfiguration) configProvider;
            script = console.getScript();
        } else {
            ScmOesTemplateFlowDefinitionConfiguration scm = (ScmOesTemplateFlowDefinitionConfiguration) configProvider;

            Queue.Executable _build = owner.getExecutable();
            if (!(_build instanceof Run)) {
                throw new IOException("can only check out SCM into a Run");
            }
            Run<?,?> build = (Run<?,?>) _build;

            SCM scm1 = scm.getScm();
            String scriptPath = scm.getScriptPath();
            TaskListener listener = owner.getListener();

            try(SCMFileSystem fs = SCMFileSystem.of(build.getParent(), scm1)) {
                if (fs != null) {
                    try {
                        script = fs.child(scriptPath).contentAsString();
                        listener.getLogger().println("Obtained " + scriptPath + " from " + scm1.getKey());

                    } catch (FileNotFoundException e) {
                        throw new AbortException("Unable to find " + scriptPath + " from " + scm1.getKey());
                    }
                } else {
                    throw new AbortException("Could not get template file " + scriptPath + " from " + scm1.getKey());
                }
            }
        }

        return script;
    }

}
