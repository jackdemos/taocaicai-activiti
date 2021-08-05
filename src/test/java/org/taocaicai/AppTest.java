package org.taocaicai;

import static org.junit.Assert.assertTrue;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/** Unit test for simple App. */
public class AppTest {

  /** 部署任务 */
  @Test
  public void deploy() {
    /** 创建 ProcessEngine对象 */
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /** 获取 RepositoryService对象进行实例部署 */
    RepositoryService repositoryService = processEngine.getRepositoryService();
    /** 实例部署 */
    Deployment deploy =
        repositoryService
            .createDeployment()
            .addClasspathResource("bpmn/evection.bpmn")
            .addClasspathResource("bpmn/evection.png")
            .name("出差申请")
            .deploy();
    System.out.println(deploy.getId() + "\t" + deploy.getName());
  }

  /** 通过Zip文件 部署任务 */
  @Test
  public void deployByZip() {
    InputStream resourceAsStream =
        this.getClass().getClassLoader().getResourceAsStream("evection.zip");
    ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
    /** 创建 ProcessEngine对象 */
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /** 获取 RepositoryService对象进行实例部署 */
    RepositoryService repositoryService = processEngine.getRepositoryService();
    /** 实例部署 */
    Deployment deploy =
        repositoryService
            .createDeployment()
            .addZipInputStream(zipInputStream)
            .name("请假申请")
            .deploy();
    System.out.println(deploy.getId() + "\t" + deploy.getName());
  }

  /** 启动一个流程实例 */
  @Test
  public void startProcess() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /** 获取 runtimeService 对象 */
    RuntimeService runtimeService = processEngine.getRuntimeService();
    /** 定义流程Id */
    String id = "evection";
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(id);
    System.out.println("流程定义Id:\t" + processInstance.getProcessDefinitionId());
    System.out.println("流程实例Id:\t" + processInstance.getId());
    System.out.println("流程实例名称:" + processInstance.getProcessDefinitionName());
    System.out.println("当前活动的Id\t" + processInstance.getActivityId());
  }

  /** 任务查询 */
  @Test
  public void taskList() {
    /*处理人*/
    // String assignee="zhangsan";
    String assignee = "lisi";
    String key = "evection";
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /** 获取一个 taskService对象 */
    TaskService taskService = processEngine.getTaskService();
    /** 根据流程的Key及任务负责人，查询任务 */
    List<Task> taskList =
        taskService.createTaskQuery().processDefinitionKey(key).taskAssignee(assignee).list();
    taskList.forEach(
        task -> {
          System.out.printf(
              "流程定义Id: %s \t 任务ID: %s\t 任务名称: %s \t负责人: %s",
              task.getProcessInstanceId(), task.getId(), task.getName(), task.getAssignee());
        });
  }

  /** 完成任务 */
  @Test
  public void doneTask() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /** 获取一个 taskService对象 */
    TaskService taskService = processEngine.getTaskService();
    /*只查询了一条记录(目录已知仅有一条记录)*/
    Task task =
        taskService
            .createTaskQuery()
            .processDefinitionKey("evection")
            .taskAssignee("zhangsan")
            .singleResult();
    /*完成任务*/
    taskService.complete(task.getId());
  }

  /** 查询流程的定义 */
  @Test
  public void queryDefinitionProcess() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    /** 获取流程定义 processDefinitionQuery对象 ，用来进行查询操作 */
    ProcessDefinitionQuery processDefinitionQuery =
        repositoryService.createProcessDefinitionQuery();
    List<ProcessDefinition> processDefinitionList =
        processDefinitionQuery
            .processDefinitionKey("evection")
            .orderByProcessDefinitionVersion()
            .desc()
            .list();
    processDefinitionList.forEach(
        processDefinition -> {
          System.out.printf(
              "流程定义的ID: %s\t流程定义的名称: %s\t流程定义的Key: %s\t流程定义部署的Id: %s \t流程定义的Version: %s\n",
              processDefinition.getId(),
              processDefinition.getName(),
              processDefinition.getKey(),
              processDefinition.getDeploymentId(),
              processDefinition.getVersion());
        });
  }

  /** 删除流程 */
  @Test
  public void deleteDeploy() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    /** 如果有实例已经启动，则应用抛出错误信息 */
    repositoryService.deleteDeployment("15001");
    /** 设置为 TRUE 级联删除流程定义,即使实例已经启动，也可以被删除，设置为FALSE,为非级联操作 */
    // repositoryService.deleteDeployment("1",true);
  }

  /** activiti 文件下载，需要使用commons-io */
  @Test
  public void downloadProcessDefinition() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    List<ProcessDefinition> processDefinitionList =
        repositoryService.createProcessDefinitionQuery().list();
    processDefinitionList.forEach(
        processDefinition -> {
          /*获取流程部署Id*/
          String deploymentId = processDefinition.getDeploymentId();
          /** 获取png图片 */
          InputStream pngInputStream =
              repositoryService.getResourceAsStream(
                  deploymentId, processDefinition.getDiagramResourceName());
          /*文件流*/
          InputStream bpmnInputStream =
              repositoryService.getResourceAsStream(
                  deploymentId, processDefinition.getResourceName());
          /** 文件的保存 */
          File pngFile = new File("Z:/evection.png");
          File bpmn = new File("Z:/evection.bpmn");
          try {
            FileOutputStream pngOutputStream = new FileOutputStream(pngFile);
            FileOutputStream bpmnOutputStream = new FileOutputStream(bpmn);
            IOUtils.copy(pngInputStream, pngOutputStream);
            IOUtils.copy(bpmnInputStream, bpmnOutputStream);
            pngInputStream.close();
            pngInputStream.close();
            bpmnInputStream.close();
            bpmnOutputStream.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  /** 流程历史信息查看 */
  @Test
  public void historyList() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    HistoryService historyService = processEngine.getHistoryService();
    /** 获取actInst表的查询对象 */
    HistoricActivityInstanceQuery instanceQuery =
        historyService.createHistoricActivityInstanceQuery();
    List<HistoricActivityInstance> list =
        instanceQuery.processDefinitionId("evection:1:4").orderByActivityId().desc().list();
    list.forEach(
        historicActivityInstance -> {
          System.out.println(historicActivityInstance.getId());
          System.out.println(historicActivityInstance.getActivityId());
          System.out.println(historicActivityInstance.getAssignee());
        });
  }

  /** 挂起流程 */
  @Test
  public void suspended() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    /** 获取流程定义的对象 */
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("evection").singleResult();

    /** 获取当前流程定义的状态 */
    boolean suspended = processDefinition.isSuspended();
    String id = processDefinition.getId();
    System.out.println(id);
    /** 如果定义流程状态为激话则挂起，如果挂起则激活 */
    if (suspended) {
      /** 表示当前流程定义是挂起的 */
      repositoryService.activateProcessDefinitionById(id, true, null);
      System.out.println("流程定义Id:" + id + "被挂起");
    } else {
      /** 激话状态需要，设置为挂起 */
      repositoryService.suspendProcessDefinitionByKey(id, true, null);
      System.out.println("流程定义Id:" + id + "被激活");
    }
  }
}
