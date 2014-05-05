package net.yech.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RabbitUtility {

    public static final String EXCHANGE_NAME = "mc";
    public static final String QUEUE_CACHE = "queue_cache";
    public static final String ROUTE_KEY_CACHE_UPDATE = "cache_update";

    private static Config config;


    public static Connection getConnection() throws IOException {

        if (config == null) {
            Properties prop = new Properties();
            InputStream in = Object.class.getResourceAsStream("/config.properties");
            prop.load(in);
            config = new Config(prop.getProperty("host").trim(), prop.getProperty("virtualHost").trim(),
                    prop.getProperty("user").trim(), prop.getProperty("password").trim());
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setVirtualHost(config.getVirtualHost());
        factory.setUsername(config.getUser());
        factory.setPassword(config.getPassword());
        factory.setConnectionTimeout(1000);
        return factory.newConnection();
    }

    //创建队列并绑定
    public static void declareQueue(Channel channel, String exchange, String queueName, String routeKey, int queueExpireHours, int queueMessageTtlHours) throws IOException {

        Map<String, Object> queueArgs = new HashMap<String, Object>();
        //队列过多久没有操作就过期
        queueArgs.put("x-expires", queueExpireHours * 60 * 60 * 1000);
        //队列中的消息最多存活多少时间
        queueArgs.put("x-message-ttl", queueMessageTtlHours * 60 * 60 * 1000);

        //declare一个durable=true exclusive=false autoDelete=false的队列
        channel.queueDeclare(queueName, true, false, false, queueArgs);

        //绑定队列
        channel.queueBind(queueName, exchange, routeKey);

    }

    public static void sendMessage(String exchange, String routeKey, String message, long expiration) throws IOException {

        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        //创建一个type=direct duration=true autoDelete=false的exchange
        channel.exchangeDeclare(exchange, "direct", true, false, null);

        //deliveryMode=2代表该消息要持久化,expiration表示该消息过期时间
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .contentType("text/plain").contentEncoding("UTF-8").deliveryMode(2)
                .expiration((new Long(expiration)).toString()).build();

        channel.basicPublish(exchange, routeKey, properties, message.getBytes());

        channel.close();
        connection.close();
    }
}
