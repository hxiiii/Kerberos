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
import java.util.Arrays;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import Demo.MessageDemo;
import des.DES;
import messageTran.MessageTran;

public class myActionListener implements ActionListener{
	p2pThread p2pthread;
	DataOutputStream output;
	MessageDemo demo;
	String user_sendfor;
	SimpleDateFormat df;
	DatagramSocket socket = null;
	File file = null;
	String path="E:\\";
	public myActionListener(){}
	public myActionListener(p2pThread p2pthread) {
		// TODO Auto-generated constructor stub
		this.p2pthread=p2pthread;
		this.output=p2pthread.output;
		this.demo=p2pthread.demo;
		this.user_sendfor=p2pthread.user_sendfor;
		this.socket=p2pthread.socket;
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd=e.getActionCommand();
	//
		if (!Client.isConnected) {
			JOptionPane.showMessageDialog(demo, "��������ر����ӣ���");
			return;
		}
	///	
		if(cmd.equals("����")){
			if(demo.text.getText().equals("")){
				//JOptionPane.s
				JOptionPane.showMessageDialog(demo, "��ϢΪ�գ������룡��");
			}else{
				System.out.println("����message:");
				if(user_sendfor.equals("All Online Users")){
					sendAllMessage();
				}else{
					if(!p2pthread.isConp2p){
						JOptionPane.showMessageDialog(demo, "��֤ʧ�ܣ���");
						return;
					}
					sendUserMessage();
				}
			}
		}else if(cmd.equals("ѡ���ļ�")){
			System.out.println("����file");
			if(user_sendfor.equals("All Online Users")){
				sendAllfile();
			}else{
				if(!p2pthread.isConp2p){
					JOptionPane.showMessageDialog(demo, "��֤ʧ�ܣ���");
					return;
				}
				sendUserfile();
			}
		}
	}
	
	private void sendUserfile() {
		// TODO Auto-generated method stub
		if(chooseFile()){
			byte cmd=0x43;
			int port = 0;
			ServerSocket serverSocket=null;
			try {
				serverSocket=new ServerSocket(0);
				port=serverSocket.getLocalPort();
				System.out.println("port:"+port);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int length=(int) file.length();
			String date=df.format(new Date());
			String message=Client.user+"#"+date+"#"+file.getName()+"#"+file.length()+"#"+port;
			if(message.getBytes().length>248){
				JOptionPane.showMessageDialog(demo, "�����������ƣ���");
			}else{
				MessageTran mes=new MessageTran(cmd,new DES().encrypt(message, p2pthread.key));
				message=Client.user+"	"+date+"\n"+"�����ļ�:"+file.getName()+"	��СΪ:"+length+"bytes\n";
				System.out.println(message);
				StringBuffer bf=Client.getMap().get(user_sendfor);
				if(bf==null){
					JOptionPane.showMessageDialog(demo, user_sendfor+"������,��رյ�ǰ���ڣ���");
					return;
				}
				bf.append(message);
				demo.ta.setText(bf.toString());
				byte[] buffer=mes.getDataTran();
				System.out.println(buffer.length);
				DatagramPacket packet=new DatagramPacket(buffer,buffer.length,p2pthread.packet.getAddress(),p2pthread.packet.getPort());
				System.out.println(packet.getLength());
				try {	
					p2pthread.socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//sendFile();
				
				
				
				new sendFileThread(serverSocket,file,p2pthread.key).start();
			}
		}
	}
	
	
	
	private void sendAllfile() {
		// TODO Auto-generated method stub
		String key="1234567";
		if(chooseFile()){
			byte cmd=0x32;
			int port = 0;
			ServerSocket serverSocket = null;
			try {
				serverSocket= new ServerSocket(0);
				port=serverSocket.getLocalPort();
				System.out.println("port:"+port);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int length=(int) file.length();
			String date=df.format(new Date());
			String message=Client.user+"#"+date+"#"+file.getName()+"#"+file.length()+"#"+key+"#"+port;
			MessageTran mes=new MessageTran(cmd,new DES().encrypt(message, Client.getPasswd()));
			try {
				output.write(mes.getDataTran());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//time=new Date().getTime();
			message=Client.user+"	"+date+"\n"+"�����ļ�:"+file.getName()+"  	��СΪ:"+length+"bytes\n";
			StringBuffer bf=Client.getMap().get("All Online Users");
			bf.append(message);
			demo.ta.setText(bf.toString());
			
			new fileServerThread(serverSocket,file,key).start();
		}	
		} 

	
	
	private boolean chooseFile() {
		// TODO Auto-generated method stub
		JFileChooser fc=new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY); 
		int result=fc.showDialog(demo,"ѡ��");
		if (result == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			System.out.println(fc.getName(file));
			System.out.println(file.getAbsolutePath()+file.getName()+file.length());
			return true;
		}
		return false;
		}

	private void sendUserMessage() {
		// TODO Auto-generated method stub
		byte cmd=0x42;
		String message;
		String date=df.format(new Date());
		message=Client.user+"	"+date+"\n"+demo.text.getText()+"\n";
		if(message.getBytes().length>248){
			JOptionPane.showMessageDialog(demo, "�����������ƣ���");
		}else{
			System.out.println(message);
			StringBuffer bf=Client.getMap().get(user_sendfor);
			if(bf==null){
				JOptionPane.showMessageDialog(demo, user_sendfor+"������,��رյ�ǰ���ڣ���");
				return;
			}
			bf.append(message);
			demo.ta.setText(bf.toString());
			demo.text.setText("");
			MessageTran mes=new MessageTran(cmd,new DES().encrypt(message, p2pthread.key));
			//data=mes.getDataTran();
			byte[] bufferedarray=mes.getDataTran();
			System.out.println(bufferedarray.length);
			DatagramPacket packet=new DatagramPacket(bufferedarray,bufferedarray.length,p2pthread.packet.getAddress(),p2pthread.packet.getPort());
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
		byte cmd=0x31;
		String date = df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ�䣬Ҳ��ʹ�õ�ǰʱ���
		String message=Client.user+"	"+date+"\n"+demo.text.getText()+"\n";
		if(message.getBytes().length>248){
			JOptionPane.showMessageDialog(demo, "�����������ƣ���");
		}else{
			System.out.println(message);
			StringBuffer bf=Client.getMap().get("All Online Users");
			bf.append(message);
			demo.ta.setText(bf.toString());
			demo.text.setText("");
			MessageTran mes=new MessageTran(cmd,new DES().encrypt(message, Client.getPasswd()));
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
