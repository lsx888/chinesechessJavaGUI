package chinesechess.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.text.html.Option;

import chinesechess.bean.SocketBaseInfo;
import chinesechess.common.CommonInterface;
import chinesechess.common.CommonMethod;
import chinesechess.utils.CommonUtils;

/**
 * 创建服务器
 * @author lisx
 * @2017年5月16日
 */
@SuppressWarnings("all")
public class CreateServer extends Frame implements CommonInterface,ActionListener{
	JButton jbStart=new JButton("启动[Q]");
	JButton jbStop =new JButton("关闭[T]");
	JLabel  jlport =new JLabel("端口号:");
	JTextField jtport=new JTextField();
	JPanel jp=new JPanel();
	JPanel jpTest=new JPanel();
	JLabel jlTest=new JLabel(new ImageIcon("WebRoot/webapp/image/beijing.png"));
	JFrame jf=new JFrame("中国象棋-服务器");
    String port;//端口号
	int x=200;int y=200;//窗口初始位置的x轴与y轴位置
	int width=500; int height=500;//窗口的宽度和高度
	ServerSocket ss;
	Vector<String> vector=new Vector<String>();
	Hashtable<String,SocketBaseInfo> socketBaseInfo=new Hashtable<String,SocketBaseInfo>();
	
 	/**
  	 * 初始化函数,是用默认值
  	 * @param port 端口号
  	 * */
       public CreateServer(){
    	   this.initializePort(port);//初始化端口号
  	} 
     
     
 	/**
 	 * 初始化端口
 	 * @param port 端口号
     */
    public CreateServer(String port){
    	this.initializePort(port);//初始化端口号
 	} 
      
  	/**
   	 * 初始化函数端口及x、y轴的坐标
   	 * @param port 端口号
   	 * */
     public CreateServer(String port,int width, int height){
    	 this.initializePort(port);//初始化端口号
   		this.width=width;this.height=height;
   	}  
     
     /**
 	 * 初始化端口，x，y轴坐标及窗口的宽度和高度
 	 * @param port 端口号
 	 * */
      public CreateServer(String port,int x,int y,int width,int height){
 		this.initializePort(port);//初始化端口号
 		this.x=x;this.y=y;
 		this.height=height; this.width=0;
 	}
 	
	/**初始化端口号并向系统注册*/
	private void initializePort(String port){
		if(CommonUtils.validatePort(port) == false)  System.exit(0); //输入不合法的端口，将终止代码执行
		this.port =port;
		try {
			this.ss=new ServerSocket(Integer.parseInt(port),10);//最大队列这里设置为2，系统默认为50
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**开始运行服务器*/
	public void start(){
		this.initializeComponent();
		this.addListener();
		this.initializeState();
		this.initializeWindow();
	}

	@Override
	public void initializeComponent() {
		jp.setLayout(null); 
		//初始化窗口右边
		jlport.setBounds(25,200,45,20); jtport.setBounds(75, 200, 60, 20); jtport.setText(port);
		jp.add(jlport);  jp.add(jtport);	
		jbStart.setBounds(0,230, 80, 20); jbStop.setBounds(85, 230, 80, 20);
		jp.add(jbStart); jp.add(jbStop);	
		//初始化窗口左边
		jpTest.add(jlTest);
		JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jpTest,jp);
		jf.add(jsp);
	}

	@Override
	public void addListener() {
		this.jbStart.setMnemonic(KeyEvent.VK_Q);//按ALT+Q组合建，启动服务器
		this.jbStop.setMnemonic(KeyEvent.VK_T);//按ALT+T组合建，停止服务器
		this.jbStart.addActionListener(this);//启动服务器
		this.jbStop.addActionListener(this);//关闭服务器
	}

	@Override
	public void initializeState() {
		jtport.setEditable(false);
	}

	@Override
	public void initializeWindow(){
		//初始化窗口
		CommonMethod.baseInitializeWindow(jf,x,y,width,height,"WebRoot/webapp/image/serverTouXiang.jpg");
	}
	
	/**给启动按钮增加监听事件*/
	private void startAddListener(){
		if(CommonUtils.validatePort(jtport.getText())){//端口号验证成功
			WaitClientLinkThread wclt=new WaitClientLinkThread(this);
			Thread th=new Thread(wclt);
			th.start();
			System.out.println("服务器已经启动！");
			this.jbStart.setEnabled(false);//启动按钮设置为不可用
		}else{
			Object [] option={"关闭窗口！"};
			int flag=JOptionPane.showOptionDialog(null,
												"服务器启动失败，请检测端口号！", 
												"确认框", JOptionPane.YES_NO_OPTION, 
												JOptionPane.QUESTION_MESSAGE,
												null, 
												option, 
												option[0]);
			if(flag==0){
				System.out.println(0);
			}
			System.exit(0);	
		};
	} 
	
	/**给关闭按钮增加监听事件*/
	private void stopAddListener(){
		//鼠标点击事件事件
		System.out.println("服务器关闭！");
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	} 
	
	
	public static void main(String[] args) {
		new CreateServer("888", 550, 500).start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.jbStart)		this.startAddListener();//启动按钮增加事件
		else if(e.getSource()==this.jbStop)	this.stopAddListener();//停止按钮增加事件
	}

}


