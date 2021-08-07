package org.taocaicai;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.taocaicai.entity.Evection;

import java.util.HashMap;
import java.util.Map;

/**
 * @project taocaicai-activiti
 * @author Oakley
 * @created 2021-08-07 05:34:5:34
 * @package org.taocaicai
 * @description 并行网关
 * @version: 0.0.0.1
 */
public class ExclusiveParllelGetwayTest {

  /** 部署实例 */
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
            .addClasspathResource("bpmn/exection-parallelgateway.bpmn") // 添加bpmn资源
            .addClasspathResource("bpmn/exection-parallelgateway.png") // 添加png资源
            .name("出差申请单-并行网关")
            .deploy();
    System.out.println("流程主义Id: " + deploy.getId());
    System.out.println("流程主义名称: " + deploy.getName());
  }

  /** 启动流程 */
  @Test
  public void startProcessDeploy() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    /** 设置流程变量 */
    String key = "online-parallel";
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
    /** 输出流程启动信息 */
    System.out.println("流程定义Id:\t" + processInstance.getProcessDefinitionId());
    System.out.println("流程实例Id:\t" + processInstance.getId());
    System.out.println("流程实例名称:" + processInstance.getProcessDefinitionName());
  }

  /** 完成任务 */
  @Test
  public void complete() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /** 获取一个 taskService对象 */
    TaskService taskService = processEngine.getTaskService();
    /*只查询了一条记录(目录已知仅有一条记录)*/
    Task task =
        taskService
            .createTaskQuery()
            .processDefinitionKey("online-parallel")
//            .taskAssignee("zhangsan")
//            .taskAssignee("jishu")
//            .taskAssignee("chanping")
//            .taskAssignee("xiangmu")
            .taskAssignee("cto")
            .singleResult();
    if (task == null) {
      System.out.println("未找到流程实例Id,任务执行失败");
      return;
    }
    Evection evection = new Evection();
    evection.setNum(4);

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("evection", evection);
    /** 完成任务 */
    taskService.complete(task.getId(), variables);
    System.out.println(task.getAssignee() + "已完成任务");
  }
}
