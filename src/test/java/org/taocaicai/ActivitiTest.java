package org.taocaicai;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.util.HashMap;

/**
 * @project taocaicai-activiti
 * @author Oakley
 * @created 2021-08-06 06:52:6:52
 * @package org.taocaicai
 * @description TODO
 * @version: 0.0.0.1
 */
public class ActivitiTest {

  @Test
  public void deployment() {
    /** 创建 ProcessEngine对象 */
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /** 获取 RepositoryService对象进行实例部署 */
    RepositoryService repositoryService = processEngine.getRepositoryService();
    /** 实例部署 */
    Deployment deploy =
        repositoryService
            .createDeployment()
            .addClasspathResource("bpmn/evection.bpmn") // 添加bpmn资源
            .addClasspathResource("bpmn/evection.png") // 添加png资源
            .name("出差申请")
            .deploy();
    System.out.println(deploy.getId() + "\t" + deploy.getName());
  }

  /** 创建一个流程实例 */
  @Test
  public void test01() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    /** 设置 assignee的取值 */
    HashMap<String, Object> assignee = new HashMap<String, Object>();
    assignee.put("assignee0", "张三");
    assignee.put("assignee1", "李四");
    assignee.put("assignee2", "王五");
    assignee.put("assignee3", "周六");
    /** 创建流程实例 */
    ProcessInstance processInstance =
        runtimeService.startProcessInstanceByKey("process-evection", assignee);
    System.out.println("流程实例ID: "+processInstance.getId());
    System.out.println("流程定义ID: "+processInstance.getProcessDefinitionId());
    System.out.println("流程定义名称: "+processInstance.getProcessDefinitionName());
  }
}
