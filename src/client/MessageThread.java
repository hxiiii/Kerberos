package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JList;


public class MessageThread extends Thread {
	//Socket socket=null;
	DataInputStream input=null;
	//DataOutputStream output=null;
	JList list;
	DefaultListModel model;
	byte[] buffer=new byte[1024];
	int len = 0;
	byte cmd = 0;
	byte[] data;
	String path="E:\\";
	public MessageThread(){}
	public MessageThread(DataInputStream input,JList list) {
		// TODO Auto-generated constructor stub
		//this.socket=socket;
		this.input=input;
		this.list=list;
		model=(DefaultListModel) list.getModel();
		System.out.println("接收服务器线程开启！！");
		/*try {
			input=new DataInputStream(socket.getInputStream());
			output=new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/	
	}
	
	public void run(){
		while(true){
			try {
				len=input.read(buffer);
				if(len==-1){
					//clsoeConnect();
					ClientDemo.isConnected=false;
					return;
				}
				cmd=buffer[0];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("已关闭接收服务器消息线程！！");
				ClientDemo.isConnected=false;
				break;
			}
			if(cmd==0x20){
				data=Arrays.copyOfRange(buffer, 2, len);
				String user=new String(data);
				model.addElement(user);//上线通知
				ClientDemo.getMap().put(user, new StringBuffer());
				System.out.println(ClientDemo.getMap().keySet());
			}else if(cmd==0x21){
				data=Arrays.copyOfRange(buffer, 2, len);
				String user=new String(data);
				model.removeElement(user);//下线通知
				ClientDemo.getMap().remove(user, ClientDemo.getMap().get(user));
			}else if(cmd==0x23){
				//接收群发消息
				data=Arrays.copyOfRange(buffer, 2, len);
				String message=new String(data);
				System.out.println("群發消息");
				StringBuffer bf=ClientDemo.getMap().get("All Online Users");
				bf.append(message);
				ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
				for(p2pThread p2p:p2pthreads){
					if(p2p.user_sendfor.equals("All Online Users")){
						p2p.ta.setText(bf.toString());
					}
				}
			}else if(cmd==0x24){
				//群发文件
				data=Arrays.copyOfRange(buffer, 2, len);
				System.out.println(new String(data));
			//	receiveFile();
				///
				new receiveFileThread().start();
			
				
			}else if(cmd==0x25){
				//p2p连接
				data=Arrays.copyOfRange(buffer, 2, len);
				System.out.println("p2p连接,新线程开启"+new String(data));
				new Thread(new p2pMessageThread(data)).start();
				//新线程
			}	
		}
	}
	
	class receiveFileThread extends Thread{
		public void run(){
			String[] message = new String(data).split(" ");
			File file = new File(path + message[2]);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int length = Integer.parseInt(message[3]);
			String m=message[0]+" "+message[1]+"\n"+"发送文件:"+message[2]+"	大小为:"+length+"bytes\n";
			StringBuffer bf=ClientDemo.getMap().get("All Online Users");
			bf.append(m);
			ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
			for(p2pThread p2p:p2pthreads){
				if(p2p.user_sendfor.equals("All Online Users")){
					p2p.ta.setText(bf.toString());
					break;
				}
			}
				Socket socket=null;
				try {
					System.out.println("socket");
					socket = new Socket(message[5],Integer.parseInt(message[4]));
					DataInputStream input=new DataInputStream(socket.getInputStream());
					int count=0;
					byte[] buffer=new byte[1024];
					System.out.println("socket");
					int readsize=0;
					while(count<length){
						try {
							readsize = input.read(buffer);
							count+=readsize;
							System.out.println(readsize+"總長:"+count);
							fos.write(buffer, 0, readsize);
							fos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(socket!=null){
						socket.close();
					}
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/*
	private void receiveFile() {
		// TODO Auto-generated method stub
		String[] message = new String(data).split(" ");
		File file = new File(path + message[2]);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int length = Integer.parseInt(message[3]);
		String m=message[0]+" "+message[1]+"\n"+"发送文件:"+message[2]+"	大小为:"+length+"bytes\n";
		StringBuffer bf=ClientDemo.getMap().get("All Online Users");
		bf.append(m);
		ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals("All Online Users")){
				p2p.ta.setText(bf.toString());
			}
		int count=0;
		while(count<length){
			try {
				len = input.read(buffer);
				count+=len;
				System.out.println(len+"總長:"+count);
				fos.write(buffer, 0, len);
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/




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
