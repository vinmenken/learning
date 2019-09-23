# Kafka简介

由Scala和Java编写,Kafka是一种高吞吐量的分布式发布订阅消息系统.

# 术语介绍

- Broker : Kafka集群包含一个或多个服务器，这种服务器被称为broker
- Topic : 每条发布到Kafka集群的消息都有一个类别，这个类别被称为Topic。（物理上不同Topic的消息分开存储，逻辑上一个Topic的消息虽然保存于一个或多个broker上但用户只需指定消息的Topic即可生产或消费数据而不必关心数据存于何处）
- Partition : Partition是物理上的概念，每个Topic包含一个或多个Partition.
- Producer : 负责发布消息到Kafka broker
- Consumer : 消息消费者，向Kafka broker读取消息的客户端。
- Consumer Group : 每个Consumer属于一个特定的Consumer Group（可为每个Consumer指定group name，若不指定group name则属于默认的group）。

# 消费模式

![这里写图片描述](https://img-blog.csdn.net/20180315151540326?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L3NoYW5nbWluZ3Rhbw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

为了照顾对MQ不是很了解的同学,先讲一下MQ的原理.一般MQ都是在服务端存储一个队列.生产者把消息丢到MQ server,消费者从MQ server消费.这样一来解决了生产者和消费者的高耦合问题,同时也解决了生产速度和消费速度差异导致的消费者跟不上生产者的生产速度而导致的消费者压力过大问题.

在kafka中的topic就是一系列队列的总称,称为一个主题.当然ActiveMQ和RabbitMQ中都有这个概念.一类消息都会丢到一个topic中去.

讲完topic我们讲一下partition(分区),这个东西是kafka独有的东西,也是kafka实现横向扩展和高并发的一个重要设计.我们试想一下,如果每个topic只有一个队列,随着业务增加topic里消息越来越多.多到一台server装不下了怎么办.为了解决这个问题,我们引入了partition这个概念.一个partition(分区)代表了一个物理上存在的队列.topic只是一组partition(分区)的总称,也就是说topic仅是逻辑上的概念.这样一来当topic上的消息越来越多.我们就可以将新增的partition(分区)放在其他server上.也就是说topic里边的partition(分区)可以分属于不同的机器.实际生产中,也基本都是这样玩的.

这里说一个特殊情况,有时我们创建了一个topic没有指定partition(分区)数量或者指定了partition(分区)数量为1,这时实际也是有一个默认的partition(分区)的,名字我忘记了.

从Producer(生产者)角度,一个消息丢到topic中任务就完成了.至于具体丢到了topic中的哪个partition(分区),Producer(生产者)不需要关注.这里kafka自动帮助我们做了负载均衡.当然如果我们指定某个partition(分区)也是可以的.这个大家官方文档和百度.

接下里我们讲Consumer Group(消费组),Consumer Group(消费组)顾名思义就是一组Consumer(消费者)的总称.那有了组的概念以后能起到什么作用.如果只有一组内且组内只有一个Consumer,那这个就是传统的点对点模式,如果有多组,每组内都有一个Consumer,那这个就是发布-订阅(pub-sub)模式.每组都会收到同样的消息.

最后讲最难理解也是大家讨论最多的地方,partition(分区)和Consumer(消费者)的关系.首先,**一个Consumer(消费者)的一个线程在某个时刻只能接收一个partition(分区)的数据,一个partition(分区)某个时刻也只会把消息发给一个Consumer(消费者)**.我们设计出来几种场景:

**场景一:** topic-1 下有partition-1和partition-2 
group-1 下有consumer-1和consumer-2和consumer-3 
所有consumer只有一个线程,且都消费topic-1的消息. 
**消费情况 :** consumer-1只消费partition-1的数据 
consumer-2只消费partition-2的数据 
consumer-3不会消费到任何数据 
**原因 :** 只能接受一个partition(分区)的数据

**场景二:** topic-1 下有partition-1和partition-2 
group-1 下有consumer-1 
consumer只有一个线程,且消费topic-1的消息. 
**消费情况 :** consumer-1先消费partition-1的数据 
consumer-1消费完partition-1数据后开始消费partition-2的数据 
**原因 :** 这里是kafka检测到当前consumer-1消费完partition-1处于空闲状态,自动帮我做了负载.所以大家看到这里在看一下上边那句话的”某个时刻” 
**特例:** consumer在消费消息时必须指定topic,可以不指定partition,场景二的情况就是发生在不指定partition的情况下,如果consumer-1指定了partition-1,那么consumer-1消费完partition-1后哪怕处于空闲状态了也是不会消费partition-2的消息的.

进而我们总结出了一条经验,同组内的消费者(单线程消费)数量不应多于topic下的partition(分区)数量,不然就会出有消费者空闲的状态,此时并发线程数=partition(分区)数量.反之消费者数量少于topic下的partition(分区)数量也是不理想的,原因是此时并发线程数=消费者数量,并不能完全发挥kafka并发效率.

最后我们看下上边的图,Consumer Group A的两个机器分别开启两个线程消费P0 P1 P2 P3的消息Consumer Group B的四台机器单线程消费P0 P1 P2 P3的消息就可以了.此时效率最高.

##  搭建Kafka环境

**安装Kafka**

下载：http://kafka.apache.org/downloads.html

```
tar zxf kafka-<VERSION>.tgz



cd kafka-<VERSION>
```

**启动Zookeeper**

启动Zookeeper前需要配置一下config/zookeeper.properties：

![img](http://static.oschina.net/uploads/space/2015/1208/122152_QREg_1434710.png)

接下来启动Zookeeper

```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

**启动Kafka Server**

启动Kafka Server前需要配置一下config/server.properties。主要配置以下几项，内容就不说了，注释里都很详细：

![img](http://static.oschina.net/uploads/space/2015/1208/122249_oZs4_1434710.png)

然后启动Kafka Server：

```
bin/kafka-server-start.sh config/server.properties
```

 **创建Topic**

```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

**查看创建的Topic**

```
bin/kafka-topics.sh --list --zookeeper localhost:2181
```

**启动控制台Producer****，向Kafka****发送消息**

```sh
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

This is a message
This is another message



^C
```

**启动控制台Consumer****，消费刚刚发送的消息**

```shell
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning



This is a message



This is another message
```

**删除Topic**

```
bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic test
```

注：*只有当delete.topic.enable=true时，该操作才有效*

**配置Kafka****集群（单台机器上）**

首先拷贝server.properties文件为多份（这里演示**4****个节点**的Kafka集群，因此还需要拷贝3份配置文件）：

```
cp config/server.properties config/server1.properties



cp config/server.properties config/server2.properties



cp config/server.properties config/server3.properties
```

修改server1.properties的以下内容：

```
broker.id=1



port=9093



log.dir=/tmp/kafka-logs-1
```

同理修改server2.properties和server3.properties的这些内容，并保持所有配置文件的zookeeper.connect属性都指向运行在本机的zookeeper地址localhost:2181。注意，由于这几个Kafka节点都将运行在同一台机器上，因此需要保证这几个值不同，这里以累加的方式处理。例如在server2.properties上：

```
broker.id=2



port=9094



log.dir=/tmp/kafka-logs-2
```

把server3.properties也配置好以后，依次启动这些节点：

```
bin/kafka-server-start.sh config/server1.properties &



bin/kafka-server-start.sh config/server2.properties &



bin/kafka-server-start.sh config/server3.properties &
```

**Topic & Partition**

Topic在逻辑上可以被认为是一个queue，每条消费都必须指定它的Topic，可以简单理解为必须指明把这条消息放进哪个queue里。为了使得Kafka的吞吐率可以线性提高，物理上把Topic分成一个或多个Partition，每个Partition在物理上对应一个文件夹，该文件夹下存储这个Partition的所有消息和索引文件。

现在在Kafka集群上创建备份因子为3，分区数为4的Topic：

```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 4 --topic kafka
```

说明：备份因子replication-factor越大，则说明集群容错性越强，就是当集群down掉后，数据恢复的可能性越大。所有的分区数里的内容共同组成了一份数据，分区数partions越大，则该topic的消息就越分散，集群中的消息分布就越均匀。

然后使用kafka-topics.sh的--describe参数查看一下Topic为kafka的详情：

![img](http://static.oschina.net/uploads/space/2015/1208/144854_ERJG_1434710.png)

输出的第一行是所有分区的概要，接下来的每一行是一个分区的描述。可以看到Topic为kafka的消息，PartionCount=4，ReplicationFactor=3正是我们创建时指定的分区数和备份因子。

另外：Leader是指负责这个分区所有读写的节点；Replicas是指这个分区所在的所有节点（不论它是否活着）；ISR是Replicas的子集，代表存有这个分区信息而且当前活着的节点。

拿partition:0这个分区来说，该分区的Leader是server0，分布在id为0，1，2这三个节点上，而且这三个节点都活着。

再来看下Kafka集群的日志：

![img](http://static.oschina.net/uploads/space/2015/1208/145002_llIL_1434710.png)

其中kafka-logs-0代表server0的日志，kafka-logs-1代表server1的日志，以此类推。

从上面的配置可知，id为0，1，2，3的节点分别对应server0, server1, server2, server3。而上例中的partition:0分布在id为0, 1, 2这三个节点上，因此可以在server0, server1, server2这三个节点上看到有kafka-0这个文件夹。这个kafka-0就代表Topic为kafka的partion0。

 

## kafka server-properties配置说明

```properties
broker.id =0
每一个broker在集群中的唯一表示，要求是正数。当该服务器的IP地址发生改变时，broker.id没有变化，则不会影响consumers的消息情况
log.dirs=/data/kafka-logs
kafka数据的存放地址，多个地址的话用逗号分割 /data/kafka-logs-1，/data/kafka-logs-2
port =9092
broker server服务端口
message.max.bytes =6525000
表示消息体的最大大小，单位是字节
num.network.threads =4
broker处理消息的最大线程数，一般情况下不需要去修改
num.io.threads =8
broker处理磁盘IO的线程数，数值应该大于你的硬盘数
background.threads =4
一些后台任务处理的线程数，例如过期消息文件的删除等，一般情况下不需要去做修改
queued.max.requests =500
等待IO线程处理的请求队列最大数，若是等待IO的请求超过这个数值，那么会停止接受外部消息，应该是一种自我保护机制。
host.name
broker的主机地址，若是设置了，那么会绑定到这个地址上，若是没有，会绑定到所有的接口上，并将其中之一发送到ZK，一般不设置
socket.send.buffer.bytes=100*1024
socket的发送缓冲区，socket的调优参数SO_SNDBUFF
socket.receive.buffer.bytes =100*1024
socket的接受缓冲区，socket的调优参数SO_RCVBUFF
socket.request.max.bytes =100*1024*1024
socket请求的最大数值，防止serverOOM，message.max.bytes必然要小于socket.request.max.bytes，会被topic创建时的指定参数覆盖
log.segment.bytes =1024*1024*1024
topic的分区是以一堆segment文件存储的，这个控制每个segment的大小，会被topic创建时的指定参数覆盖
log.roll.hours =24*7
这个参数会在日志segment没有达到log.segment.bytes设置的大小，也会强制新建一个segment会被 topic创建时的指定参数覆盖
log.cleanup.policy = delete
日志清理策略选择有：delete和compact主要针对过期数据的处理，或是日志文件达到限制的额度，会被 topic创建时的指定参数覆盖
log.retention.minutes=60*24 # 一天后删除
数据存储的最大时间超过这个时间会根据log.cleanup.policy设置的策略处理数据，也就是消费端能够多久去消费数据
log.retention.bytes和log.retention.minutes任意一个达到要求，都会执行删除，会被topic创建时的指定参数覆盖
log.retention.bytes=-1
topic每个分区的最大文件大小，一个topic的大小限制 = 分区数*log.retention.bytes。-1没有大小限log.retention.bytes和log.retention.minutes任意一个达到要求，都会执行删除，会被topic创建时的指定参数覆盖
log.retention.check.interval.ms=5minutes
文件大小检查的周期时间，是否处罚 log.cleanup.policy中设置的策略
log.cleaner.enable=false
是否开启日志压缩
log.cleaner.threads = 2
日志压缩运行的线程数
log.cleaner.io.max.bytes.per.second=None
日志压缩时候处理的最大大小
log.cleaner.dedupe.buffer.size=500*1024*1024
日志压缩去重时候的缓存空间，在空间允许的情况下，越大越好
log.cleaner.io.buffer.size=512*1024
日志清理时候用到的IO块大小一般不需要修改
log.cleaner.io.buffer.load.factor =0.9
日志清理中hash表的扩大因子一般不需要修改
log.cleaner.backoff.ms =15000
检查是否处罚日志清理的间隔
log.cleaner.min.cleanable.ratio=0.5
日志清理的频率控制，越大意味着更高效的清理，同时会存在一些空间上的浪费，会被topic创建时的指定参数覆盖
log.cleaner.delete.retention.ms =1day
对于压缩的日志保留的最长时间，也是客户端消费消息的最长时间，同log.retention.minutes的区别在于一个控制未压缩数据，一个控制压缩后的数据。会被topic创建时的指定参数覆盖
log.index.size.max.bytes =10*1024*1024
对于segment日志的索引文件大小限制，会被topic创建时的指定参数覆盖
log.index.interval.bytes =4096
当执行一个fetch操作后，需要一定的空间来扫描最近的offset大小，设置越大，代表扫描速度越快，但是也更好内存，一般情况下不需要搭理这个参数
log.flush.interval.messages=None
log文件”sync”到磁盘之前累积的消息条数,因为磁盘IO操作是一个慢操作,但又是一个”数据可靠性"的必要手段,所以此参数的设置,需要在"数据可靠性"与"性能"之间做必要的权衡.如果此值过大,将会导致每次"fsync"的时间较长(IO阻塞),如果此值过小,将会导致"fsync"的次数较多,这也意味着整体的client请求有一定的延迟.物理server故障,将会导致没有fsync的消息丢失.
log.flush.scheduler.interval.ms =3000
检查是否需要固化到硬盘的时间间隔
log.flush.interval.ms = None
仅仅通过interval来控制消息的磁盘写入时机,是不足的.此参数用于控制"fsync"的时间间隔,如果消息量始终没有达到阀值,但是离上一次磁盘同步的时间间隔达到阀值,也将触发.
log.delete.delay.ms =60000
文件在索引中清除后保留的时间一般不需要去修改
log.flush.offset.checkpoint.interval.ms =60000
控制上次固化硬盘的时间点，以便于数据恢复一般不需要去修改
auto.create.topics.enable =true
是否允许自动创建topic，若是false，就需要通过命令创建topic
default.replication.factor =1
是否允许自动创建topic，若是false，就需要通过命令创建topic
num.partitions =1
每个topic的分区个数，若是在topic创建时候没有指定的话会被topic创建时的指定参数覆盖
 
 
以下是kafka中Leader,replicas配置参数
 
controller.socket.timeout.ms =30000
partition leader与replicas之间通讯时,socket的超时时间
controller.message.queue.size=10
partition leader与replicas数据同步时,消息的队列尺寸
replica.lag.time.max.ms =10000
replicas响应partition leader的最长等待时间，若是超过这个时间，就将replicas列入ISR(in-sync replicas)，并认为它是死的，不会再加入管理中
replica.lag.max.messages =4000
如果follower落后与leader太多,将会认为此follower[或者说partition relicas]已经失效
##通常,在follower与leader通讯时,因为网络延迟或者链接断开,总会导致replicas中消息同步滞后
##如果消息之后太多,leader将认为此follower网络延迟较大或者消息吞吐能力有限,将会把此replicas迁移
##到其他follower中.
##在broker数量较少,或者网络不足的环境中,建议提高此值.
replica.socket.timeout.ms=30*1000
follower与leader之间的socket超时时间
replica.socket.receive.buffer.bytes=64*1024
leader复制时候的socket缓存大小
replica.fetch.max.bytes =1024*1024
replicas每次获取数据的最大大小
replica.fetch.wait.max.ms =500
replicas同leader之间通信的最大等待时间，失败了会重试
replica.fetch.min.bytes =1
fetch的最小数据尺寸,如果leader中尚未同步的数据不足此值,将会阻塞,直到满足条件
num.replica.fetchers=1
leader进行复制的线程数，增大这个数值会增加follower的IO
replica.high.watermark.checkpoint.interval.ms =5000
每个replica检查是否将最高水位进行固化的频率
controlled.shutdown.enable =false
是否允许控制器关闭broker ,若是设置为true,会关闭所有在这个broker上的leader，并转移到其他broker
controlled.shutdown.max.retries =3
控制器关闭的尝试次数
controlled.shutdown.retry.backoff.ms =5000
每次关闭尝试的时间间隔
leader.imbalance.per.broker.percentage =10
leader的不平衡比例，若是超过这个数值，会对分区进行重新的平衡
leader.imbalance.check.interval.seconds =300
检查leader是否不平衡的时间间隔
offset.metadata.max.bytes
客户端保留offset信息的最大空间大小
kafka中zookeeper参数配置
 
zookeeper.connect = localhost:2181
zookeeper集群的地址，可以是多个，多个之间用逗号分割 hostname1:port1,hostname2:port2,hostname3:port3
zookeeper.session.timeout.ms=6000
ZooKeeper的最大超时时间，就是心跳的间隔，若是没有反映，那么认为已经死了，不易过大
zookeeper.connection.timeout.ms =6000
ZooKeeper的连接超时时间
zookeeper.sync.time.ms =2000
```

 