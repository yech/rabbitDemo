package net.yech.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;

public class Consumer {


    public static void main(String args[]) throws IOException, InterruptedException {
        Connection connection = RabbitUtility.getConnection();
        Channel channel = connection.createChannel();

        RabbitUtility.declareQueue(channel, RabbitUtility.EXCHANGE_NAME, RabbitUtility.QUEUE_CACHE, RabbitUtility.ROUTE_KEY_CACHE_UPDATE, 7 * 24, 24);

        //1次处理1条消息
        channel.basicQos(1);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(RabbitUtility.QUEUE_CACHE, false, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            String routingKey = delivery.getEnvelope().getRoutingKey();
            //这里处理消息

            Thread.sleep(5000);

            //手工确认
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
        }
    }

}
