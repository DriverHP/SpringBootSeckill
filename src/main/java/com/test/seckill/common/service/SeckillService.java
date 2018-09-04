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
     *
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
}
