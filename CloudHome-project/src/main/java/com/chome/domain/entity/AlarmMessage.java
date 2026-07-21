package com.chome.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chome.domain.Enum.AlarmLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 警报消息实体，对应数据库表 CHome_Alarm_Message
 * @Date 2025/7/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */
@TableName("CHome_Alarm_Message") // 绑定数据库表名
@AllArgsConstructor // lombok 全参构造器
@NoArgsConstructor  // lombok 无参构造器（MyBatis反射实例化必需）
@Builder            // lombok 构建者模式，快速组装对象
@Data               // lombok 自动生成get/set/toString/equals/hashCode
public class AlarmMessage implements Serializable {

    /**
     * 报警信息主键ID，自增主键
     */
    @TableId(value = "alarm_id", type = IdType.AUTO)
    private String alarmId;

    /**
     * 报警类型，区分不同故障告警（如过载、离线、故障等）
     */
    private String alarmType;

    /**
     * 报警详情文本，告警描述内容
     */
    private String alarmText;

    /**
     * 报警等级枚举：低/中/高/紧急等
     */
    private AlarmLevel alarmLevel;

    /**
     * 报警状态
     * 0:未确认 1:已确认
     */
    private Integer alarmStatus;

    /**
     * 告警来源设备ID，关联产生告警的硬件设备
     */
    private String alarmSourceId;

    /**
     * 告警确认操作人ID，操作用户主键
     */
    private Integer confirmUser;

    /**
     * 告警确认操作时间，用户点击确认告警的时间
     */
    private LocalDateTime confirmTime;

    /**
     * 告警产生时间，设备上报告警报文的时间
     */
    private LocalDateTime alarmTime;

    /**
     * 序列化版本号
     * 1. 当前实体实现Serializable序列化接口，用于Redis缓存、网络传输、对象持久化
     * 2. @Serial：Java16新增标准注解，标识此字段为序列化版本号，编译器校验语法合规
     * 3. @TableField(exist = false)：MyBatis-Plus标记，该字段不在数据库表内，持久化时忽略映射
     * 4. static final 静态常量，不属于对象实例属性，序列化时不会存入字节流
     * 5. 作用：反序列化时对比版本号，版本不一致直接抛出序列化异常，避免类结构变更导致数据错乱
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}