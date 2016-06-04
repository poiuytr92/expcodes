package service.server;

import java.net.InetSocketAddress;

import org.apache.thrift.TProcessor; 
import org.apache.thrift.protocol.TBinaryProtocol; 
import org.apache.thrift.protocol.TBinaryProtocol.Factory; 
import org.apache.thrift.server.TServer; 
import org.apache.thrift.server.TThreadPoolServer; 
import org.apache.thrift.server.TThreadPoolServer.Args;  
import org.apache.thrift.transport.TServerSocket; 
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException; 
import org.apache.thrift.transport.TTransportFactory;

import service.demo.Hello; 
import service.demo.HelloServiceImpl; 

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2015-12-28 下午02:51:31 
 * 类说明 
 */
public class HelloServiceServer {

	/** 
     * 启动 Thrift 服务器
     * @param args 
     */ 
    public static void main(String[] args) { 
        try { 
        	Factory proFactory = new TBinaryProtocol.Factory(); 
        	Hello.Processor processor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl()); 
        	TServerTransport serverTransport = new TServerSocket(new InetSocketAddress("172.168.8.36", 9813));  
        	Args trArgs = new Args(serverTransport);  
        	trArgs.processor(processor);
        	trArgs.protocolFactory(proFactory); 
        	TServer server = new TThreadPoolServer(trArgs);  
        	System.out.println("HelloTThreadPoolServer start....");  
        	server.serve();  
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    }
}
