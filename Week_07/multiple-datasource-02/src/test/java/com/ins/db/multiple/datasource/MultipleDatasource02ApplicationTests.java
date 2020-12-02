package com.ins.db.multiple.datasource;

import com.ins.db.multiple.datasource.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MultipleDatasource02ApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	@Test
	public void contextLoads() {
		//jdbcTemplate.update("insert into t1 value (?)", 4);
		List<User> result = jdbcTemplate.query("select * from t1", new BeanPropertyRowMapper<>(User.class));
		result.forEach(System.out::println);
	}

}
