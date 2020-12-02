package com.ins.db.batch.insert;

import com.ins.db.batch.insert.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchInsertApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testQuery() {
        List<User> result = jdbcTemplate.query("select * from t1", new BeanPropertyRowMapper<>(User.class));
        result.forEach(System.out::println);
    }

    @Test
    public void testBatchInsert() {
        String sql = "INSERT INTO `order`(serial_num,state,user_id,good_id,create_tm,modify_tm) VALUES" +
                "(?,1,?,?,?,?)";
        final Date now = new Date(1606922265000L);
        final int prefix = new Random().nextInt(10000000);
        final List<Integer> limit = IntStream.rangeClosed(1, 10000000).boxed().collect(Collectors.toList());

        final long startTime = System.currentTimeMillis();
        jdbcTemplate.batchUpdate(sql, limit, 3000, new ParameterizedPreparedStatementSetter<Integer>() {
            @Override
            public void setValues(PreparedStatement ps, Integer index) throws SQLException {
                ps.setInt(1, prefix+index);
                ps.setInt(2, new Random().nextInt(10000));
                ps.setInt(3, new Random().nextInt(10000));
                ps.setDate(4, now);
                ps.setDate(5, now);
            }
        });
        final long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime));
    }


}
