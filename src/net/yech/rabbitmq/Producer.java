package net.yech.rabbitmq;

import java.util.Random;

public class Producer {

    public static void main(String[] args) {


        //消息1天后过期
        long expiration = 24 * 60 * 60 * 1000;

        try {
            for (int i = 0; i < 10; i++) {
                String message = "update cache: No." + i;
                RabbitUtility.sendMessage(RabbitUtility.EXCHANGE_NAME, RabbitUtility.ROUTE_KEY_CACHE_UPDATE, message, expiration);
                System.out.println(" [x] Sent : " + message);
                Thread.sleep((new Random()).nextInt(1000));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




    }

}
