package com.ins.starteruser;

import com.ins.demo.domain.ISchool;
import com.ins.demo.domain.Klass;
import com.ins.demo.domain.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StarterUserApplicationTests {

	@Autowired
	private Student student;

	@Autowired
	private Klass klass;

	@Autowired
	private ISchool school;

	@Test
	public void contextLoads() {
		student.study();
		klass.dong();
		school.ding();
	}
}
