package client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import javax.swing.JOptionPane;

import Demo.ClientDemo;
public class ExtendMouseAdapter extends MouseAdapter {
	private ClientDemo demo;
	private DataOutputStream output;
	public ExtendMouseAdapter(){}
	public ExtendMouseAdapter(ClientDemo demo,DataOutputStream output){
	//	this.isConnected=isConnected;
	//	this.p2pthreads=p2pthreads;
		this.demo=demo;
		this.output=output;
	}
	public void mouseEnter(){
		//设置鼠标进入的选项颜色
	}
	
	public void mouseExit(){
		
	}
	
	public void mouseClicked(MouseEvent e){
		if (e.getClickCount() == 2) {
			if (!Client.isConnected) {
				JOptionPane.showMessageDialog(demo.scrollPane, "与服务器关闭连接！！");
				return;
			}
			String user_sendfor = (String) demo.list.getSelectedValue();
			for (p2pThread p2p : Client.getp2pThreads()) {
				//System.out.println(p2p.isAlive()+p2p.user_sendfor);
				//if(p2p==null)System.out.println("hh");
				//System.out.println(p2p);
				if (user_sendfor.equals(p2p.user_sendfor)) {
					JOptionPane.showMessageDialog(demo.scrollPane, "已打开相同对话窗口哦！！");
					return;
				}
			}
			p2pThread p2p_thread = new p2pThread(output, user_sendfor);
			//new Thread(p2p_thread).start();// socket
			p2p_thread.start();
			Client.getp2pThreads().add(p2p_thread);
			System.out.println(demo.list.getSelectedValue());
		}
	}
}