package com.khm.rabbitmq.workQueues.round.robin;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * WorkQueues!
 * 多个消费者来消费同一个消息队列
 */
public class Send
{
	private static final String QUEUE_NAME = "queue_name"; 
	
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
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        for(int i = 0; i < 100; i++) {
        	String msg = "msg " + i;
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            System.out.println("Sent:" + msg);
            Thread.sleep(1000);
        }
        
        channel.close();
        connection.close();
    }
    
    /**
     * Receiving 消费者 consumer
     * @throws TimeoutException 
     * @throws IOException 
     */
    public static void recv() throws IOException, TimeoutException {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        DefaultConsumer consumer = new DefaultConsumer(channel) {
        	public void handleDelivery(String consumerTag, Envelope envelope, 
        			AMQP.BasicProperties properties,
                    byte[] body) throws IOException {
        		String msg = new String(body, "utf-8");
        		System.out.println(msg);
        	}
        };
        
        channel.basicConsume(QUEUE_NAME, true, consumer);
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
