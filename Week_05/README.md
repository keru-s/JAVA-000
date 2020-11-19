# 学习笔记

## 第二题-Bean 装配

题目：写代码实现Spring Bean的装配，方式越多越好（XML、Annotation都可以）。

答案：

相关代码在 `BeanDemo` 中

在 Spring 中最常见的 Bean 装配机制有三种，分别是：

- 在 XML 上显式配置
- 在 Java 中显式配置
- 隐式的 bean 发现机制和自动装配

下面逐一介绍三种装配机制，为了方便示例，需要创建几个 bean，这里用音响系统举例。首先创建一个 `CompactDisc` 接口，然后创建一个 `CDPlayer` 类，让 Spring 将 `CompactDiscBean` 注入到  `CDPlayer` 中。

```JAVA
package soundsystem;

public interface CompactDisc {
    void play();
}
```

```JAVA
package soundsystem;

public class CDPlayer implements MediaPlayer {
    
    private CompactDisc cd;

    public CDPlayer(CompactDisc cd) {
        this.cd = cd;
    }

    @Override
    public void play() {

    }
}

```



### 一、自动化装配

Spring 中最强大的装配莫过于自动化装配了，它减少了大量的重复编码。

Spring 从两个角度来实现自动化装配：

- 组件扫描（component scanning）：Spring 会自动发现应用上下文创建的 Bean。
- 自动装配（autowiring）：Spring 自动满足 Bean 之间的依赖。

#### 创建可被发现的 Bean

##### `@Component`注解

首先，实现 `CompactDisc` 接口，创建一个 `Fantasy` 的CD类，然后标记上 `@Component`，这个注解标明该类会作为组件类，并告知 Spring 要为这个类创建 Bean。

```JAVA
package soundsystem;

import org.springframework.stereotype.Component;

@Component
public class Fantasy implements CompactDisc {

    private String title = "范特西";
    private String artist = "周杰伦";

    @Override
    public void play() {
        System.out.println("正在播放 " + artist + " 的 " + title);
    }
}
```

但是，**组件扫描默认是不开启的，因此需要显示配置 Spring**，让其寻找带有 `@Component` 注解的类，并为其创建 Bean。

首先，创建一个 `CDPlayerConfig` 来定义Spring装配规则，然后给这个类加上 `@ComponentScan` 注解，默认情况下，`@ComponentScan` 会扫描与配置类相同的包，这里，即会扫描 `soundsystem` 这个包，以及这个包的子包，查找所有带 `@Component` 注解的类，这样的话，就能发现 `Fantasy` 这个类，然后自动为其创建一个 Bean

```JAVA
package soundsystem;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class CDPlayerConfig {
}
```

通过一个简单的 JUnit 测试即可验证：

```JAVA
package soundsystem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDPlayerConfig.class)
public class CDPlayerTest {
    @Autowired
    private CompactDisc cd;

    @Test
    public void cdShouldNotBeNull() {
        assertNotNull(cd);
    }
}
```

##### `@Named` 注解

除了 `@Component` 注解之外，还有一个注解是 `@Named`，这是 Java依赖注入规范（`javax.inject`） 中提供的注解，Spring 支持将其作为 `@Component` 注解的替代方案。

```java
package soundsystem;

import javax.inject.Named;

@Named
public class Fantasy implements CompactDisc {

}
```



#### 为 bean 添加注解实现自动装配

##### `@Autowired`

自动装配：就是让 Spring 自动满足 bean 依赖的一种方法，在满足依赖的过程中，Spring 会在应用上下文中寻找匹配某个bean所需要的其他bean。

通过 `@Autowire` 注解，即可实现自动装配，下面是一个将 `CompactDisc` 注入 `CDPlayer` 的例子

```JAVA
package soundsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CDPlayer implements MediaPlayer {

    private CompactDisc cd;

    @Autowired
    public CDPlayer(CompactDisc cd) {
        this.cd = cd;
    }

    @Override
    public void play() {
        cd.play();
    }
}
```

这种方式叫构造器注入，还有一种方式：属性注入，是将 `@Autowired` 放在 Setter 方法上

```java
    @Autowired
    public void setCd(CompactDisc cd) {
        this.cd = cd;
    }
```

当然最常用的是直接在成员变量上使用 `@Autowired` 

```JAVA
@Component
public class CDPlayer implements MediaPlayer {

    @Autowired
    private CompactDisc cd;

    @Override
    public void play() {
        cd.play();
    }
}
```

使用以下的测试验证是否自动装配成功

```JAVA
package soundsystem;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDPlayerConfig.class)
public class CDPlayerTest {

    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog();

    @Autowired
    private MediaPlayer player;

    @Test
    public void playByAutowired() {
        player.play();
        assertEquals("正在播放：周杰伦的范特西\r\n", log.getLog());
    }
}
```

这里使用了 [System Rules](https://www.baeldung.com/java-testing-system-out-println#using-system-rules) 库提供的 `SystemOutRule` 来验证 `System.out` 的输出内容。

##### `@Inject`

自动装配除了使用 Spring 定义的 `@Autowired` 之外，还可以使用 `Javax.inject` 中的 `@Inject` 注解进行替换，Spring 同样支持该注解。



### 二、通过 Java 代码装配 bean

尽管组件扫描+自动装配来实现Spring的自动化配置是推荐的方式，但是自动化装配并不是万能的，比如需要将第三方库的组件装配到应用中时，是没法在它的类上添加 `@Component` 和 `@Autowired` 注解的，这时候，就需要使用显式装配了。

第一种显式装配方式就是通过 JavaConfig ，对原来的 config 类进行改造

```JAVA
package soundsystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CDPlayerConfig {

    @Bean(name = "firstAlbum")
    public CompactDisc jay(){
        return new Jay();
    }

    @Bean
    public CDPlayer cdPlayer(CompactDisc compactDisc){
        return new CDPlayer(compactDisc);
    }
}
```

- 创建 JavaConfig 类的关键在于为其添加 `@Configuration` 注解，该注解会表明这个类是一个配置类。
- 创建一个方法，该方法的返回值是所需创建的实例，然后给方法增加 `@Bean` 注解，该注解会告诉Spring，这个方法将返回一个对象，且该对象要注册为 Spring应用上下文中的 Bean。
  - 默认情况下，bean 的ID与带有 `@Bean` 注解的方法名是一样的，如果需要重命名，可以通过 name 属性来指定一个不同的名字。
- 如果需要对 bean 进行装配，那么直接在 `@Bean` 注解的方法入参中，声明你需要的 Bean，当 Spring 调用 `cdPlayer()` 方法创建 `cdPlayerBean` 的时候，它会自动装配一个 CD 类到配置方法中。

通过测试可以得到结果是正确的：

```Java
    @Test
    public void playByJavaConfig() {
        player.play();
        assertEquals("正在播放：周杰伦的周杰伦\r\n", log.getLog());
    }	
```



### 三、通过 XML 装配 bean

第三种方式是通过 XML 来进行装配。

首先在 `resources` 文件夹中创建一个 XML 文件，一个Spring的配置文件，最少要包含以下的内容：

```XML
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

</beans>
```

`beans` 是所有 Spring 配置的根元素，它定义了可以配置的 Spring XML 元素。

要在XML中声明一个 bean，我们需要使用 `<bean>` 元素，它类似于 `@Bean` 注解。

```xml
<bean id="fiveAlbum" class="soundsystem.QiLiXiang"/>
```

- class：是全类名，定义了要装配的 bean
- id：给 bean 命名，如果不给明确的 id，bean就会以全限定类名来进行命名，上面的例子中，命名就是 `soundsystem.QiLiXiang#0`，`#0` 用于区分相同类型的其他Bean。

如果需要使用构造器注入，那么可以写成这样：

```xml
<bean id="cdPlayer" class="soundsystem.CDPlayer">
    <constructor-arg ref="fiveAlbum"/>
</bean>
```

也可以使用 Spring 3.0 引入的 C 命名空间

```XML
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:c="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="fiveAlbum" class="soundsystem.QiLiXiang"/>
    
	<bean id="cdPlayer" class="soundsystem.CDPlayer" c:cd-ref="fiveAlbum"/>
    
</beans>
```

- `c:cd-ref="fiveAlbum"`：属性名以 `c:` 开头，也就是命名空间的前缀。接下来是要装配的构造器参数名， `-ref` 是个约定，它会告诉 Spring，正在装配的是一个 bean的引用，引用的名字为 `fiveAlbum`

然后我们进行一下测试，需要注意的是，这个我们要把配置文件切换成新创建的配置文件

`@ContextConfiguration(locations = {"classpath*:/applicationContext.xml"})`

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml"})
public class CDPlayerTest {

    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog();

    @Autowired
    private CompactDisc cd;

    @Autowired
    private MediaPlayer player;

    @Test
    public void playByXML() {
        player.play();
        assertEquals("正在播放：周杰伦的七里香\r\n", log.getLog());
    }
}
```



除了以上三种常见的方法，还可以

- 组合导入：
  - JavaConfig 通过 `@Import` 实现；
  - 在XML中引入 XML，使用 `<import>` 元素，
- 以及混合导入：
  - 比如在 JavaConfig 中引入 XML 配置，使用`@ImportResource`指定 XML 文件；
  - XML 中导入 JavaConfig，通过 `<bean>` 元素来实现。



## 第四题-自动配置与Starter

题目：给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。

答案：

starter 位于 `student-starter` 项目中，`starter-user` 中引入了 `student-starter` ，并于单元测试中使用了 Starter 中的类。

### 自动配置

Spring 官方是这样[定义自动配置 Bean](https://docs.spring.io/spring-boot/docs/2.0.0.M3/reference/html/boot-features-developing-auto-configuration.html#boot-features-custom-starter)的：

> auto-configuration is implemented with standard `@Configuration` classes. Additional `@Conditional` annotations are used to constrain when the auto-configuration should apply. Usually auto-configuration classes use `@ConditionalOnClass` and `@ConditionalOnMissingBean` annotations. This ensures that auto-configuration only applies when relevant classes are found and when you have not declared your own `@Configuration`.

首先，我们创建一个配置属性的bean，用于接收 `application.properties` 中定义的属性

```java
@ConfigurationProperties(prefix = "insight.student")
@Data
public class StudentProperties {
    private int id;
    private String name;
}
```

这样，我们就可以通过 

```properties
insight.student.id=1
insight.student.name=insight
```

`insight.student` 前缀的配置来给属性Bean赋值。

然后，我们创建一个配置类，用于装配各个类

```JAVA
@Configuration
@EnableConfigurationProperties({StudentProperties.class})
public class SchoolConfiguration {

    @Bean
    public Student student100(StudentProperties properties){
        Student result = new Student();
        result.setId(properties.getId());
        result.setName(properties.getName());
        return result;
    }

    @Bean
    @ConditionalOnMissingBean
    public Klass klass(Student student){
        Klass result = new Klass();
        result.setStudents(Collections.singletonList(student));
        return result;
    }

    @Bean
    @ConditionalOnClass({Klass.class,Student.class})
    public ISchool school(){
        return new School();
    }
}
```

接着，创建一个自动配置类，用于加载所有的配置类

```JAVA
@Configuration
@Import(SchoolConfiguration.class)
public class SchoolAutoConfiguration {
}
```

最后，在 `resource` 目录下，创建 `META-INF` 文件夹，然后创建 `spring.factories` 文件，该文件中定义需要自动配置的类

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.ins.demo.domain.config.SchoolAutoConfiguration
```

### 定义 Starter

根据 [Spring 官方的定义](https://docs.spring.io/spring-boot/docs/2.0.0.M3/reference/html/boot-features-developing-auto-configuration.html#boot-features-custom-starter)：

> A full Spring Boot starter for a library may contain the following components:
>
> - The `autoconfigure` module that contains the auto-configuration code.
> - The `starter` module that provides a dependency to the autoconfigure module as well as the library and any additional dependencies that are typically useful. In a nutshell, adding the starter should be enough to start using that library.

因此，有了自动配置，我们只需要在 `pom.xml` 中定义好自动配置需要的模块，即可提供一个 Starter 。

定义 `pom.xml` 中的依赖，这里我们引入 lombok

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.0.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.ins</groupId>
    <artifactId>student-demo-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>student-demo</name>
    <description>Demo starter project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>

```

 然后运行 `mvn install` 命令，将 starter打包到本地仓库。

**需要注意的是：**在运行打包之前，需要将 `pom.xml` 中自带的 plugin 删除，否则打包出来的 jar 是一个胖jar，是一个可运行的项目而非一个 Starter。

```
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```

然后在另一个项目引入 starter。

```xml
	<dependencies>
		<dependency>
			<groupId>com.ins</groupId>
			<artifactId>student-demo-spring-boot-starter</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

配置上属性

```properties
insight.student.id=1211
insight.student.name=insight
```

即可运行单元测试，检测bean是否注入成功

```java
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
```



## 第六题-JDBC 接口和数据库连接池

题目：

> 研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：
> 1）使用 JDBC 原生接口，实现数据库的增删改查操作。
> 2）使用事务，PrepareStatement 方式，批处理方式，改进上述操作。
> 3）配置 Hikari 连接池，改进上述操作。提交代码到 Github。

答案

在 `jdbc` 模块中

### 使用JDBC原生接口实现CRUD

在 `JdbcStatementCase` 类中

### 使用事务

使用事务的方式很简单，将自动提交改为手动提交即可。

```java
    /**
     * 使用事务
     */
    private static void insertWithTransaction() {
        String sql = "insert into student(name) value (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            statement.setString(1, "张无忌");
            statement.executeUpdate();
            statement.setString(1, "赵敏");
            //制造异常
            int tmp=1/0;
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
```

### 配置 Hikari 连接池

由于 SpringBoot 2.0 之后，默认使用了 Hikari 做为数据库连接池，因此，只需在 `application.properties` 中增加配置项即可使用 Hikari

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true
spring.datasource.username=test
spring.datasource.password=test
spring.datasource.hikari.connection-timeout=20000
```

使用单测检查连接情况

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void contextLoads() {
		final List<String> name_from_student = jdbcTemplate.queryForList("select name from student", String.class);
		name_from_student.forEach(System.out::println);
	}

}
```

结果为：

```bash
2020-11-19 15:00:24.561  INFO 26340 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2020-11-19 15:00:24.730  INFO 26340 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
………………
```

`HikariPool-1 - Start completed.` 可以看到已成功使用 Hikari 连接池。