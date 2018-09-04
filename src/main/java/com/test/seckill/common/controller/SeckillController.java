package com.test.seckill.common.controller;

import com.test.seckill.common.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
