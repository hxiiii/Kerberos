package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import des.DES;


public class MessageThread extends Thread {
	private DataInputStream input=null;
	private JList list;
	private DefaultListModel model;
	private byte[] data;
	private String path="E:\\";
	public MessageThread(){}
	public MessageThread(DataInputStream input,JList list) {
		// TODO Auto-generated constructor stub
		this.input=input;
		this.list=list;
		model=(DefaultListModel) list.getModel();
		System.out.println("接收服务器线程开启！！");
	}
	
	public void run(){
		byte[] buffer=new byte[1024];
		int len = 0;
		byte cmd = 0;
		while(true){
			try {
				len=input.read(buffer);
				if(len==-1){
					//clsoeConnect();
					Client.isConnected=false;
					return;
				}
				cmd=buffer[0];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("已关闭接收服务器消息线程！！");
				Client.isConnected=false;
				break;
			}
			if(cmd==0x20){
				data=Arrays.copyOfRange(buffer, 2, len);
				String user=new String(data);
				model.addElement(user);//上线通知
				Client.getMap().put(user, new StringBuffer());
				System.out.println(Client.getMap().keySet());
			}else if(cmd==0x21){
				data=Arrays.copyOfRange(buffer, 2, len);
				String user=new String(data);
				model.removeElement(user);//下线通知
				Client.getMap().remove(user, Client.getMap().get(user));
			}else if(cmd==0x23){
				//接收群发消息
				data=Arrays.copyOfRange(buffer, 2, len);
				String message=new String(new DES().decrypt(data, Client.getPasswd()));
				System.out.println("群發消息"+message);
				StringBuffer bf=Client.getMap().get("All Online Users");
				bf.append(message);
				ArrayList<p2pThread> p2pthreads=Client.getp2pThreads();
				for(p2pThread p2p:p2pthreads){
					if(p2p.user_sendfor.equals("All Online Users")){
						p2p.demo.ta.setText(bf.toString());
						break;
					}
				}
			}else if(cmd==0x24){
				//群发文件
				data=Arrays.copyOfRange(buffer, 2, len);
				System.out.println(new String(new DES().decrypt(data, Client.getPasswd())));
				new receiveFileThread(data).start();	
			}else if(cmd==0x25){
				//p2p连接
				data=Arrays.copyOfRange(buffer, 2, len);
				System.out.println("p2p连接,新线程开启"+new String(new DES().decrypt(data, Client.getPasswd())));
				new Thread(new p2pMessageThread(data)).start();
				//新线程
			}	
		}
	}
	
	private void clsoeConnect() {
		// TODO Auto-generated method stub
		//关闭客户端服务线程!!!!!!!!!!!
		/*try {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
