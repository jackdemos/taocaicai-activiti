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
import java.util.List;

/**
 * @project taocaicai-activiti
 * @author Oakley
 * @created 2021-08-07 02:14:2:14
 * @package org.taocaicai
 * @description 多用户(组用户)
 * @version: 0.0.0.1
 */
public class UsersTest {

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
            .addClasspathResource("bpmn/evection-users.bpmn") // 添加bpmn资源
            .addClasspathResource("bpmn/evection-users.png") // 添加png资源
            .name("出差申请单-组任务")
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
    String key = "evection-exclusive";
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
    /** 输出流程启动信息 */
    System.out.println("流程定义Id:\t" + processInstance.getProcessDefinitionId());
    System.out.println("流程实例Id:\t" + processInstance.getId());
    System.out.println("流程实例名称:" + processInstance.getProcessDefinitionName());
  }

  /** 多用户完成三部步骤 1.查询用户 2.拾取用户 3.完成任务 */
  @Test
  public void queryUsers() {
    String key = "evection-users";
    /** 候选人 */
    String candidateUser = "yanglan";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    /** 查询候选人 */
    List<Task> tasks =
        taskService
            .createTaskQuery()
            .processDefinitionKey(key)
            .taskCandidateUser(candidateUser)
            //                .taskCandidateOrAssigned(candidateUser)
            /** 查询个人代办 */
            //            .taskAssignee(candidateUser)
            .list();

    for (Task task : tasks) {
      System.out.println("流程实例ID: " + task.getProcessInstanceId());
      System.out.println("任务ID: " + task.getId());
      System.out.println("负责人: " + task.getAssignee());
      System.out.println("任务名称: " + task.getName());
    }
  }

  /** 拾取用户 */
  @Test
  public void claimUser() {
    String taskId = "30002";
    String userId = "yanglan";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    /** 获取任务 */
    Task task =
        taskService.createTaskQuery().taskId(taskId).taskCandidateUser(userId).singleResult();
    /** 如果 task 不为空刚可以拾取用户 */
    if (task == null) {
      System.out.println("不能拾取用户");
      return;
    }
    taskService.claim(taskId, userId);
    System.out.println("拾取用户成功");
  }

  /** 归还任务 */
  @Test
  public void unclaimUser() {
    String taskId = "30002";
    String userId = "weidong";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    /** 获取任务 */
    Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(userId).singleResult();
    /** 如果 task 不为空刚可以拾取用户 */
    if (task == null) {
      System.out.println("不能归还任务");
      return;
    }
    /** 以下两行功能相同 */
//     taskService.unclaim(taskId);
    taskService.setAssignee(taskId, null);
    System.out.println("归还任务成功");
  }

  /**
   * 任务交接(将任务交给指定人)
   */
  @Test
  public void appointAssignee(){
    String taskId = "30002";
    String userId = "yanglan";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    /** 获取任务 */
    Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(userId).singleResult();
    /** 如果 task 不为空刚可以拾取用户 */
    if (task == null) {
      System.out.println("任务交接失败");
      return;
    }
    /** 设置新的负责人 */
    taskService.setAssignee(taskId, "weidong");
    System.out.println("任务交接成功");
  }


  /** 完成任务 */
  @Test
  public void complete() {
    String taskId = "27505";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    taskService.complete(taskId);
    System.out.println("任务完成");
  }
}
