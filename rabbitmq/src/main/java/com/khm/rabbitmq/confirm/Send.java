package com.khm.rabbitmq.confirm;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * confirm机制
 * 单条或批量 机制
 * 
 */
public class Send
{
	private static final String QUEUE_NAME = "queue_name_confirm"; 
	
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
        
        channel.confirmSelect(); //消息确认（消息队列收到消息后的confirm确认机制）机制开启
        
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        String msg = "msg";
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        
        //注意：批量发送，也是这种写法，只要批量发送完成，调用下面代码，就ok
        //消息确认（消息队列收到消息后的confirm确认机制）还需要下面这样做判断
        if(!channel.waitForConfirms()) {
        	System.out.println("消息发送失败！");
        } else {
        	System.out.println("消息发关成功！");
        }
        
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
