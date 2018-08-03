package exp.fw.fkf;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;
 
/**
 * <PRE>
 * kafka消费者样例
 * </PRE>
 * <br/><B>PROJECT : </B> kafka
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DemoConsumer {
 
	public static void main(String[] args) throws Exception {
		final String KAFKA_SOCKET = "127.0.0.1:9092";
		final String TOPIC = "exp-topic-test";
		final String GROUP_ID = "group-1";
		
		DemoConsumer consumer = new DemoConsumer(KAFKA_SOCKET, GROUP_ID);
		consumer.consume(TOPIC);
    }
	
	
	
	/** kafka消费者对象 */
    private Consumer<String, String> consumer;
 
    /**
     * 构造函数
     * @param kafkaSocket
     * @param groupId Consumer所在的Group
     * 		（一个Topic可以对应多个Group, 不论是多播还是单播, kafka只会把消息发到Group, 
     * 		  Consumer只能收到它所在的Group的消息）
     */
    private DemoConsumer(String kafkaSocket, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSocket);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");	// 消息偏移, latest表示最新的消息
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");	// 自动提交
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");	// 自动提交间隔(ms)
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");	// 会话超时(ms)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
        		StringSerializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
        		StringSerializer.class.getName());
        
        this.consumer = new KafkaConsumer<String, String>(props);
    }
 
    /**
     * 从指定主题连续消费消息
     * @param TOPICS 消息主题集, 当主题只有一个分区时, 逻辑上可以认为主题是一个队列
     * @throws Exception 
     */
    @SuppressWarnings("deprecation")
	private void consume(final String... TOPICS) throws Exception {
        consumer.subscribe(Arrays.asList(TOPICS)); // 可同时消费多个topic
        
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for(ConsumerRecord<String, String> record : records) {
            	String msg = String.format("offset = %d, key = %s, value = %s", 
            			record.offset(), record.key(), record.value());
            	
                System.out.printf(msg);
                Thread.sleep(10);
            }
        }
    }
    
    public void close() {
    	consumer.close();
    }
 
}

