package com.test.seckill.queue;

import com.test.seckill.common.entity.SuccessKilled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SeckillQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillQueue.class);

    private static final int QUEUE_MAX_SIZE = 100;
    private static BlockingQueue<SuccessKilled> blockingQueue = new LinkedBlockingDeque<>(QUEUE_MAX_SIZE);

    private SeckillQueue(){

    }

    private static class SingletonHolder {
        private static final SeckillQueue instance = new SeckillQueue();
    }

    public static SeckillQueue getMailQueue(){
        return SingletonHolder.instance;
    }

    public boolean produce (SuccessKilled successKilled){
        boolean flag = blockingQueue.offer(successKilled);
        return flag;
    }

    public SuccessKilled consume() throws InterruptedException{
        return blockingQueue.take();
    }

    public int size(){
        return blockingQueue.size();
    }
}
