package org.taocaicai.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @project taocaicai-activiti
 * @author Oakley
 * @created 2021-08-07 02:03:2:03
 * @package org.taocaicai.entity
 * @description 出差申请POJO对象
 * @version: 0.0.0.1
 */
@Data
public class Evection implements Serializable {
  private long id;
  private String name;
  /** 出差天数 */
  private int num;
  /** 开始时间 */
  private Date startDate;
  /** 结束时间 */
  private Date endDate;
  /** 目地地 */
  private String destination;
  /** 原因 */
  private String reson;
  /** 金额 */
  private int money;
}
