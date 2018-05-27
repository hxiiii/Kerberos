package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import des.DES;
import messageTran.MessageTran;

public class ClientDemo extends JFrame {//socket
	//JPanel panel;
	JList list;
	DefaultListModel listModel;
	JScrollPane scrollPane;
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
	public ClientDemo(){
		listModel=new DefaultListModel();
		listModel.addElement("All Online Users");
		try {
			connectServer();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//listModel=new DefaultListModel();
		//listModel.addElement("All Online User");
		list=new JList(listModel);
		map.put("All Online Users", new StringBuffer());
		list.setSelectionBackground(Color.red);
		MouseAdapter mouseAdapter=new ExtendMouseAdapter();
		list.addMouseListener(mouseAdapter);
		scrollPane=new JScrollPane(list);
		add(scrollPane,BorderLayout.CENTER);
		setTitle("主页面");
		list.setBackground(Color.cyan);
		//list.setBorder(BorderFactory.createBevelBorder(1));
		messageThread = new MessageThread(input,list);
		messageThread.start();
		pack();
		setSize(300,500);
		//setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		//setDefaultCloseOperation(3);
		addWindowListener(new WindowAdapter() {
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
		MessageTran message=new MessageTran(cmd,DES.encrypt(user, passwd));
		try {
			output.write(message.getDataTran());
			output.flush();
			len=input.read(buffer);
			String[] allUser=new String(Arrays.copyOfRange(buffer, 0, len)).split(" "); 
			for(int i=0;i<allUser.length;i++){
				listModel.addElement(allUser[i]);
				map.put(allUser[i], new StringBuffer());
				map.remove("");
			}
			System.out.println(ClientDemo.getMap().keySet());
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
	
	private class ExtendMouseAdapter extends MouseAdapter {
		public ExtendMouseAdapter(){}
		public void mouseEnter(){
			//设置鼠标进入的选项颜色
		}
		
		public void mouseExit(){
			
		}
		
		public void mouseClicked(MouseEvent e){
			if (e.getClickCount() == 2) {
				if (!isConnected) {
					JOptionPane.showMessageDialog(scrollPane, "与服务器关闭连接！！");
					return;
				}
				String user_sendfor = (String) list.getSelectedValue();
				for (p2pThread p2p : p2pthreads) {
					if (user_sendfor.equals(p2p.user_sendfor)) {
						JOptionPane.showMessageDialog(scrollPane, "已打开相同对话窗口哦！！");
						return;
					}
				}
				p2pThread p2p_thread = new p2pThread(output, user_sendfor);
				new Thread(p2p_thread).start();// socket
				p2pthreads.add(p2p_thread);
				System.out.println(list.getSelectedValue());
				// DefaultListModel d=(DefaultListModel) list.getModel();
				// d.addElement("hh");
			}
		}
	}

	public static void main(String[] args) {
		new ClientDemo();
	}

	public static String getPasswd() {
		// TODO Auto-generated method stub
		return passwd;
	}
}
