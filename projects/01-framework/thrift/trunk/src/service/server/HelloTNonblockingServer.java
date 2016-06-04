package service.server;

import java.net.InetSocketAddress;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import service.demo.Hello; 
import service.demo.HelloServiceImpl; 

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2015-12-30 上午09:01:27 
 * 类说明 
 */
public class HelloTNonblockingServer {

	public static void main(String[] args) {
		try { 
			//处理器
			TProcessor tprocessor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
			//传输通道 - 非阻塞方式  
			TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(new InetSocketAddress("172.168.8.36", 9812));
			//异步IO，需要使用TFramedTransport，它将分块缓存读取。  
			TNonblockingServer.Args tArgs = new TNonblockingServer.Args(serverTransport);
			tArgs.processor(tprocessor);
			tArgs.transportFactory(new TFramedTransport.Factory());
			//使用高密度二进制协议 
			tArgs.protocolFactory(new TCompactProtocol.Factory());
			//使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
			TServer server = new TNonblockingServer(tArgs);
			System.out.println("HelloTNonblockingServer start....");
			server.serve(); // 启动服务
		} catch (Exception e) { 
            e.printStackTrace(); 
        } 
	}
}
