package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import des.DES;
import messageTran.MessageTran;
import rsa.KEY;
import rsa.PrivateKey;
import rsa.PublicKey;
import rsa.RSAUtil;

public class p2pThread extends JFrame implements Runnable{
	JTextArea ta;
	JPanel southPanel;
	JPanel sendPanel;
	JScrollPane scroll;
	JButton button_message;
	JButton button_file;
	//JTextField text;
	JTextArea text;
	DataOutputStream output;
	DatagramSocket socket = null;
	DatagramPacket packet=null;
	//int length=1024;
	byte[] bufferedarray;
	//String user;
	String user_sendfor;
	String key=null;
	myActionListener listener;
	boolean isConp2p=false;
	public p2pThread(){}
	public p2pThread(DataOutputStream output,String user_sendfor){
		this.output=output;
		this.user_sendfor=user_sendfor;
		init();	
	}
	
	
	private void init() {
		// TODO Auto-generated method stub
		ta=new JTextArea();
		ta.setEditable(false);
		ta.setLineWrap(true);
		scroll=new JScrollPane(ta);
		southPanel=new JPanel();
		sendPanel=new JPanel();
		southPanel.setPreferredSize(new Dimension(0,55));
		southPanel.setLayout(new BorderLayout());
		sendPanel.setLayout(new BorderLayout());
		//listener=new myActionListener(output,user_sendfor,text);
		button_message=new JButton("发送");
		button_file=new JButton("选择文件");
		text=new JTextArea();
		text.setLineWrap(true);
		sendPanel.add(button_message, BorderLayout.NORTH);
		sendPanel.add(button_file, BorderLayout.SOUTH);
		southPanel.add(text, BorderLayout.CENTER);
		southPanel.add(sendPanel, BorderLayout.EAST);
		add(scroll,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		setTitle(user_sendfor);
		pack();
		setSize(500,500);
		//setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		//setDefaultCloseOperation(3);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {	
				//关闭线程
				if(isConp2p){
					closep2pCon();
				}
				removep2pThread();
				return;
			}
		});
	}
	
	protected void closep2pCon() {
		// TODO Auto-generated method stub
		byte cmd=0x40;
		MessageTran mes=new MessageTran(cmd);
		bufferedarray=mes.getDataTran();
		//System.out.println(bufferedarray.length);
		DatagramPacket p=new DatagramPacket(bufferedarray,bufferedarray.length,packet.getAddress(),packet.getPort());
		try {
			socket.send(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void removep2pThread() {
		// TODO Auto-generated method stub
		ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
		for(int i=0;i<p2pthreads.size();i++){
			if(p2pthreads.get(i)==this){
				p2pthreads.remove(i);
				break;
			}
		}
	}
	
	public void run(){
		System.out.println("对话开始"+user_sendfor);
		listener=new myActionListener(this);
		button_message.addActionListener(listener);
		button_file.addActionListener(listener);
		if(user_sendfor.equals("All Online Users")){
			StringBuffer bf=ClientDemo.getMap().get("All Online Users");
			ta.setText(bf.toString());
		//listener=new myActionListener(output,user_sendfor,text);
		/*	listener=new myActionListener(this);
			button_message.addActionListener(listener);
			button_file.addActionListener(listener);*/
		}else{
			isConp2p=true;
			StringBuffer bf=ClientDemo.getMap().get(user_sendfor);
			ta.setText(bf.toString());
			try {
				socket = getRandomPort();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		    System.out.println("__________socket.getLocalPort():" + socket.getLocalPort()); 
		    sendp2pRequest();//请求p2p连接
		    if(!authentication()){//身份认证
		    	//认证失败
		    	//this.dispose();
		    	//closep2pCon();
		    	isConp2p=false;
		    }
		}
	}
	
	private boolean authentication() {
		// TODO Auto-generated method stub
		boolean flag = false;
		bufferedarray=new byte[1024];
		try {
			packet=new DatagramPacket(bufferedarray,bufferedarray.length);
			socket.receive(packet);
			byte[] data=Arrays.copyOfRange(packet.getData(), 2, packet.getLength());
			System.out.println(new String(data));
			//RSA认证
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(user_sendfor+"_PublicKey.dat"));
			KEY key=(PublicKey)ois.readObject();
			flag=RSAUtil.verify(user_sendfor, new BigInteger(data), key);
			flag=sendACK(flag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println(flag);
		return flag;
	}
	
	private Boolean sendACK(boolean flag) {
		// TODO Auto-generated method stub
		if(flag==true){
			try {
	            // 获得一个指定编码的信息摘要算法
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            // 获得数据的数据指纹
	            byte[] digest = md.digest(ClientDemo.user.getBytes());
	            ObjectInputStream ois;
	            BigInteger m ;
	    		try {
	    			ois = new ObjectInputStream(new FileInputStream(ClientDemo.user+"_PrivateKey.dat"));
	    			KEY key = (PrivateKey) ois.readObject();
	    			ois.close();
	    			m= RSAUtil.encrypt(key, digest);
	    			DatagramPacket ackPacket=new DatagramPacket(m.toByteArray(),m.toByteArray().length,packet.getAddress(),packet.getPort());
	    			socket.send(ackPacket);
	    		} catch (IOException | ClassNotFoundException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	        } catch (NoSuchAlgorithmException e1) {
	            e1.printStackTrace();
	        }
			return receiveACK();
		}else{
			String message="NOTACK";
			DatagramPacket ackPacket=new DatagramPacket(message.getBytes(),message.getBytes().length,packet.getAddress(),packet.getPort());
			try {
				socket.send(ackPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}
	
	private Boolean receiveACK() {
		// TODO Auto-generated method stub
		byte[] buffer=new byte[1024];
		DatagramPacket ackPacket=new DatagramPacket(buffer,buffer.length);
		try {
			socket.receive(ackPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(new String(ackPacket.getData()));
		if(new String(Arrays.copyOfRange(ackPacket.getData(), 0, ackPacket.getLength())).equals("ACK")){
			return true;
		}else{
			return false;
		}
	}
	
	private void sendp2pRequest() {
		// TODO Auto-generated method stub
		byte cmd=0x33;
		try {
			String ip=InetAddress.getLocalHost().getHostAddress();
			System.out.println("ip:"+ip);
			//socket.getInetAddress().getHostAddress()
			key="abcdefg";
			String message=user_sendfor+" "+socket.getLocalPort()+" "+key;
			MessageTran mes=new MessageTran(cmd,new DES().encrypt(message,ClientDemo.getPasswd()));
			try {
				output.write(mes.getDataTran());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static DatagramSocket getRandomPort() throws SocketException {  
	    DatagramSocket s = new DatagramSocket(0);  
	    return s;  
	}  
}
