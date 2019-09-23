package com.khm.kafka.producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class producer01 {
		
	public static void main( String[] args ) throws InterruptedException, ExecutionException
    {
        System.out.println( "Hello World!" );
        Properties properties = new Properties();
        //指定kafka集群
        
//      properties.put("bootstrap.servers", "localhost:9092");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //主分区和备份分区都收到消息后，才返回确认信息给生产者
        properties.put("acks", "all");
        //重试次数
        properties.put("retries", 0);
        //批次大小，一次发送16k
        properties.put("batch.size", 16384);
        //等待1秒钟发送，这个参数与上面批次相互相成，表示消息没有达到上面参数设定的16k，只需要待1秒钟就能发送
        properties.put("linger.ms", 1000);
        //recordAccumulator变量大小，因为生产者发送是两个线程 ，见 技术点如何设置说明
        properties.put("buffer.memory", 33554432);
        //生产者通过序列化类，对key 与 value 进行序列化
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //用户还能自定义分区分配的策略类,如果不写有默认处理类来进行分配
//      properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "写上自定义的分区策略类，这个类需要实现partitioner接口");
        
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
//        for(int i = 0; i < 10; i++) {
//            producer.send(new ProducerRecord<String, String>("topic01", Integer.toString(i), "test" + Integer.toString(i)));
//        }
//        在这里的send方法中调用了get()方法，使得消息的发送不是异步（粗解释不准确），能使单个分区的消息一定是有序的
//        for(int i = 0; i < 10; i++) {
//          producer.send(new ProducerRecord<String, String>("topic01", 
//        		  Integer.toString(i), "test" + Integer.toString(i))).get();
//        }
        
        //下面是调用了回调方法
        for(int i = 0; i < 10; i++) {
        	ProducerRecord producerRecord = new ProducerRecord<String, String>("topic01", Integer.toString(i), "test" + Integer.toString(i));
            producer.send(producerRecord, new Callback() {

				@Override
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					if(exception == null) {
						System.out.println(metadata.offset());
					}
				}
            	
            });
        }
        
//        try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        producer.close();
    }

}
