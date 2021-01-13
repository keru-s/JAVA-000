# 学习笔记

## 周四-第一题

搭建 ActiveMQ 服务，基于 JMS，写代码分别实现对于 queue 和 topic 的消息生产和消费，代码提交到 github。



## queue的生产和消费

### 依赖和配置

首先引入activeMq的依赖。

```xml
<dependency>    
   <groupId>org.springframework.boot</groupId>    
   <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

然后，配置相关属性。

```properties
# ActiveMQ
spring.activemq.broker-url=tcp://127.0.0.1:61616
spring.activemq.user=admin
spring.activemq.password=admin
spring.activemq.pool.enabled=true
spring.activemq.pool.max-connections=100
```

### 定义POJO类型

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    private String to;
    private String body;

    @Override
    public String toString() {
        return String.format("Email{to=%s, body=%s}", getTo(), getBody());
    }
}
```

### 消费者的实现

接着，实现一个消费者

```java
@Component
public class JmsReceiver {

    @JmsListener(destination = "mailbox")
    public void receiverMessage(Email email){
        System.out.println("Received <" + email + ">");
    }
}
```

通过 `@JmsListener` 来指定该方法对某一个 queue 中的内容进行消费。这里，我们指定这个方法消费 `mailbox` 队列。

然后在启动类上增加注解：`@EnableJms`，使得  `@JmsListener`  能够生效。

```JAVA
@SpringBootApplication
@EnableJms
public class ActiveMqDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiveMqDemoApplication.class, args);
    }

}
```

### 生产者实现

首先对`JmsTemplate`进行配置

```java
@Configuration
public class JmsConfig {

    @Autowired
    private Environment env;

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(env.getProperty("spring.activemq.broker-url"));
        connectionFactory.setUserName(env.getProperty("spring.activemq.user"));
        connectionFactory.setPassword(env.getProperty("spring.activemq.password"));
        return connectionFactory;
    }


    @Bean
    public MessageConverter jacksonJmsMessageConverter(){
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        /* Specify the name of the JMS message property that carries the type id for the contained object: either a mapped id value or a raw Java class name.
        Default is none. NOTE: This property needs to be set in order to allow for converting from an incoming message to a Java object.*/
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
        return jmsTemplate;
    }
}
```

这里定义了好几个 Bean，分别为：

- `ConnectionFactory`：定义`connectionFactory` 为 `ActiveMQConnectionFactory`
- `MessageConverter`：默认情况下，JmsTemplate 只能对 string、map、byte、实现 Serializable 接口的POJO 进行消息的转换，为了能将普通的POJO当做消息进行发送，因此将默认的 `MessageConverter` 修改为 `MappingJackson2MessageConverter`，使用 Jackson 对类进行转换。
  - `TargetType`：设置转换的类型，默认只支持 bytes 和 text。
  - `TypeIdPropertyName`：设置保存类属性的属性名，这个值必须设置，否则会出现无法将消息转换成 POJO 的情况。
- `JmsTemplate`：将定义好的 `ConnectionFactory`，`MessageConverter`设置到 `jmsTemplate` 中

然后定义一个生产者

```JAVA
@Service
public class JmsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendEmail(Email email) {
        System.out.println("Sending an email message.");
        jmsTemplate.convertAndSend("mailbox", email);
    }
}
```

这里，将email对象进行转换，然后发送到 `mailbox` 队列中。

### 整合

创建一个 Controller，来调用生产者：

```JAVA
@RestController
public class JmsController {

    @Autowired
    private JmsProducer jmsProducer;

    @PostMapping("/sendEmail")
    public JmsResponse sendEmail(Email email) {
        jmsProducer.sendEmail(email);
        return JmsResponse.success();
    }
}
```

调用：

```
POST http://localhost:8080/sendEmail
{
  "to": "insight@gmail.com",
  "body": "hello,world!"
}
```

可以看到打印出相关信息：

```
Sending an email message.
Received <Email{to=insight@gmail.com, body=hello,world!}>
```



### 参考资料

[Spring：Messaging with JMS](https://spring.io/guides/gs/messaging-jms/)

[ActiveMQ — Getting Started with SpringBoot.](https://medium.com/@mailshine/activemq-getting-started-with-springboot-a0c3c960356e)

[MappingJacksonMessageConverter](https://docs.spring.io/spring-framework/docs/3.2.2.RELEASE_to_4.0.0.M1/Spring%20Framework%203.2.2.RELEASE/org/springframework/jms/support/converter/MappingJacksonMessageConverter.html#typeIdPropertyName)



## 周六-第一题

搭建一个 3 节点 Kafka 集群，测试功能和性能；实现 spring kafka 下对 kafka 集群的操作，将代码提交到 github。