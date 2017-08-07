package exp.libs.warp.net.sock.io.common;

import java.net.Socket;

import exp.libs.warp.net.sock.bean.SocketBean;

public interface ISession {

	public String ID();
	
	public SocketBean getSocketBean();
	
	public Socket getSocket();
	
	public void write(String msg);
	
	public boolean conn();
	
	public String read();
	
	public void clearIOBuffer();
	
	public boolean isClosed();
	
	public boolean close();
	
}
