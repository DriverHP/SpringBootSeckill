package com.test.seckill.common.repository;

import com.test.seckill.common.entity.Seckill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeckillRepository extends JpaRepository <Seckill, Long> {

}

