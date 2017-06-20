package chinesechess.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 等待客户端连接线程
 * @author lisx
 * @2017年5月22日
 */
public class WaitClientLinkThread implements Runnable {
	CreateServer father;
	ServerSocket ss;
	Socket socket;
	@Override
	public void run() {
		while (true) {
			try {
				socket=ss.accept();//等待客户端接入
				serverAgentThread lc=new serverAgentThread(this);
				Thread th=new Thread(lc);
				th.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**构造函数*/
	public WaitClientLinkThread(CreateServer father){
		this.father=father;
		this.ss=father.ss;
	}
}
