package chinesechess.bean;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Socket的基本信息
 * @author lisx
 * @2017年6月5日
 */
public class SocketBaseInfo {
	private Socket socket;//保存socket
	private DataInputStream din;//数据输出流
	private DataOutputStream dops;//数据输入流
	
	public DataInputStream getDin() {
		return din;
	}
	public void setDin(DataInputStream din) {
		this.din = din;
	}
	public DataOutputStream getDops() {
		return dops;
	}
	public void setDops(DataOutputStream dops) {
		this.dops = dops;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
