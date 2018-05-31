package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import Demo.ClientDemo;
import des.DES;
import messageTran.MessageTran;

public class Client {
	public ClientDemo demo;
	private int port=9002;
	private String ip="127.0.0.1";
	Socket socket=null;
	DataInputStream input=null;
	DataOutputStream output=null;
	static String user="hxii";
	static String passwd="123456";
	static ArrayList<p2pThread> p2pthreads=new ArrayList<p2pThread>();
	static HashMap<String,StringBuffer> map=new HashMap<String,StringBuffer>();
	static boolean isConnected=false;
	MessageThread messageThread;
	public Client(){
		demo=new ClientDemo();
		try {
			connectServer();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		map.put("All Online Users", new StringBuffer());
		MouseAdapter mouseAdapter=new ExtendMouseAdapter(demo,output);
		demo.list.addMouseListener(mouseAdapter);
		messageThread = new MessageThread(input,demo.list);
		messageThread.start();
		demo.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					closep2pConnect();
					closeConnect();						
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);// 退出程序
			}
		});
	}
	
	public Client(Socket socket,DataInputStream input,DataOutputStream output,String user,String passwd){
		demo=new ClientDemo();
		this.socket=socket;
		this.input=input;
		this.output=output;
		this.user=user;
		this.passwd=passwd;
		map.put("All Online Users", new StringBuffer());
		MouseAdapter mouseAdapter=new ExtendMouseAdapter(demo,output);
		demo.list.addMouseListener(mouseAdapter);
		messageThread = new MessageThread(input,demo.list);
		messageThread.start();
		demo.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					closep2pConnect();
					closeConnect();						
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);// 退出程序
			}
		});
		user_online();
	}
	
	protected void closep2pConnect() {
		// TODO Auto-generated method stub
		for(p2pThread p2p :p2pthreads){
			if(p2p.isConp2p){
				p2p.closep2pCon();
			}
		}
	}

	protected void closeConnect() throws IOException {// 客户端主动关闭连接
		// TODO Auto-generated method stub
		// 记得关闭客户端服务线程!!!!!!!!!!!
		if (input != null) {
			input.close();
		}
		if (output != null) {
			output.close();
		}
		if (socket != null) {
			socket.close();
		}
		isConnected = false;
		// 客户端关闭与服务器的连接
	}
	
	private void connectServer() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		socket=new Socket(ip,port);
		System.out.println("已连接服务器  ip:"+socket.getLocalPort());
		isConnected=true;
		input=new DataInputStream(socket.getInputStream());
		output=new DataOutputStream(socket.getOutputStream());
		user_online();
	}
	
	private void user_online() {
		// TODO Auto-generated method stub
		byte cmd=0x07;
		int len;
		byte[] buffer=new byte[1024];
		//MessageTran message=new MessageTran(cmd,user.getBytes());
		MessageTran message=new MessageTran(cmd,new DES().encrypt(user, passwd));
		try {
			output.write(message.getDataTran());
			output.flush();
			len=input.read(buffer);
			String[] allUser=new String(Arrays.copyOfRange(buffer, 0, len)).split(" "); 
			for(int i=0;i<allUser.length;i++){
				demo.listModel.addElement(allUser[i]);
				map.put(allUser[i], new StringBuffer());
				map.remove("");
			}
			System.out.println(Client.getMap().keySet());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,StringBuffer> getMap() {
		// TODO Auto-generated method stub
		return map;
	}
	
	public static ArrayList<p2pThread> getp2pThreads() {
		// TODO Auto-generated method stub
		return p2pthreads;
	}
	
	public static String getPasswd() {
		// TODO Auto-generated method stub
		return passwd;
	}
	
	public static void main(String[] args) {
		new Client();
	}
	
}
