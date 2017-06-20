package chinesechess.client;

import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.swing.*;

import chinesechess.bean.QiZi;
import chinesechess.common.CommonInterface;
import chinesechess.utils.CommonUtils;

/**
 * 创建客户端
 * @author lisx
 * @2017年5月19日
 */
public class CreateClient extends JFrame implements CommonInterface,ActionListener, MouseListener{
	private static final long serialVersionUID = 1L;
	int x =200;int y=200; //x轴坐标，y轴坐标
	int width=500; int height=500; //窗口的宽度和高度
	JLabel zhujiming=new JLabel("主机名：");
	JLabel duankouhao=new JLabel("端口号：");
	JLabel nicheng=new JLabel("昵     称:");
    JTextField ip=new JTextField("127.0.0.1");//主机名值
	JTextField port=new JTextField("888");//端口号值
	JTextField alias=new JTextField("one");//昵称值
	JButton link=new JButton("连接[C]");
	JButton stop=new JButton("断开[T]");
	JComboBox<Object> jcombobox=new JComboBox<Object>(); //组合框jcombobox
	JButton tiaozhan=new JButton("挑战");
	JButton renshu=new JButton("认输");
	JPanel rightLayout=new JPanel();//用于存放布局右边的组件
	JPanel leftLayout;//用于存放布局左边的组件
//	JLabel image=new JLabel(new ImageIcon("WebRoot/webapp/image/beijing.png"));
	JFrame jf=new JFrame("中国象棋-客户端");
	Socket socket;
	DataInputStream dips;
	DataOutputStream dops;
	String requestType="link";//link表示连接服务端，close表示关闭客户端窗口，
	String camp="red";//该客户属于黑棋还是红棋，black黑棋，red红棋,默认是红方
	QiZi[][] qiziList=new QiZi[9][10];//存放32个棋子的属性，在对弈的时候才使用
	QiZi[][] redQiZiList=new QiZi[9][10];//保存黑方所有棋子的初始属性
	QiZi[][] blackQiZiList=new QiZi[9][10];//保存黑方所有棋子的初始属性
	QiPan qipan;
	int startX=-1, startY=-1, endX=-1, endY=-1;//玩家棋子移动开始与结束坐标
	QiZi noQiZi=new QiZi("该位置没有棋子", Color.white);//位置上没有棋子时，就用这个标志
	String getServerData;//得到服务器传过来的数据
	int caipan=0;//0表示该客户可以移动棋子，1表示该客户可以移动棋子，页面加载时该状态不能移动棋子所以初始值设置为零
	String enemyAlias;//对手的昵称
	String ownAlias;//客户自己的昵称
	
	
	
	
	@Override
	public void initializeWindow() {
		JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftLayout,rightLayout);
		jsp.setDividerLocation(342);//设置分割线的绝对位置
		jf.add(jsp);
		this.baseInitializeWindow(jf, x, y,width,height,"WebRoot/webapp/image/clientTouXiang.png",this.ownAlias);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void initializeState() {//将部分按钮置灰色
		this.tiaozhan.setEnabled(false);this.renshu.setEnabled(false);this.jcombobox.setEnabled(false);
		this.renshu.hide();//隐藏认输按钮，暂时不开发这个功能；
	}
	
	@Override
	public void addListener() {
		this.link.setMnemonic(KeyEvent.VK_C);//设置ALT+T组合键为断开按钮的热键
		this.stop.setMnemonic(KeyEvent.VK_T);//设置ALT+T组合键为断开按钮的热键
		this.link.addActionListener(this);//给连接按钮增加监听事件
		this.stop.addActionListener(this);//给断开按钮增加监听事件
		this.tiaozhan.addActionListener(this);//给挑战按钮增加监听事件
		this.leftLayout.addMouseListener(this);//给棋盘添加鼠标监听事件
	}

	@Override
	public void initializeComponent() {
		this.initializeLeftLayout();
		this.initializeRightLayout();
	}
	
	@Override
	public void start() {
		this.initializeComponent(); //初始化组件
		this.addListener(); //增加监听器
		this.initializeState(); //初始化状态
		this.initializeWindow(); //初始化窗口
	}
	
	public static void main(String[] args) {
		new CreateClient(550,550,580,500).start();
	}
	
	/**
   	 * 是用默认值
   	 * @param port 端口号
   	 * */
     public CreateClient(){
    	 this.initializeQiZiProperty();//初始化棋子的属性
    	 return;
     }  
	
    /**
     * 初始化窗口的宽度和高度
     * @param port 端口号
     * */
      public CreateClient(int width, int height){
    		this.width=width;this.height=height;
    		this.initializeQiZiProperty();//初始化棋子的属性
    	}  
 	
     
	/**
	 * 初始化窗口的x轴和y轴坐标及窗口的宽度和高度
	 * @param port 端口号
	 * */
     public CreateClient(int x,int y,int width,int height){
    	 this.initializeQiZiProperty();//初始化棋子的属性
    	 this.x=x;this.y=y;
 		this.width=width; this.height=height;
	}

	
	/**初始化棋子的属性*/
	public void initializeQiZiProperty(){
			//当前客户是红方
					//录入黑方的棋子属性
			redQiZiList[0][0]=new QiZi("車", Color.black);
			redQiZiList[1][0]=new QiZi("馬", Color.black);
			redQiZiList[2][0]=new QiZi("象", Color.black);
			redQiZiList[3][0]=new QiZi("士", Color.black);
			redQiZiList[4][0]=new QiZi("将", Color.black);
			redQiZiList[5][0]=new QiZi("士", Color.black);
			redQiZiList[6][0]=new QiZi("象", Color.black);
			redQiZiList[7][0]=new QiZi("馬", Color.black);
			redQiZiList[8][0]=new QiZi("車", Color.black);      
			redQiZiList[1][2]=new QiZi("炮", Color.black);
			redQiZiList[7][2]=new QiZi("炮", Color.black);
			redQiZiList[0][3]=new QiZi("卒", Color.black);
			redQiZiList[2][3]=new QiZi("卒", Color.black);
			redQiZiList[4][3]=new QiZi("卒", Color.black);
			redQiZiList[6][3]=new QiZi("卒", Color.black);
			redQiZiList[8][3]=new QiZi("卒", Color.black);
					//录入红方的棋子属性
			redQiZiList[0][9]=new QiZi("車",  Color.red);
			redQiZiList[1][9]=new QiZi("馬", Color.red);
			redQiZiList[2][9]=new QiZi("相", Color.red);
			redQiZiList[3][9]=new QiZi("仕", Color.red);
			redQiZiList[4][9]=new QiZi("帅", Color.red);
			redQiZiList[5][9]=new QiZi("仕", Color.red);
			redQiZiList[6][9]=new QiZi("相", Color.red);
			redQiZiList[7][9]=new QiZi("馬", Color.red);
			redQiZiList[8][9]=new QiZi("車", Color.red);
			redQiZiList[1][7]=new QiZi("炮", Color.red);
			redQiZiList[7][7]=new QiZi("炮", Color.red);
			redQiZiList[0][6]=new QiZi("兵", Color.red);
			redQiZiList[2][6]=new QiZi("兵", Color.red);
			redQiZiList[4][6]=new QiZi("兵", Color.red);
			redQiZiList[6][6]=new QiZi("兵", Color.red);
			redQiZiList[8][6]=new QiZi("兵", Color.red);
			//当前客户是黑方
					//录入红方的棋子属性                      
			blackQiZiList[0][0]=new QiZi("車", Color.red);      
			blackQiZiList[1][0]=new QiZi("馬", Color.red);      
			blackQiZiList[2][0]=new QiZi("相", Color.red);      
			blackQiZiList[3][0]=new QiZi("仕", Color.red);      
			blackQiZiList[4][0]=new QiZi("帅", Color.red);      
			blackQiZiList[5][0]=new QiZi("仕", Color.red);      
			blackQiZiList[6][0]=new QiZi("相", Color.red);      
			blackQiZiList[7][0]=new QiZi("馬", Color.red);      
			blackQiZiList[8][0]=new QiZi("車", Color.red);      
			blackQiZiList[1][2]=new QiZi("炮", Color.red);      
			blackQiZiList[7][2]=new QiZi("炮", Color.red);      
			blackQiZiList[0][3]=new QiZi("兵", Color.red);      
			blackQiZiList[2][3]=new QiZi("兵", Color.red);      
			blackQiZiList[4][3]=new QiZi("兵", Color.red);      
			blackQiZiList[6][3]=new QiZi("兵", Color.red);      
			blackQiZiList[8][3]=new QiZi("兵", Color.red);      
		                                             
					//录入黑方的棋子属性                       
			blackQiZiList[0][9]=new QiZi("車", Color.black);
			blackQiZiList[1][9]=new QiZi("馬", Color.black);
			blackQiZiList[2][9]=new QiZi("象", Color.black);
			blackQiZiList[3][9]=new QiZi("士", Color.black);
			blackQiZiList[4][9]=new QiZi("将", Color.black);
			blackQiZiList[5][9]=new QiZi("士", Color.black);
			blackQiZiList[6][9]=new QiZi("象", Color.black);
			blackQiZiList[7][9]=new QiZi("馬", Color.black);
			blackQiZiList[8][9]=new QiZi("車", Color.black);
			blackQiZiList[1][7]=new QiZi("炮", Color.black);
			blackQiZiList[7][7]=new QiZi("炮", Color.black);
			blackQiZiList[0][6]=new QiZi("卒", Color.black);
			blackQiZiList[2][6]=new QiZi("卒", Color.black);
			blackQiZiList[4][6]=new QiZi("卒", Color.black);
			blackQiZiList[6][6]=new QiZi("卒", Color.black);
			blackQiZiList[8][6]=new QiZi("卒", Color.black);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {//得到鼠标键值 
		if(this.caipan==0) return;//该客户没有资格移动棋子
		Hashtable<Character, Integer> hashtable=CommonUtils.getQiZiIndex(e.getX(), e.getY(), this.qipan);
		int x=hashtable.get('x');//玩家点击棋盘对应的棋子的x下标
		int y=hashtable.get('y');//玩家点击棋盘对应的棋子的y下标
		if(x==-2 || y==-2)  return;//玩家点击位置太偏，应该舍去本次操作
		if(startX==x && startY==y) return;//玩家连续点击了两次及以上次同一个棋子将，舍去多余的操作
		if(startX==-1 || startY==-1){//玩家第一次点击，只改变棋子状态
			try {
				if(this.qiziList[x][y] != null){//玩家必须点击的是棋子
					if(("red".equalsIgnoreCase(this.camp) && Color.red.equals(this.qiziList[x][y].getColor())) || ("black".equalsIgnoreCase(this.camp) && Color.black.equals(this.qiziList[x][y].getColor()))){//该客户只能移动自己的棋子，不能移动对方的棋子
						startX=x; startY=y;
						System.out.println("startX="+startX+"   startY="+startY);
					}
				}
			} catch (Exception e2) {//出现异常的鼠标点击事件不做处理，该操作无效

			}
		}else{//玩家第二次点击，这时才开始移动棋子
			endX=x;endY=y;
			try {
				dops.writeUTF("#shift_"+this.enemyAlias+"|"+startX+"|"+startY+"|"+endX+"|"+endY);//发送数据格式为#shift_enemyAlias|startX|startY|endX|endY
			} catch (IOException e1) {
				System.out.println("发送移动棋子的坐标信息失败！");
				e1.printStackTrace();
				return;//玩游戏中数据交流异常，将取消本次异常操作
			}
			this.qiziList[endX][endY]=this.qiziList[startX][startY];
			this.qiziList[startX][startY]=null;//this.qiziList[startX][startY]=null这是错的
			startX=-1;startY=-1;//还原棋子的开始坐标
			System.out.println("endX="+endX+"  endY="+endY);
			this.caipan=0;//玩家移动一次棋子后该让对手移动棋子，此时玩家不具备移动棋子的资格
		}
		qipan.repaint();//刷新绘制的图形
	}

	@Override
	public void mousePressed(MouseEvent e) {//鼠标被按下  
	}

	@Override
	public void mouseReleased(MouseEvent e) {//鼠标被放开
	}

	@Override
	public void mouseEntered(MouseEvent e) { //鼠标进入组件 
	}

	
	/**连接服务器
	 * @param socket2
	 * @param zhujiming   
	 * @param port
	 * */
	private void linkServer( JTextField port,JLabel zhujiming) throws UnknownHostException{
		if(ip.getText()!=null && !"".equals(ip.getText()) && alias.getText() !=null && !"".equals(alias.getText()) && CommonUtils.validatePort(port.getText())){//主机名不为空、端口号或者昵称为正确录入
			try {
				this.ownAlias=this.alias.getText();//保存登陆时的昵称
				socket=new Socket(ip.getText(),Integer.parseInt(port.getText()));//创建客户端
				ClientAgentThread cat=new ClientAgentThread(this);
				Thread thread=new Thread(cat); 
				thread.start();
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else{
			CommonUtils.promptWindows();//弹出提示框
		}
	}
	
	/**初始化左边布局*/
	private void initializeLeftLayout(){
		QiPan qipan=new QiPan(this);
		leftLayout=qipan;
	}
	
	/**初始化右边布局*/
	public void initializeRightLayout(){
		rightLayout.setLayout(null);
//		rightLayout.setBounds(342, this.y, 215, this.y);
		zhujiming.setBounds(40, 10, 90, 20);ip.setBounds(110, 10, 90, 20);//主机名
		duankouhao.setBounds(40, 40, 90, 20);port.setBounds(110, 40, 90, 20);//端口号
		nicheng.setBounds(40, 70, 90, 20);alias.setBounds(110, 70, 90, 20);//昵称
		link.setBounds(10, 100, 90, 20);stop.setBounds(110, 100, 90, 20);
		jcombobox.setBounds(10, 130, 190, 20);//下拉框
		tiaozhan.setBounds(10, 160, 90, 20);renshu.setBounds(110, 160, 90, 20);
		
		rightLayout.add(zhujiming);rightLayout.add(ip); //主机名
		rightLayout.add(duankouhao);rightLayout.add(port);//端口号
		rightLayout.add(nicheng);rightLayout.add(alias);//昵称
		rightLayout.add(link);rightLayout.add(stop);
		rightLayout.add(jcombobox);//下拉框
		rightLayout.add(tiaozhan);rightLayout.add(renshu);
	}
	

	
	
	/**断开按钮增加监听事件
	 * @throws Throwable */
	private void stopAddListener() throws Throwable {
		dops.writeUTF("#exitLink_"+this.ownAlias);//先关闭服务器的套接字，向服务器发送退出连接的信息
	}
	/**给挑战按钮增加监听事件*/
	private void tiaozhan(){
		try {
			this.dops.writeUTF("#tiaozhan_"+this.alias.getText()+"_"+this.jcombobox.getSelectedItem());//发送的数据格式为#tiaozhan_sendChallengeAlias_getChallengeAlias
			System.out.println(this.alias.getText()+"向"+this.jcombobox.getSelectedItem()+"发送挑战的信息为："+"#tiaozhan_"+this.alias.getText()+"_"+this.jcombobox.getSelectedItem());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.link){//给连接按钮增加监听事件
			try {
				this.linkServer(this.port,this.zhujiming);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		}else if(e.getSource()==this.stop){//给断开按钮增加监听事件
			try {
				this.stopAddListener();
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}
		else if(e.getSource()==this.tiaozhan) this.tiaozhan() ; //给挑战按钮增加监听事件
	}
	
	
	@Override
	public void mouseExited(MouseEvent e) { //鼠标离开组件  
	}
	
	/**窗口的基本一般初始
	 * @param x|窗口的轴位置    y|窗口的轴位置
     */
	public void baseInitializeWindow(JFrame jf,int x,int y,int width,int height,String touxiangUrl,final String ownAlias){
		jf.setBounds(x, y, width, height);
		jf.setIconImage(new ImageIcon(touxiangUrl).getImage());
	
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {//先关闭服务器的套接字，向服务器发送退出连接的信息
				try {
					dops.writeUTF("#exitLinkWindows_"+ownAlias);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println("服务器窗口已经关闭！");
			}

			@Override
			public void windowOpened(WindowEvent e) {
				System.out.println("窗口正常启动！");
			}
		});
		jf.setVisible(true);
		jf.setAlwaysOnTop(true);//设置窗口置顶
	}
	
}
