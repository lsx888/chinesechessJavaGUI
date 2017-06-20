package chinesechess.bean;

import java.awt.Color;

/**
 * 每个棋子的各种属性
 * @author lisx
 * @2017年6月11日
 */
public class QiZi {
	private String name;
	private Color color;
	
	public QiZi(String name,Color color){
		this.name=name;
		this.color=color;
	}
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
