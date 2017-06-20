package chinesechess.common;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * 公共方法
 * @author lisx
 * @2017年5月16日
 */
public class CommonMethod {
	
	/**窗口的基本一般初始
	 * @param x|窗口的轴位置    y|窗口的轴位置
     */
	public static void baseInitializeWindow(JFrame jf,int x,int y,int width,int height,String touxiangUrl){
		jf.setBounds(x, y, width, height);
		jf.setIconImage(new ImageIcon(touxiangUrl).getImage());
	
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
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
