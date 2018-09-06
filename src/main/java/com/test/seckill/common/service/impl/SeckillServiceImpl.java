package com.test.seckill.common.service.impl;

import com.test.seckill.common.aop.ServiceLimit;
import com.test.seckill.common.aop.ServiceLock;
import com.test.seckill.common.dynamicquery.DynamicQuery;
import com.test.seckill.common.entity.Seckill;
import com.test.seckill.common.entity.SuccessKilled;
import com.test.seckill.common.repository.SeckillRepository;
import com.test.seckill.common.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private DynamicQuery dynamicQuery;

    @Autowired
    private SeckillRepository seckillRepository;

    @Override
    public Long getSeckillCount(long seckillId) {
        String nativeSql = "SELECT count(*) FROM success_killed WHERE seckill_id=?";
        Object object =  dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        return ((Number) object).longValue();
    }

    @Override
    @Transactional
    public void deleteSeckill(long seckillId) {
        String sql = "DELETE FROM success_killed where seckill_id = ?";
        dynamicQuery.nativeExecuteUpdate(sql, new Object[]{seckillId});
        sql = "UPDATE seckill SET number = 100 where seckill_id = ?";
        dynamicQuery.nativeExecuteUpdate(sql, new Object[]{seckillId});
    }

    @Override
    @ServiceLimit
    @Transactional
    public String startSeckill(long seckillId, long userId) {
        String sql = "SELECT number FROM seckill WHERE seckill_id = ?";
        Object object = dynamicQuery.nativeQueryObject(sql, new Object[]{seckillId});
        Long number = ((Number) object).longValue();

        if(number > 0){
            sql = "UPDATE seckill SET number = number - 1 where seckill_id = ?";
            dynamicQuery.nativeExecuteUpdate(sql, new Object[]{seckillId});
            SuccessKilled successKilled = new SuccessKilled();
            successKilled.setSeckillId(seckillId);
            successKilled.setUserId(userId);
            successKilled.setState((short)0);
            successKilled.setCreateTime(new Timestamp(new Date().getTime()));
            dynamicQuery.save(successKilled);
            return "success";
        }else{
            return "failed";
        }
    }

    @Override
    @ServiceLock
    @Transactional
    public String startSeckillAopLock(long seckillId, long userId) {
        String sql = "SELECT number FROM seckill WHERE seckill_id = ?";
        Object object = dynamicQuery.nativeQueryObject(sql, new Object[]{seckillId});
        Long number = ((Number) object).longValue();
        if(number > 0){
            sql = "UPDATE seckill SET number = number - 1 where seckill_id = ?";
            dynamicQuery.nativeExecuteUpdate(sql, new Object[]{seckillId});
            SuccessKilled successKilled = new SuccessKilled();
            successKilled.setSeckillId(seckillId);
            successKilled.setUserId(userId);
            successKilled.setState((short) 0);
            successKilled.setCreateTime(new Timestamp(new Date().getTime()));
            dynamicQuery.save(successKilled);
            return "success";
        }else{
            return "failed";
        }
    }

    /*
    1)如果不开启事务，第二步即使加锁，第一个会话读库存结束后，变会释放锁，第二个会话仍有机会在去库存前读库存，出现超卖。
    2)如果开启事务，第二步不加锁，第一个会话读库存结束后，第二个会话容易出现【脏读】，出现超卖。
    3)即加事务，又加读锁：开启事务，第一个会话读库存时加读锁，并发时，第二个会话也允许获得读库存的读锁，但是在第一个会话执行写操作时，写锁便会等待第二个会话的读锁，第二个会话执行写操作时，写锁便会等待第一个会话的读锁，出现死锁
    4)即加事务，又加写锁：第一个会话读库存时加写锁，写锁会阻止其它事务的读锁和写锁。直到commit才会释放，允许第二个会话查询库存，不会出现超卖现象。
    */

    @Override
    @Transactional
    public String startSeckilDBPCC_ONE(long seckillId, long userId) {
        String sql = "SELECT number FROM seckill WHERE seckill_id = ? FOR UPDATE";
        Object object = dynamicQuery.nativeQueryObject(sql, new Object[]{seckillId});
        Long number = ((Number) object).longValue();
        if(number > 0){
            sql = "UPDATE seckill SET number = number -1 where seckill_id = ?";
            dynamicQuery.nativeExecuteUpdate(sql, new Object[]{seckillId});
            SuccessKilled successKilled = new SuccessKilled();
            successKilled.setSeckillId(seckillId);
            successKilled.setUserId(userId);
            successKilled.setState((short) 0);
            successKilled.setCreateTime(new Timestamp(new Date().getTime()));
            dynamicQuery.save(successKilled);
            return "success";
        }else{
            return "failed";
        }
    }

    @Override
    @Transactional
    public String startSeckilDBPCC_TWO(long seckillId, long userId) {
        String sql = "UPDATE seckill SET number = number -1 where seckill_id = ? AND number > 0";
        int count = dynamicQuery.nativeExecuteUpdate(sql, new Object[]{seckillId});
        if(count > 0){
            SuccessKilled successKilled = new SuccessKilled();
            successKilled.setSeckillId(seckillId);
            successKilled.setUserId(userId);
            successKilled.setState((short) 0);
            successKilled.setCreateTime(new Timestamp(new Date().getTime()));
            dynamicQuery.save(successKilled);
            return "success";
        }else{
            return "failed";
        }
    }

    @Override
    @Transactional
    public String startSeckilDBOCC(long seckillId, long userId, long number) {
        Seckill seckill = seckillRepository.findById(seckillId).get();
        if(seckill.getNumber() >= number){
            String sql = "UPDATE seckill SET number=number-?, version=version+1 WHERE seckill_id=? AND version=?";
            int count = dynamicQuery.nativeExecuteUpdate(sql, new Object[]{number, seckillId, seckill.getVersion()});
            if(count > 0){
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserId(userId);
                successKilled.setState((short) 0);
                successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                dynamicQuery.save(successKilled);
                return "success";
            }else{
                return "failed";
            }
        }else{
            return "failed";
        }
    }
}
