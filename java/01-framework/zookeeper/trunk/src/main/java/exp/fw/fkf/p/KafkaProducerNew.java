package exp.fw.fkf.p;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
 
import java.util.Properties;
 
/**
* producer: org.apache.kafka.clients.producer.KafkaProducer;
 * */
 
public class KafkaProducerNew {
 
	public static void main(String[] args) {
        new KafkaProducerNew().produce();
    }
	
    private final KafkaProducer<String, String> producer;
 
    public final static String TOPIC = "exp-topic-test";
 
    private KafkaProducerNew() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
 
        producer = new KafkaProducer<String, String>(props);
    }
 
    public void produce() {
        int messageNo = 1;
        final int COUNT = 1000;
 
        while(messageNo < COUNT) {
            String key = String.valueOf(messageNo);
            String data = String.format("hello KafkaProducer message %s", key);
            System.out.println(data);
            
            try {
            	producer.send(new ProducerRecord<String, String>(TOPIC, data));
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
 
            messageNo++;
        }
        
        producer.close();
    }
 
}

