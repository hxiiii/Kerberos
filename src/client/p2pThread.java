package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import messageTran.MessageTran;

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
	int length=1024;
	byte[] bufferedarray=new byte[length];
	//String user;
	String user_sendfor;
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
		    }
		}
	}
	
	private boolean authentication() {
		// TODO Auto-generated method stub
		try {
			packet=new DatagramPacket(bufferedarray,bufferedarray.length);
			socket.receive(packet);
			byte[] data=Arrays.copyOfRange(packet.getData(), 2, packet.getLength());
			System.out.println(new String(data));
			//RSA认证
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private void sendp2pRequest() {
		// TODO Auto-generated method stub
		byte cmd=0x33;
		try {
			String ip=InetAddress.getLocalHost().getHostAddress();
			System.out.println("ip:"+ip);
			//socket.getInetAddress().getHostAddress()
			String message=user_sendfor+" "+socket.getLocalPort();
			byte[] data=message.getBytes();
			MessageTran mes=new MessageTran(cmd,data);
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
