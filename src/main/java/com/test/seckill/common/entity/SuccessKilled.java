package com.test.seckill.common.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * 两个@Id即复合主键
 */
@Data
@Entity
@Table(name = "success_killed")
public class SuccessKilled implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "seckill_id", nullable = false)
    private long seckillId;

    @Id
    private long userId;
    private short state;
    private Timestamp createTime;
}
