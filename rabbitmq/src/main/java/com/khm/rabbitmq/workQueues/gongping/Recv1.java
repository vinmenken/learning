package com.khm.rabbitmq.workQueues.gongping;

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
public class Recv1
{
	private static final String QUEUE_NAME = "queue_name"; 
	
    public static void main( String[] args ) throws IOException, TimeoutException, InterruptedException
    {
        recv();
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
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        //start 公平分发: 
        channel.basicQos(1);
        //end 公平分发: 
        DefaultConsumer consumer = new DefaultConsumer(channel) {
        	public void handleDelivery(String consumerTag, Envelope envelope, 
        			AMQP.BasicProperties properties,
                    byte[] body) throws IOException {
        		String msg = new String(body, "utf-8");
        		System.out.println("recv1: " + msg);
        		
        		try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		finally {
        			//start 公平分发: 手动返回一个消息确认，因为自动应答已经关闭
        			channel.basicAck(envelope.getDeliveryTag(), false);
        			//end 公平分发:
        		}
        		
        	}
        };
        
        //start 公平分发:
        boolean autoAck = false; //关闭自动应答
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
        //end 公平分发:
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
