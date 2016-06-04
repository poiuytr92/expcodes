package service.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import service.demo.Hello; 

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2015-12-30 上午09:06:16 
 * 类说明 
 */
public class HelloNonblockingClient {

	public static final String SERVER_IP = "172.168.8.36";
	public static final int SERVER_PORT = 9812;
	public static final int TIMEOUT = 30000;

	public static void main(String[] args) {
		try {
			//设置传输通道，对于非阻塞服务，需要使用TFramedTransport，它将数据分块发送  
			TTransport transport = new TFramedTransport(new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT));
			//协议要和服务端一致
			//HelloTNonblockingServer
			//使用高密度二进制协议 
			TProtocol protocol = new TCompactProtocol(transport);
			//使用二进制协议 
			//TProtocol protocol = new TBinaryProtocol(transport);
			Hello.Client client = new Hello.Client(protocol);
			transport.open();
			String result = client.helloString("jack");
			System.out.println("result : " + result);
			//关闭资源
			transport.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
