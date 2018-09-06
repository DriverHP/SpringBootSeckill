package com.test.seckill.common.controller;

import com.test.seckill.common.dynamicquery.DynamicQuery;
import com.test.seckill.common.entity.SuccessKilled;
import com.test.seckill.common.service.SeckillService;
import com.test.seckill.queue.SeckillQueue;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seckill")
public class SeckillController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillController.class);

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10l, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000));

    @Autowired
    private DynamicQuery dynamicQuery;
    @Autowired
    private SeckillService seckillService;

    @PostMapping("/start")
    public String start(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        for(long i = 0; i < 1000; i++){
            final long  userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    String res = seckillService.startSeckill(seckillId, userId);
                    LOGGER.info("当前线程: {}, 秒杀结果：{}", Thread.currentThread(), res);
                    if(res.equals("failed")){
                        LOGGER.info("用户：{} 人太多");
                    }else{
                        LOGGER.info("用户：{} 买到了", userId);
                    }
                }
            };
            threadPoolExecutor.execute(task);
        }
        try {
            Thread.sleep(10000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }


    @PostMapping("/startAopLock")
    public String startAoplock(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        for(long i = 0; i < 1000; i++){
            final long  userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    String res = seckillService.startSeckillAopLock(seckillId, userId);
                    if(res.equals("failed")){
                        LOGGER.info("用户：{} 人太多");
                    }else{
                        LOGGER.info("用户：{} 买到了", userId);
                    }
                }
            };
            threadPoolExecutor.execute(task);
        }
        try {
            Thread.sleep(10000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @PostMapping("/startDBPCC_ONE")
    public String startSeckilDBPCC_ONE(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        for(long i = 0; i < 1000; i++){
            final long  userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    String res = seckillService.startSeckilDBPCC_ONE(seckillId, userId);
                    if(res.equals("failed")){
                        LOGGER.info("用户：{} 人太多");
                    }else{
                        LOGGER.info("用户：{} 买到了", userId);
                    }
                }
            };
            threadPoolExecutor.execute(task);
        }
        try {
            Thread.sleep(10000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }
    @PostMapping("/startDPCC_TWO")
    public String startDPCC_TWO(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId =  seckillId;
        LOGGER.info("开始秒杀五(正常、数据库锁最优实现)");
        for(int i=0;i<1000;i++){
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    String res = seckillService.startSeckilDBPCC_TWO(killId, userId);
                    if(res.equals("failed")){
                        LOGGER.info("用户：{} 人太多");
                    }else{
                        LOGGER.info("用户：{} 买到了", userId);
                    }
                }
            };
            threadPoolExecutor.execute(task);
        }
        try {
            Thread.sleep(10000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @ApiOperation(value="秒杀六(数据库乐观锁)")
    @PostMapping("/startDBOCC")
    public String startDBOCC(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId =  seckillId;
        for(int i=0;i<1000;i++){
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    //这里使用的乐观锁、可以自定义抢购数量、如果配置的抢购人数比较少、比如120:100(人数:商品) 会出现少买的情况
                    //用户同时进入会出现更新失败的情况
                    String res = seckillService.startSeckilDBOCC(killId, userId, 1);
                    if(res.equals("failed")){
                        LOGGER.info("用户：{} 人太多");
                    }else{
                        LOGGER.info("用户：{} 买到了", userId);
                    }
                }
            };
            threadPoolExecutor.execute(task);
        }
        try {
            Thread.sleep(10000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @ApiOperation(value="秒杀六(BlockingQueue)")
    @PostMapping("/startQueue")
    public String startQueue(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId =  seckillId;
        for(int i=0;i<1000;i++){
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    SuccessKilled successKilled = new SuccessKilled();
                    successKilled.setSeckillId(seckillId);
                    successKilled.setUserId(userId);
                    boolean flag = SeckillQueue.getMailQueue().produce(successKilled);
                    if(flag){
                        LOGGER.info("用户：{} 买到了", userId);
                    }else{
                        LOGGER.info("用户：{} 人太多");
                    }
                }
            };
            threadPoolExecutor.execute(task);
        }
        try {
            Thread.sleep(10000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
