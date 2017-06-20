package chinesechess.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import chinesechess.client.QiPan;

/**
 * 一般工具类
 * @author lisx
 * @2017年5月20日
 */
public class CommonUtils{
	private static Hashtable<Character, Integer> qiZiIndex = new Hashtable<Character, Integer>();

	/**端口号的验证*/
	public static boolean validatePort( String port){
		int iPort = -1;
		try {
			iPort=Integer.parseInt(port);
			if (iPort<1 || iPort>9999) {
				System.out.println("请输入一个1到9999的正整数");
			}else if(iPort>=1 && iPort<=8000 ){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("输入的端口号必须是整数");
		}
		return false;
	}
	
	/**弹出提示框窗口*/
	public static void promptWindows(){
		Object [] option={"关闭窗口！"};
		JOptionPane.showOptionDialog(null,
									"录入数据可能错在明显错误，请仔细检查！", 
									"确认框", JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE,
									null, 
									option, 
									option[0]);
	
	}
	
   
   /**
    * 将玩家点击的坐标转化为数组的下表
    * @return value代表棋子数组的坐标
    * */
   public static Hashtable<Character, Integer> getQiZiIndex(int x,int y,QiPan qipan){
	   x=x-qipan.leftSize;
	   y=y-qipan.topSize;
	   int xIndex,yIndex;//将返回的值数组坐标保存在这两个参数里面
	   if(x%qipan.lineLength<5) xIndex=(int)(x/qipan.lineLength);//四舍
	   else xIndex=(int)((x)/qipan.lineLength)+1;//五入
	   if(y%qipan.lineLength<5) yIndex=(int)(y/qipan.lineLength);//四舍
	   else yIndex=(int)(y/qipan.lineLength)+1;//五入
	   
	   //录入x坐标
	   if(xIndex==0 && x<0 && yIndex<=9 && yIndex>=0)  qiZiIndex.put('x', 0);//点击左边线外面的棋子，则x坐标为零
	   else if(xIndex==8 && x>qipan.lineLength*8 && yIndex<=9 && yIndex>=0)  qiZiIndex.put('x', 8);//点击右边线外面的棋子，则x坐标为零
	   else if(x>=0 && x<=qipan.lineLength*8 && yIndex<=9 && yIndex>=0) qiZiIndex.put('x',xIndex);//,点击的是棋盘内，必须将转化为int，因为转化出来有可能会是小数
	   else qiZiIndex.put('x', -2);//点击棋盘外，记为负二;
	   //录入y坐标
	   if(yIndex==0 && y<0 && xIndex<=8 && xIndex>=0)  qiZiIndex.put('y', 0);//点击左边线外面的棋子，则x坐标为零
	   else if(yIndex==9 && y>qipan.lineLength*9 && xIndex<=8 &&  xIndex>=0)  qiZiIndex.put('y', 9);//点击右边线外面的棋子，则x坐标为零
	   else if(y>=0 && y<=qipan.lineLength*9 && xIndex<=8 && xIndex>=0) qiZiIndex.put('y', yIndex);//,点击的是棋盘内，必须将转化为int，因为转化出来有可能会是小数
	   else qiZiIndex.put('y', -2);//点击棋盘外，记为负二;
	   return qiZiIndex;
   }
   
   /**
    * 客户退出与客服端的连接
    * @param socket：套接字， dips:输出流，dops:输出流
    * */
   public static void exitLink(Socket socket, DataInputStream dips, DataOutputStream dops){
	   
		try {
			if(dops != null) dops.close();
			if(dips != null)  dips.close();
			if(socket != null) socket.close();
		} catch (IOException e) {
			System.out.println("关闭套接字出现异常！");
			e.printStackTrace();
		}
   }
   
}
