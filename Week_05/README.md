# 学习笔记

## 第二题-Bean 装配

题目：写代码实现Spring Bean的装配，方式越多越好（XML、Annotation都可以）。

答案：

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