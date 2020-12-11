package com.ins.db.multiple.datasource;

import com.ins.db.multiple.datasource.domain.Order;
import com.ins.db.multiple.datasource.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingApplicationTests {

    @Autowired
    private OrderRepository orderRepository;


    @Test
    public void testSave() {
        Random random = new Random();
        final Date date = new Date();

        for (int i = 0; i < 10_00; i++) {
            final Order order = new Order();
            order.setState(1);
            order.setCreateTm(date);
            order.setUserId((long) (Math.random() * 1024));
            order.setSerialNum(123L+random.nextInt(1000000));
            orderRepository.save(order);
        }

    }

}
