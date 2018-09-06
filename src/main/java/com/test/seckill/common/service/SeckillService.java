package com.test.seckill.common.service;

public interface SeckillService {
    /**
     * 删除秒杀记录
     * @param seckillId
     */
    void deleteSeckill(long seckillId);

    /**
     * 开始秒杀
     * @param seckillId
     * @param userId
     * @return
     */
    String startSeckill(long seckillId, long userId);

    /**
     * 生秒杀
     * @param seckilled
     * @return
     */
    Long getSeckillCount(long seckilled);

    /**
     * 通过aop加锁
     * @param seckillId
     * @param userId
     * @return
     */
    String startSeckillAopLock(long seckillId, long userId);

    /**
     * 秒杀 二、数据库悲观锁
     * @param seckillId
     * @param userId
     * @return
     */
    String startSeckilDBPCC_ONE(long seckillId,long userId);
    /**
     * 秒杀 三、数据库悲观锁
     * @param seckillId
     * @param userId
     * @return
     */
    String startSeckilDBPCC_TWO(long seckillId,long userId);
    /**
     * 秒杀 三、数据库乐观锁
     * @param seckillId
     * @param userId
     * @return
     */
    String startSeckilDBOCC(long seckillId,long userId,long number);
}
