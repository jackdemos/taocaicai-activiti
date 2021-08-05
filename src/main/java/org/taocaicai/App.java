package org.taocaicai;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;

/** Hello world! */
public class App {
  public static void main(String[] args) {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    Deployment deploy =
        repositoryService
            .createDeployment()
            .addClasspathResource("bpmn/evection.bpmn")
            .addClasspathResource("bpmn/evection.png")
            .name("出差申请")
            .deploy();
    System.out.println(deploy.getId() + "\t" + deploy.getName());
  }
}
