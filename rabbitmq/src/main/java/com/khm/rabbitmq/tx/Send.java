package com.khm.rabbitmq.tx;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * tx AMQP事务机制
 * 
 */
public class Send
{
	private static final String QUEUE_NAME = "queue_name_tx"; 
	
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
    public static void send() throws IOException, TimeoutException {
    	System.out.println( "producer 提供者!" );
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String msg = "msg";
        try {
			channel.txSelect(); //开启事务
			channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
			channel.txCommit(); //提交事务
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			channel.txRollback(); //回滚事务
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
