package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import des.DES;
import messageTran.MessageTran;

public class myActionListener implements ActionListener{
	p2pThread p2pthread;
	DataOutputStream output;
	//String user;
	String user_sendfor;
	JTextArea ta;
	JTextArea text;
	byte cmd;
	int len;
	String date;
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	byte[] data;
	byte[] buffer;
	static int length;
	DatagramSocket socket = null;
	DatagramPacket packet=null;
	byte[] bufferedarray;
	String path="E:\\";
	File file;
	ServerSocket serverSocket;
	public myActionListener(){}
	public myActionListener(DataOutputStream output, String user_sendfor, JTextArea text){
		this.output=output;
		this.user_sendfor=user_sendfor;
		this.text=text;
	}

	public myActionListener(p2pThread p2pthread) {
		// TODO Auto-generated constructor stub
		this.p2pthread=p2pthread;
		//this(p2pthread.output,p2pthread.user_sendfor,p2pthread.text);
		this.output=p2pthread.output;
		this.user_sendfor=p2pthread.user_sendfor;
		this.text=p2pthread.text;
		this.ta=p2pthread.ta;
		this.socket=p2pthread.socket;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd=e.getActionCommand();
	//
		if (!ClientDemo.isConnected) {
			JOptionPane.showMessageDialog(p2pthread, "与服务器关闭连接！！");
			return;
		}
	///	
		
		if(cmd.equals("发送")){
			if(text.getText().equals("")){
				//JOptionPane.s
				JOptionPane.showMessageDialog(p2pthread, "消息为空，请输入！！");
			}else{
				System.out.println("发送message:");
				if(user_sendfor.equals("All Online Users")){
					sendAllMessage();
				}else{
					sendUserMessage();
				}
			}
		}else if(cmd.equals("选择文件")){
			System.out.println("发送file");
			if(user_sendfor.equals("All Online Users")){
				sendAllfile();
			}else{
				sendUserfile();
			}
		}
	}
	
	private void sendUserfile() {
		// TODO Auto-generated method stub
		if(chooseFile()){
			cmd=0x43;
			int port = 0;
			try {
				serverSocket=new ServerSocket(0);
				port=serverSocket.getLocalPort();
				System.out.println("port:"+port);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			length=(int) file.length();
			date=df.format(new Date());
			String message=ClientDemo.user+"#"+date+"#"+file.getName()+"#"+file.length()+"#"+port;
			MessageTran mes=new MessageTran(cmd,message.getBytes());
			if(message.getBytes().length>255){
				JOptionPane.showMessageDialog(p2pthread, "超过字数限制！！");
			}else{
				message=ClientDemo.user+"	"+date+"\n"+"发送文件:"+file.getName()+"  	大小为:"+length+"bytes\n";
				System.out.println(message);
				StringBuffer bf=ClientDemo.getMap().get(user_sendfor);
				if(bf==null){
					JOptionPane.showMessageDialog(p2pthread, user_sendfor+"已下线,请稍后发送！！");
					return;
				}
				bf.append(message);
				ta.setText(bf.toString());
				bufferedarray=mes.getDataTran();
				System.out.println(bufferedarray.length);
				packet=new DatagramPacket(bufferedarray,bufferedarray.length,p2pthread.packet.getAddress(),p2pthread.packet.getPort());
				System.out.println(packet.getLength());
				try {	
					p2pthread.socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//sendFile();
				
				
				
				new sendFileThread().start();
			}
		}
	}
	
	
	
	private void sendAllfile() {
		// TODO Auto-generated method stub
		if(chooseFile()){
			cmd=0x32;
			int port = 0;
		//	ServerSocket serverSocket = null;
			try {
				serverSocket= new ServerSocket(0);
				port=serverSocket.getLocalPort();
				System.out.println("port:"+port);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			length=(int) file.length();
			date=df.format(new Date());
			String key="1234567";
			String message=ClientDemo.user+"#"+date+"#"+file.getName()+"#"+file.length()+"#"+key+"#"+port;
			MessageTran mes=new MessageTran(cmd,DES.encrypt(message, ClientDemo.getPasswd()));
			try {
				output.write(mes.getDataTran());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//time=new Date().getTime();
			message=ClientDemo.user+"	"+date+"\n"+"发送文件:"+file.getName()+"  	大小为:"+length+"bytes\n";
			StringBuffer bf=ClientDemo.getMap().get("All Online Users");
			bf.append(message);
			ta.setText(bf.toString());
			//sendFile();
			new fileServerThread(serverSocket,file,key).start();
			
			//new sendFileThread().start();
		}	
		} 

	class sendFileThread extends Thread {
		public void run() {
			ServerSocket server = serverSocket;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				System.out.println(fis.available());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] buffer = new byte[1024];
			try {
				System.out.println("ssss");
				// Socket socket=serverSocket.accept();
				Socket socket = server.accept();
				System.out.println("服务已连接");
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				int readsize = 0;
				// for(int i=0;i<block;i++)
				while (true) {
					readsize = fis.read(buffer, 0, buffer.length);
					if (readsize == -1)
						break;
					out.write(buffer, 0, readsize);
					System.out.println(readsize);
					out.flush();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
/*	private void sendFile() {
		// TODO Auto-generated method stub
		buffer=new byte[1024];
		int block=length/1024+((length%1024==0)?0:1);
		if (user_sendfor.equals("All Online Users")) {
			for (int i = 0; i < block; i++) {
				try {
					len = fis.read(buffer, 0, buffer.length);
					output.write(buffer, 0, len);
					System.out.println(len);
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			for (int i = 0; i < block; i++) {
				try {
					len = fis.read(buffer, 0, buffer.length);
					packet=new DatagramPacket(buffer,len,p2pthread.packet.getAddress(),p2pthread.packet.getPort());
					System.out.println("第"+i+": "+packet.getLength());
					p2pthread.socket.send(packet);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	private boolean chooseFile() {
		// TODO Auto-generated method stub
		JFileChooser fc=new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY); 
		int result=fc.showDialog(p2pthread,"选择");
		//fc.showOpenDialog(p2pthread);
		if (result == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			System.out.println(fc.getName(file));
			System.out.println(file.getAbsolutePath()+file.getName()+file.length());
		/*	try {
				fis=new FileInputStream(file);
				System.out.println(fis.available());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			return true;
		}
		return false;
		}

	private void sendUserMessage() {
		// TODO Auto-generated method stub
		cmd=0x42;
		String message;
		date=df.format(new Date());
		message=ClientDemo.user+"	"+date+"\n"+text.getText()+"\n";
		if(message.getBytes().length>255){
			JOptionPane.showMessageDialog(p2pthread, "超过字数限制！！");
		}else{
			System.out.println(message);
			StringBuffer bf=ClientDemo.getMap().get(user_sendfor);
			if(bf==null){
				JOptionPane.showMessageDialog(p2pthread, user_sendfor+"已下线,请稍后上线发送！！");
				return;
			}
			bf.append(message);
			ta.setText(bf.toString());
			text.setText("");
			MessageTran mes=new MessageTran(cmd,message.getBytes());
			//data=mes.getDataTran();
			bufferedarray=mes.getDataTran();
			System.out.println(bufferedarray.length);
			packet=new DatagramPacket(bufferedarray,bufferedarray.length,p2pthread.packet.getAddress(),p2pthread.packet.getPort());
			System.out.println(packet.getLength());
			try {
				p2pthread.socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void sendAllMessage() {
		// TODO Auto-generated method stub
		cmd=0x31;
		String message;
		//time=new Date().getTime();
	//	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
		message=ClientDemo.user+"	"+date+"\n"+text.getText()+"\n";
		if(message.getBytes().length>255){
			JOptionPane.showMessageDialog(p2pthread, "超过字数限制！！");
		}else{
			System.out.println(message);
			StringBuffer bf=ClientDemo.getMap().get("All Online Users");
			bf.append(message);
			ta.setText(bf.toString());
			text.setText("");
			//MessageTran mes=new MessageTran(cmd,message.getBytes());
			//data=mes.getDataTran();
			MessageTran mes=new MessageTran(cmd,DES.encrypt(message, ClientDemo.getPasswd()));
			try {
				output.write(mes.getDataTran());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
