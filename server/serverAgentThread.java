package chinesechess.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import chinesechess.bean.SocketBaseInfo;

/**
 * 服务器代理线程
 * @author lisx
 * @2017年5月22日
 */
public class serverAgentThread implements Runnable{
	WaitClientLinkThread father;
	Socket socket;
	DataInputStream dips;
	DataOutputStream dops;
	Vector<String> vector;
	Hashtable<String,SocketBaseInfo> socketBaseInfo;
	boolean exitLink=true;//false表示客户端退出与服务器的连接,开始默认为true
	@Override
	public void run() {
		while(exitLink){
			try {
				String getClientData= dips.readUTF().trim(); //得到客户发送的数据
				if(getClientData.startsWith("#link")) this.questionConn(getClientData);//客户端请求连接服务器，getClientData值格式#link_alias
				if(getClientData.startsWith("#tiaozhan")) this.getChallengeInfo(getClientData);//收到客户端请求挑战其他用户，getClientData值格式#tiaozhan_sendChallengeAlias_getChallengeAlias
				if(getClientData.startsWith("#agree")) this.agreeChallenge(getClientData);//收到客户端请求挑战其他用户，getClientData值格式#tiaozhan_sendChallengeAlias_getChallengeAlias
				if(getClientData.startsWith("#shift")) this.sendFloatingInf(getClientData);//玩家对弈期间相互传输数据，数据格式为#shift_enemyAlias|startX|startY|endX|endY
				if(getClientData.startsWith("#exitLink")) this.playerExitGame(getClientData);//玩家退出与客户端的连接,数据格式为#exitLink_alias
				if(getClientData.startsWith("#exitLinkWindows")) this.playerExitGameWindows(getClientData);//玩家点击窗口上的叉直接关掉与客户端的连接,数据格式为#exitLinkWindows__alias
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	/**玩家点击窗口上的叉来直接退出窗口*/
	private void playerExitGameWindows(String getClientData){
		String alias=getClientData.split("_")[1];//得到退出客户的姓名
		try {//关闭流socket
			socketBaseInfo.get(alias).getDops().writeUTF(getClientData);//通知客户端可以关闭了
			if(socketBaseInfo.get(alias).getDops() != null) socketBaseInfo.get(alias).getDops().close();
			if(socketBaseInfo.get(alias).getDin() != null)  socketBaseInfo.get(alias).getDin().close();
			if(socketBaseInfo.get(alias).getSocket() != null) socketBaseInfo.get(alias).getSocket().close();
			if(vector.size()==1){//如果退出得客户是最后一个客户，还需要关闭当前的socket字节
				this.dips.close();this.dops.close(); this.socket.close();
			}
			
		} catch (IOException e) {
			System.out.println("关闭套接字出现异常！");
			e.printStackTrace();
		}finally{
			this.exitLink=false;//使套接字不在接收数据
			socketBaseInfo.remove(alias);//移除该客户的信息
			for(int x=0;x<vector.size();x++) if(alias.equals(vector.get(x))) vector.remove(x);//移除vector里面的信息
		}
	}
	
	/**收到玩家请求退出服务器的请求*/
	private void playerExitGame(String getClientData){
		String alias=getClientData.split("_")[1];//得到退出客户的姓名
		try {//关闭流socket
			socketBaseInfo.get(alias).getDops().writeUTF(getClientData);//通知客户端可以关闭了
			if(socketBaseInfo.get(alias).getDops() != null) socketBaseInfo.get(alias).getDops().close();
			if(socketBaseInfo.get(alias).getDin() != null)  socketBaseInfo.get(alias).getDin().close();
			if(socketBaseInfo.get(alias).getSocket() != null) socketBaseInfo.get(alias).getSocket().close();
			if(vector.size()==1){//如果退出得客户是最后一个客户，还需要关闭当前的socket字节
				this.dips.close();this.dops.close(); this.socket.close();
			}
			
		} catch (IOException e) {
			System.out.println("关闭套接字出现异常！");
			e.printStackTrace();
		}finally{
			this.exitLink=false;//使套接字不在接收数据
			socketBaseInfo.remove(alias);//移除该客户的信息
			for(int x=0;x<vector.size();x++) if(alias.equals(vector.get(x))) vector.remove(x);//移除vector里面的信息
			if(vector.size()>=1) this.refreshAllClientList(alias,"exit");//不是最后一个客户推出时，需要给所有客户刷新的下拉列表值
			System.out.println(alias+"客户已退出！");
		}
	}	
	
	/**玩家对弈期间，发送移动棋子的信息，开始坐标为[startX,startY],结束坐标为[endX][endY]*/
	private void sendFloatingInf(String getClientData){
		String alias=getClientData.split("\\|")[0].split("_")[1];//得到对手的昵称
		try {
			socketBaseInfo.get(alias).getDops().writeUTF(getClientData);//向对手发送数据
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**玩家同意了对方的挑战请求*/
	private void agreeChallenge(String getClientData){
		String alias=getClientData.split("_")[1];
		try {
			socketBaseInfo.get(alias).getDops().writeUTF(getClientData);//向发起挑战者发送数据
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**新的客户登录成功后所有已经在线的客户都要刷新在线列表值*/
	private void refreshAllClientList(String alias, String flag){
		int size=vector.size();
		for (int i = 0; i < size; i++) {//
			String clientAlais=vector.get(i);//得到一个客户昵称
			StringBuilder spinnerValue=getSpinnerValue(clientAlais);//得到这个客户的下拉列表值
			if(size==1){//只有一个用户在线时
				try {
					this.socketBaseInfo.get(clientAlais).getDops().writeUTF("#refresh_"+"|无其他用户登录！");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else if(size >1){//不止一个用户在线时
				if("exit".equals(flag)) continue;//假如客户是在退出时，就不用给自己发送列表数据了
				else if("link".equals(flag)){//如果客户是在登录时，就需要给机子发送下拉列表的值
					try {
						this.socketBaseInfo.get(clientAlais).getDops().writeUTF("#refresh_"+spinnerValue);
					} catch (IOException e) {
						System.out.println("刷新下拉列表失败!");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**以StringBuilder的格式得到下拉列表的值*/
	private StringBuilder getSpinnerValue(String alias){
		StringBuilder vectoryIntoString=new StringBuilder();
		int size=vector.size();
		for(int i=0;i<size;i++){
			if(alias.equals(vector.get(i).toString())) continue;//不添加自己的昵称
			vectoryIntoString.append("|"+vector.get(i).toString());
		}
		return vectoryIntoString;
	}
	
	/**收到客户端请求挑战其他用户*/
	private void getChallengeInfo(String getClientData){
		String getEnemyAlais=getClientData.split("_")[2];//得到对手的昵称
		System.out.println(getClientData+"---"+getEnemyAlais);
		try {
			this.socketBaseInfo.get(getEnemyAlais).getDops().writeUTF(getClientData);
		} catch (IOException e) {
			System.out.println("向被挑战则发送挑战信息失败，请检查网络连接是否正常！");
			e.printStackTrace();
		}
	}
	
	/**客户端请求连接服务器
	 * @throws Throwable */
	private void questionConn(String getClientData) throws Throwable{
		String alias=getClientData.split("_")[1];
		int size=vector.size();
		SocketBaseInfo socketBaseInfo=new SocketBaseInfo();
		socketBaseInfo.setSocket(this.socket);
		socketBaseInfo.setDin(new DataInputStream(this.socket.getInputStream()));
		socketBaseInfo.setDops(new DataOutputStream(this.socket.getOutputStream()));
		System.out.println("昵称为"+alias+"的客户登录了系统");
		
		if(size==0){//该客户是第一个登录服务器的话，不必验证是否重名
			dops.writeUTF(getClientData+"_zhengchang");
			this.vector.add(alias);//保存昵称 
			this.socketBaseInfo.put(alias, socketBaseInfo);//保存socket
			this.refreshAllClientList(alias,"link");//给所有客户刷新的下拉列表值
			System.out.println("当前只有"+alias+"在线！");
			return;
		}
		
		if(vector.contains(alias)){//重名
			dops.writeUTF(getClientData+"_chongming");
			dips.close();dops.close();
			System.out.println(alias+"----因为重名而无法连接服务器！");
			return;
		}
		dops.writeUTF(getClientData+"_zhengchang");
		this.vector.add(alias);
		this.socketBaseInfo.put(alias, socketBaseInfo);//保存socket
		this.refreshAllClientList(alias,"link");//给所有客户刷新的下拉列表值
	}

	
	/**构造函数*/
	public serverAgentThread(WaitClientLinkThread father){
		this.father=father;
		this.socket=father.socket;
		this.vector=father.father.vector;
		this.socketBaseInfo=father.father.socketBaseInfo;
		try {
			dips=new DataInputStream(father.socket.getInputStream());
			dops=new DataOutputStream(father.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
