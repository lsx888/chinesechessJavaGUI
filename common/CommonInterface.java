package chinesechess.common;

/**
 * 中国象棋游戏的功能接口..
 * @author lisx
 * @2017年5月19日
 */
public interface CommonInterface {
	
	/**开始创建*/
	public void start();
	
	/**初始化组件*/
	public void initializeComponent();
	
	/**增加监听器*/
	public void addListener();
	
	/**初始化状态*/
	public void initializeState();
	
	/**初始化窗口
	 * @throws Throwable */
	public void initializeWindow() throws Throwable;
}
