package chinesechess.client;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.plaf.FontUIResource;

/**
 * 棋盘类
 * @author lisx
 * @2017年6月11日
 */
public class QiPan extends JPanel{
	private static final long serialVersionUID = 1L;
	Color comLine=Color.black;//棋盘中功能线条(如：横线竖线斜线)的颜色都设置为黑色
	public int lineLength=37;//棋盘格子的边长
	public int leftSize=21;//棋盘左边的边距离
	public int topSize=27;//棋盘离顶部的距离
	int shortLineLength=6;//设置的短线的长度
	int shortLineDistance=3;//短线与正常线之间的距离
	CreateClient father;

	public QiPan(CreateClient father) {
		this.father=father;
		father.qipan=this;
	}
	
	public void paint(Graphics g1){//这个方法名字不能变，这是定义好的方法
		super.paint(g1);//将画出的图形展示出来，
		Graphics2D g=(Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);//打开抗锯齿，这样效果要好看一些
		this.drawComPart(g);//绘制棋盘的公共部分
	}
	
	/**绘制棋子*/
	private void drawQiZi(Graphics2D g){
	//filloval方法 解释
	//绘制椭圆的边框。得到一个圆或椭圆，它刚好能放入由 x、y、width 和 height 参数指定的矩形中。
	//椭圆覆盖区域的宽度为 width + 1 像素，高度为 height + 1 像素。
	g.setFont(new FontUIResource("宋体", Font.BOLD,20));//改变字体大小
	int xOffset=(int)(lineLength*0.3);//棋子x方向上的偏移量
	int yOffset=(int)(lineLength*0.2);//棋子y方向上的偏移量
	int radius=(int)(lineLength*0.5);//棋盘格子长度的一半
	for (int y = 0; y <= 9; y++) {
		for(int x=0;x<=8;x++){
			if(father.qiziList[x][y]==null) continue;
			g.setColor(Color.orange);
//			g.setColor(Color.white);
			g.fillOval(leftSize+lineLength*x-radius,topSize+lineLength*y-radius, lineLength-5, lineLength-5);//画一个外圆圈
			g.setColor(father.qiziList[x][y].getColor());
			g.drawString(father.qiziList[x][y].getName(), leftSize+lineLength*x-xOffset, topSize+lineLength*y+yOffset);//画棋子
		}
	}
	}
	
	
	/**
	 *绘制棋盘上特殊的线
	 *@param x的棋子的横坐标位置，y的棋子的纵坐标位置 
	 */
	private void drawEspecialLine(int x, int y,Graphics2D g){
		if(x>leftSize && x<leftSize+lineLength*8){//绘制的线不在边缘
			//绘制横线
			g.drawLine(x-shortLineLength-shortLineDistance, y+shortLineDistance,x-shortLineDistance, y+shortLineDistance);
			g.drawLine(x-shortLineLength-shortLineDistance, y-shortLineDistance,x-shortLineDistance, y-shortLineDistance);
			g.drawLine(x+shortLineLength+shortLineDistance, y+shortLineDistance,x+shortLineDistance, y+shortLineDistance);
			g.drawLine(x+shortLineLength+shortLineDistance, y-shortLineDistance,x+shortLineDistance, y-shortLineDistance);
			//绘制竖线
			g.drawLine(x-shortLineDistance, y+shortLineDistance,x-shortLineDistance, y+shortLineLength+shortLineDistance);
			g.drawLine(x-shortLineDistance, y-shortLineDistance,x-shortLineDistance, y-shortLineLength-shortLineDistance);
			g.drawLine(x+shortLineDistance, y+shortLineDistance,x+shortLineDistance, y+shortLineLength+shortLineDistance);
			g.drawLine(x+shortLineDistance, y-shortLineDistance,x+shortLineDistance, y-shortLineDistance-shortLineLength);
		}else if(x==leftSize){//在左边线绘制特殊线
			//绘制横线
			g.drawLine(x+shortLineDistance, y+shortLineDistance, x+shortLineDistance+shortLineLength, y+shortLineDistance);
			g.drawLine(x+shortLineDistance, y-shortLineDistance, x+shortLineDistance+shortLineLength, y-shortLineDistance);
			//绘制竖线
			g.drawLine(x+shortLineDistance, y+shortLineDistance, x+shortLineDistance, y+shortLineLength+shortLineDistance);
			g.drawLine(x+shortLineDistance, y-shortLineDistance, x+shortLineDistance, y-shortLineLength-shortLineDistance);
		}else if(x==leftSize+lineLength*8){//在右边线绘制特殊线
			//绘制横线
			g.drawLine(x-shortLineDistance, y+shortLineDistance, x-shortLineDistance-shortLineLength, y+shortLineDistance);
			g.drawLine(x-shortLineDistance, y-shortLineDistance, x-shortLineDistance-shortLineLength, y-shortLineDistance);
			//绘制竖线
			g.drawLine(x-shortLineDistance, y+shortLineDistance, x-shortLineDistance, y+shortLineLength+shortLineDistance);
			g.drawLine(x-shortLineDistance, y-shortLineDistance, x-shortLineDistance, y-shortLineLength-shortLineDistance);
		}
	}
	
	/**绘制棋盘公共部分的基本试图*/
	private void drawComPart(Graphics2D g){
		if("black".equalsIgnoreCase(father.camp)) father.qiziList=father.blackQiZiList;//该客户端是黑方
		if("red".equalsIgnoreCase(father.camp)) father.qiziList=father.redQiZiList;//该客户端是红方
		g.setColor(comLine);
//		g.fill3DRect(leftSize, topSize,lineLength*8,lineLength*9,false); 用来设置棋盘的背景色
		g.drawLine(leftSize, topSize,leftSize,topSize+lineLength*9);//棋盘的左边线
		g.drawLine(leftSize+lineLength*8, topSize, leftSize+lineLength*8,topSize+lineLength*9);//棋盘的右边线
		for(int i=topSize;i<=topSize+lineLength*9;i+=lineLength) 	g.drawLine(leftSize,i, leftSize+lineLength*8,i); //画出棋盘中的横线
		for(int i=leftSize+lineLength;i<=leftSize+lineLength*7;i+=lineLength){ 
			g.drawLine(i,topSize, i,topSize+lineLength*4);//画出棋盘上半部分的竖线
			g.drawLine(i,topSize+lineLength*5, i,topSize+lineLength*9);//画出棋盘下半部分的竖线
		}
		g.drawLine(leftSize+lineLength*3,topSize,leftSize+lineLength*5,101); g.drawLine(leftSize+lineLength*3,topSize+lineLength*2, leftSize+lineLength*5, topSize);//画出上班部分的斜线
		g.drawLine(leftSize+lineLength*3,topSize+lineLength*9,leftSize+lineLength*5,topSize+lineLength*7);g.drawLine(leftSize+lineLength*3,topSize+lineLength*7,leftSize+lineLength*5,topSize+lineLength*9);//画出下半部分的斜线
		//绘制炮位置上的的特殊线
		this.drawEspecialLine(leftSize+lineLength, topSize+lineLength*7, g);
		this.drawEspecialLine(leftSize+lineLength, topSize+lineLength*2, g);
		this.drawEspecialLine(leftSize+lineLength*7, topSize+lineLength*7, g);
		this.drawEspecialLine(leftSize+lineLength*7, topSize+lineLength*2, g);
		//绘制兵位置上的的特殊线
		this.drawEspecialLine(leftSize, topSize+lineLength*6, g);
		this.drawEspecialLine(leftSize+lineLength*2,  topSize+lineLength*6, g);
		this.drawEspecialLine(leftSize+lineLength*4, topSize+lineLength*6, g);
		this.drawEspecialLine(leftSize+lineLength*6, topSize+lineLength*6, g);
		this.drawEspecialLine(leftSize+lineLength*8, topSize+lineLength*6, g);
		
		this.drawEspecialLine(leftSize, topSize+lineLength*3, g);
		this.drawEspecialLine(leftSize+lineLength*2,  topSize+lineLength*3, g);
		this.drawEspecialLine(leftSize+lineLength*4, topSize+lineLength*3, g);
		this.drawEspecialLine(leftSize+lineLength*6, topSize+lineLength*3, g);
		this.drawEspecialLine(leftSize+lineLength*8, topSize+lineLength*3, g);
		//打印出楚河汉界这几个字
		g.setFont(new FontUIResource("宋体", Font.BOLD,30));
		g.drawString("楚河", (int)(leftSize+lineLength*1), (int)(topSize+(lineLength)*4.8));
		g.drawString("汉界", (int)(leftSize+lineLength*5), (int)(topSize+(lineLength)*4.8));
		//绘制棋子
		this.drawQiZi(g);
	}
}
