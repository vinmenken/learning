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
 *  
 * 
 */
public class Recv1
{
	private static final String QUEUE_NAME = "queue_name_confirm"; 
	
    public static void main( String[] args ) throws IOException, TimeoutException, InterruptedException
    {
        recv();
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
