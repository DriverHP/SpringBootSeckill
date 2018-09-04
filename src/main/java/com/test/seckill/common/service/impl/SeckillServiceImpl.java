package com.test.seckill.common.service.impl;

import com.test.seckill.common.aop.ServiceLock;
import com.test.seckill.common.dynamicquery.DynamicQuery;
import com.test.seckill.common.entity.SuccessKilled;
import com.test.seckill.common.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private DynamicQuery dynamicQuery;

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
}
