package com.khm.rabbitmq.confirm;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * confirm机制
 * 异步 机制 ：本地维护一个有序的set，生产者发送消息时，同时写到 消息队列和 set（只是记录消息的标记符）中，
 * 当消息 
 * 
 */
public class Send1
{
	private static final String QUEUE_NAME = "queue_name_confirm_ConfirmListener"; 
	
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
        
        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());
        
        //异步监听确认和未确认的消息
        channel.addConfirmListener(new ConfirmListener() {
        	/**
        	 *  如果参数 multiple 为 true 表示是多条消息，多少条消息取决于 deliveryTag
        	 *  消息确认有可能是批量确认的，是否批量确认在于返回的multiple的参数，此参数为bool值，
        	 *  如果true表示批量执行了deliveryTag这个值以前的所有消息，如果为false的话表示单条确认
        	 */
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("未确认消息，标识：" + deliveryTag);
                if(multiple) {
                	confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                	confirmSet.remove(deliveryTag);
                }
            }
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
                if(multiple) {
                	confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                	confirmSet.remove(deliveryTag);
                }
            }
        });
        //这种异步消息确认机制，我们中管发，至于消息是否发送成功我们通过异步监听来处理，性能好
        for (int i = 0; i < 10; i++) {
        	long seqNo = channel.getNextPublishSeqNo(); //获取消息标记号 deliveryTag
            String message = String.format("时间 => %s", new Date().getTime());
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            confirmSet.add(seqNo); //把这个消息标记号添加到 消息确认集中
        }
        
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
