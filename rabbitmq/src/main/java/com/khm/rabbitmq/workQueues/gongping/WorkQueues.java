package com.khm.rabbitmq.workQueues.gongping;


/**
 * WorkQueues!
 * 多个消费者来消费同一个消息队列: 这是一个公平分发:你一个执行完成后，应答后再分发另一个
 * 1、每个消费者在发送确认信息前，消息队列不发送下一条消息到消费者，一次只处理一条消息
 * （send 生产者）方中设置消息消费完成确认 见send.java
 * （Recv 消费者）方中设置 关闭消息自动应答 见recv1.java 
 */
public class WorkQueues
{
	
}
