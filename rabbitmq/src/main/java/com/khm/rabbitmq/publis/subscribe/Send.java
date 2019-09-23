package com.khm.rabbitmq.publis.subscribe;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * publish/subscribe 订阅模型
 * 多个消费者来消费同一个消息队列
 */
public class Send
{
	private static final String EXCHANGE_NAME = "exchange_name_fanout"; 
	
    public static void main( String[] args ) throws IOException, TimeoutException, InterruptedException
    {
        send();
    }
    
    /**
     * producer 提供者
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException 
     */
    public static void send() throws IOException, TimeoutException, InterruptedException {
    	System.out.println( "producer 提供者!" );
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        
        //注意：如果这个时候没有 队列绑定到 交换机 而交换机收到的消息 会丢失
        String msg = "msg: fanout";
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());
        System.out.println("Sent:" + msg);
        
        channel.close();
        connection.close();
    }
    
    
    public static Connection getConnection() throws IOException, TimeoutException {
    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost("127.0.0.1");
    	factory.setPort(5672);
    	factory.setVirtualHost("/");
    	factory.setUsername("guest");
    	factory.setPassword("guest");
    	return factory.newConnection();
    }
}
