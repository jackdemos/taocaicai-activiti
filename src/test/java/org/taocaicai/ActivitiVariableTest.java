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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.val;

/**
 * @project taocaicai-activiti
 * @author Oakley
 * @created 2021-08-07 02:14:2:14
 * @package org.taocaicai
 * @description TODO
 * @version: 0.0.0.1
 */
public class ActivitiVariableTest {

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
            .addClasspathResource("bpmn/evection-uel.bpmn") // 添加bpmn资源
            .addClasspathResource("bpmn/evection-uel.png") // 添加png资源
            .name("出差申请流程变量")
            .deploy();
    System.out.println(deploy.getId() + "\t" + deploy.getName());
  }

  /** 启动流程，设置流程变量 */
  @Test
  public void startProcessDeploy() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    /** 设置流程变量 */
    String key = "evection-uel";
    HashMap<String, Object> map = new HashMap<>();
    Evection evection = new Evection();
    evection.setNum(2);
    evection.setReson("参加产销会");
    map.put("evection", evection);
    map.put("assignee0", "zhangsan");
    map.put("assignee1", "lishi");
    map.put("assignee2", "wangdong");
    map.put("assignee3", "liulei");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, map);
    /** 输出流程启动信息 */
    System.out.println("流程定义Id:\t" + processInstance.getProcessDefinitionId());
    System.out.println("流程实例Id:\t" + processInstance.getId());
    System.out.println("流程实例名称:" + processInstance.getProcessDefinitionName());
    System.out.println("当前活动的Id\t" + processInstance.getActivityId());
  }

  /** 完成 */
  @Test
  public void complete02() {
    String key = "evection-uel";
    String assignee = "lishi";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    /** 查询任务 */
    List<Task> task =
        taskService.createTaskQuery().processDefinitionKey(key).taskAssignee(assignee).list();
    if (task.get(0) == null) {
      System.out.println("任务执行失败");
      return;
    }
    HashMap<String, Object> map = new HashMap<>();
    Evection evection = new Evection();
    evection.setNum(2);
    evection.setReson("参加产销会");
    map.put("evection", evection);
    taskService.complete(task.get(0).getId(), map);
    System.out.printf("任务执行完成,流程实例Id:%s  审批人: %s\n", task.get(0).getProcessInstanceId(), task.get(0).getAssignee());
  }

  /** 完成指定流程实例 */
  @Test
  public void complete() {
    String processInstanceId = "7501";
    String assignee = "liulei";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    Task task =
        taskService
            .createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskAssignee(assignee)
            .singleResult();
    if (task == null) {
      System.out.println("未找到流程实例Id,任务执行失败");
      return;
    }
    taskService.complete(task.getId());
    System.out.printf("任务执行完成,流程实例Id: %s 审批人 %s\n", task.getProcessInstanceId(), task.getAssignee());
  }
}
