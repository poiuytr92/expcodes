package exp.fw.fkf;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
 
/**
 * <PRE>
 * kafka生产者样例
 * </PRE>
 * <br/><B>PROJECT : </B> kafka
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DemoProducer {
 
	public static void main(String[] args) throws Exception {
		final String KAFKA_SOCKET = "127.0.0.1:9092";
		final String TOPIC = "exp-topic-test";
		
		DemoProducer producer = new DemoProducer(KAFKA_SOCKET);
		producer.produce(TOPIC);
		producer.close();
    }
	
	
	
	/** kafka生产者对象 */
    private KafkaProducer<String, String> producer;
 
    /**
     * 构造函数
     * @param kafkaSocket
     */
    private DemoProducer(String kafkaSocket) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSocket);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
 
        this.producer = new KafkaProducer<String, String>(props);
    }
 
    /**
     * 连续发送消息到指定主题
     * @param TOPIC 消息主题, 当主题只有一个分区时, 逻辑上可以认为主题是一个队列
     * 		（当前版本的kafka默认会自动创建不存在的主题, 无需预建）
     * @throws Exception
     */
    public void produce(final String TOPIC) throws Exception {
        for(int i = 0; i < 100; i++) {
            String data = String.format("[%s] http://exp-blog.com", String.valueOf(i));
            ProducerRecord<String, String> msg = new ProducerRecord<String, String>(TOPIC, data);
            
            producer.send(msg);
            Thread.sleep(10);
        }
    }
    
    public void close() {
    	producer.close();
    }
 
}

