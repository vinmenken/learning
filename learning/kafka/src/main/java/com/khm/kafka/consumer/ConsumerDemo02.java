package com.khm.kafka.consumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerDemo02 {
	public static void main( String[] args ) throws InterruptedException, ExecutionException
    {
		Properties props = new Properties();
		//
	     props.put("bootstrap.servers", "localhost:9092");
	     //消息费者组
	     props.put("group.id", "test");
	     //自动提交确认: 默认5秒钟，一个 Consumer 将会提交它的 Offset 给 Kafka，
	     //或者每一次数据从指定的 Topic 取回时，将会提交最后一次的 Offset。
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     //指30秒内，消费者必须与kafka来一次心跳
	     props.put("session.timeout.ms", "30000");
	     
	     //重置offset位置,当消费者组改变了offset会重置、当过了保存的时间已经不存在了offset会重置
	     //earliest
	     //当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
	     //latest(默认值)
	     //当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
	     //none
	     //topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
//	     props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	     
	     //返序列化
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
	     //订阅主题
	     consumer.subscribe(Arrays.asList("topic01", "topic02"));
	     while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(100);
	         for (ConsumerRecord<String, String> record : records)
	             System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
	     }
    }
	
}
