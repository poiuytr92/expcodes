package service.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <br/><B>PROJECT : </B> thrift
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2015-12-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ThriftPoolableObjectFactory implements PoolableObjectFactory {
	
	/** 日志记录器 */
    public static final Logger logger = LoggerFactory.getLogger(ThriftPoolableObjectFactory.class);
    /** 服务的IP */
    private String serviceIP;
    /** 服务的端口 */
    private int servicePort;
    /** 超时设置 */
    private int timeOut;
    
    public ThriftPoolableObjectFactory(String serviceIP, int servicePort, int timeOut) {
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        this.timeOut = timeOut;
    }

    //销毁对象，销毁对象池时被调用，连接池调用invalidateObject(obj);时被调用。
	public void destroyObject(Object arg0) throws Exception {
		if (arg0 instanceof TSocket) {
            TSocket socket = (TSocket) arg0;
            if (socket.isOpen()) {
                socket.close();
            }
        }
	}

	//创建对象实例，用于填充对象池。同时可以分配这个对象适用的资源。  
	public Object makeObject() throws Exception {
		try {
            TTransport transport = new TSocket(this.serviceIP, this.servicePort, this.timeOut);
            transport.open();
            return transport;
        }catch(Exception e) {
            logger.error("error ThriftPoolableObjectFactory()", e);
            throw new RuntimeException(e);
        }
	}
	
	//查询对象有效性，需要对象池设置setTestOnBorrow(true)  
	public boolean validateObject(Object arg0) {
		try {
            if(arg0 instanceof TSocket) {
                TSocket thriftSocket = (TSocket)arg0;
                if(thriftSocket.isOpen()) {
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }catch(Exception e) {
            return false;
        }
	}
	
	//激活一个对象，从对象池获取对象时被调用。  
	public void activateObject(Object arg0) throws Exception {
		 //DO NOTHING
	}

	//挂起一个对象，将对象还给对象池时被调用。  
	public void passivateObject(Object arg0) throws Exception {
        //DO NOTHING
	}
	
	public String getServiceIP() {
        return serviceIP;
    }
	
    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }
    
    public int getServicePort() {
        return servicePort;
    }
    
    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }
    
    public int getTimeOut() {
        return timeOut;
    }
    
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }


} 
