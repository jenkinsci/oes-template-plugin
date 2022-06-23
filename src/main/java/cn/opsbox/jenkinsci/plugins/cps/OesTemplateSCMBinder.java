package cn.opsbox.jenkinsci.plugins.cps;

import hudson.model.Action;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionOwner;

import java.util.List;

public class OesTemplateSCMBinder extends FlowDefinition {

  private SCM scriptSCM;

  private String scriptPath = OesTemplateWorkflowBranchProjectFactory.SCRIPT;

  public OesTemplateSCMBinder(String scriptPath, SCM scriptSCM) {
    this.scriptPath = scriptPath;
    this.scriptSCM = scriptSCM;
  }

  @Override
  public FlowExecution create(FlowExecutionOwner handle, TaskListener listener, List<? extends Action> actions) throws Exception {
    return new CpsScmFlowDefinition(this.scriptSCM, this.scriptPath).create(handle, listener, actions);
  }
}
