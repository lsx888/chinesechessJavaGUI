package chinesechess.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * 客户端代理线程
 * @author lisx
 * @2017年5月30日
 */
public class ClientAgentThread implements Runnable{
	CreateClient father;
	Socket socket;
	DataInputStream dips;
	DataOutputStream dops;
	String getServerData;//得到服务器传过来的数据
	String alias; //昵称
	boolean exitLink=true;//false表示客户端退出与服务器的连接,开始默认为true
	@Override
	public void run() {
		while(this.exitLink){
			try {
				getServerData= dips.readUTF().trim(); //得到客户发送的数据
				father.getServerData=getServerData;
				System.out.println(getServerData);
				if(getServerData.startsWith("#link_"+alias)) this.clinkServer(getServerData);//客户连接服务器时需要验证昵称是否重名数据格式：#link_onezzz_zhengchang
				if(getServerData.startsWith("#tiaozhan")) this.getChallengaInfo(getServerData);//收到挑战信息时并得到的数据格式为#tiaozhan_sendChallengeAlias_getChallengeAlias
				if(getServerData.startsWith("#refresh")) this.refreshList(getServerData);//刷新下拉列表得到的数据格式为#refresh_|aaa|bbb
				if(getServerData.startsWith("#agree")) this.agreeChallengaInfo(getServerData);////同意后收到的数据格式#agree_sendChallengeAlias_getChallengeAlias
				if(getServerData.startsWith("#shift")) this.floatingQiZiInfo(getServerData); //玩家对弈时相互传输移动棋子的数据，数据格式为#shift_enemyAlias|startX|startY|endX|endY
				if(getServerData.startsWith("#exitLink")) this.playerExitGame(getServerData);//玩家退出与客户端的连接,数据格式为#exitLink_alias
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	/**服务器那边已经关闭了套接字了，现在可以关闭窗口了*/
	private void playerExitGame(String getServerData){
		this.exitLink=false;
		try {//关闭流socket
			if(dops != null) dops.close();
			if(dips != null)  dips.close();
			if(socket != null) socket.close();
		} catch (IOException e) {
			System.out.println("关闭套接字出现异常！");
			e.printStackTrace();
		}finally{
			System.out.println("客户端已退出！");
			System.exit(0);
		}
	}
	/**玩家在对弈时传输移动棋子的数据*/
	private void floatingQiZiInfo(String getServerData){
		//得到对手移动棋子的开始坐标和结束坐标
		String startX=getServerData.split("\\|")[1];
		String startY=getServerData.split("\\|")[2];
		String endX=getServerData.split("\\|")[3];
		String endY=getServerData.split("\\|")[4];
		//对手下完棋后更新我的棋盘上的棋子的坐标
		int sX=8-Integer.parseInt(startX);//开始位置的X坐标
		int sY=9-Integer.parseInt(startY);//开始位置的Y坐标
		int eX=8-Integer.parseInt(endX);//结束位置的X坐标
		int eY=9-Integer.parseInt(endY);//结束位置的Y坐标
		//刷新棋盘
		father.qiziList[eX][eY]=father.qiziList[sX][sY];
		father.qiziList[sX][sY]=null;
		father.qipan.repaint();
		father.caipan=1;//对手走了一步棋，现在我又资格可以下棋了；
	}
	
	/**发送挑战信息收到对方同意挑战*/
	private void agreeChallengaInfo(String getServerData){
		father.stop.setEnabled(false); this.father.tiaozhan.setEnabled(false);
		father.renshu.setEnabled(true);
		father.camp="black";//发送挑战信息者为黑方
		father.qipan.repaint();//重新绘画棋盘
		father.enemyAlias=getServerData.split("_")[2];//得到对手的名称
	}
	
	
	/**刷新下拉列表*/
	private void refreshList(String serverDate){
		father.jcombobox.removeAllItems();//清除以前的数据，father.jcombobox.removeAll这个方法无效
		String[] list=serverDate.split("\\|");
		int length=list.length;
		for (int i = 1; i < length; i++) {//第一个不是用户昵称，所以不用录入
			father.jcombobox.addItem((Object)list[i]);
		}
		//正常时，需要设置各个组件的状态
		father.link.setEnabled(false);father.ip.setEnabled(false);father.port.setEnabled(false);father.alias.setEnabled(false);
		father.jcombobox.setEnabled(true);
		if("无其他用户登录！".equals(list[1])) father.tiaozhan.setEnabled(false);//只有一个玩家登陆时，该玩家不能够使用挑战功能
		else father.tiaozhan.setEnabled(true);//有两个及以上的玩家登陆时，该玩家才能使用挑战功能
	}
	
	/**连接到服务器*/
	private void clinkServer(String serverData) throws Throwable{  
		String getAliasFlag=serverData.split("_")[1];//得到是否重名的标志
		if("chongming".equals(getAliasFlag)){//该昵称是重名状态，已经被使用
			Object [] option={"关闭窗口！"};
			JOptionPane.showOptionDialog(null,
										"昵称已经被他人使用！", 
										"确认框", JOptionPane.YES_NO_OPTION, 
										JOptionPane.QUESTION_MESSAGE,
										null, 
										option, 
										option[0]);
		}
	}
	
	/**被挑战者收到挑战信息*/
	private void getChallengaInfo(String serverData){
		String sendChallengeAlias=serverData.split("_")[1];//发起挑战者的昵称
		String getChallengeAlias=serverData.split("_")[2];//被挑战者的昵称
		System.out.println(getChallengeAlias+"收到"+sendChallengeAlias+"发出的信息为："+serverData);
		Object [] option={"同意","拒绝"};
		int flag=JOptionPane.showOptionDialog(null,
									sendChallengeAlias+"向你发起挑战", 
									"确认框", JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE,
									null, 
									option, 
									option[0]);
		if(flag==0){//你选择了同意
			father.tiaozhan.setEnabled(false);father.stop.setEnabled(false);
			father.renshu.setEnabled(true);
			System.out.println(getChallengeAlias+"接受了"+sendChallengeAlias+"的挑战！");
			father.getServerData=this.getServerData;//为后面传输数据做准备
			father.caipan=1;//同意挑战后该客户就可以移动棋子了
			father.enemyAlias=sendChallengeAlias;//得到对手的昵称
			try {
				dops.writeUTF("#agree_"+sendChallengeAlias+"_"+getChallengeAlias);//#agree_sendChallengeAlias_getChallengeAlias
			} catch (IOException e) {
				System.out.println("同意挑战时发送信息失败");
				e.printStackTrace();
			}
		}else if(flag==1){//你选择了拒绝挑战
			System.out.println(getChallengeAlias+"拒绝了"+sendChallengeAlias+"的挑战！");
		}
	}
	
	
	/**初始化构造函数*/
	public ClientAgentThread(CreateClient father){
		this.father=father; 
		this.socket=father.socket;
		try {
			father.dips=new DataInputStream(socket.getInputStream());
			father.dops=new DataOutputStream(socket.getOutputStream());
			this.dips=father.dips;
			this.dops=father.dops;
			this.alias=this.father.alias.getText();
			this.dops.writeUTF("#link_"+alias);//将名称传给服务器，验证该昵称是否已经被使用了
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
