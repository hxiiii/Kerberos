package client;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import messageTran.MessageTran;

public class p2pMessageThread implements Runnable {
	byte[] data;
	InetAddress address;
	int port;
	String user_sendfrom;
	byte cmd;
	int len;
	DatagramSocket socket=null;
	DatagramPacket packet=null;
	byte[] bufferedarray;
	String path="E:\\";
	String ip;
	public p2pMessageThread(){}
	public p2pMessageThread(byte[] data){
		this.data=data;
		String[] message=new String(data).split(" ");
		user_sendfrom=message[0];
		try {
			this.address=InetAddress.getByName(message[1]);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port=Integer.parseInt(message[2]);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		authentication();
		bufferedarray=new byte[1024];
		while(true){
			packet=new DatagramPacket(bufferedarray,bufferedarray.length);
			try {
				socket.receive(packet);
				cmd=bufferedarray[0];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(cmd==0x40){
				System.out.println("�ر�p2p����");
				socket.close();
				return;//�ر�����
			}else if(cmd==0x42){
				data=Arrays.copyOfRange(packet.getData(),2, packet.getLength());
				receiveMessage();//������Ϣ
			}else if(cmd==0x43){
				//�����ļ�
				data=Arrays.copyOfRange(packet.getData(),2, packet.getLength());
				System.out.println(new String(data));
			//	receiveFile();
				new receiveFileThread().start();
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
			String m=message[0]+" "+message[1]+"\n"+"�����ļ�:"+message[2]+"	��СΪ:"+length+"bytes\n";
			StringBuffer bf=ClientDemo.getMap().get(user_sendfrom);
			bf.append(m);
			ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
			for(p2pThread p2p:p2pthreads){
				if(p2p.user_sendfor.equals(user_sendfrom)){
					p2p.ta.setText(bf.toString());
				}
			}
			Socket socket=null;
			try {
				System.out.println("socket");
				socket = new Socket(address.getHostAddress(),Integer.parseInt(message[4]));
				DataInputStream input=new DataInputStream(socket.getInputStream());
				int count=0;
				byte[] buffer=new byte[1024];
				System.out.println("socket");
				int readsize;
				while(count<length){
					try {
						readsize = input.read(buffer);
						count+=readsize;
						System.out.println(readsize+"���L:"+count);
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
		int block = length / 1024 + ((length % 1024 == 0) ? 0 : 1);
	/*	String m=message[0]+" "+message[1]+"\n"+"�����ļ�:"+message[2]+"	��СΪ:"+length+"bytes\n";
		StringBuffer bf=ClientDemo.getMap().get(user_sendfrom);
		bf.append(m);
		ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals(user_sendfrom)){
				p2p.ta.setText(bf.toString());
			}
		}*/
	/*	for (int n = 0; n < block; n++) {
			try {
				packet=new DatagramPacket(bufferedarray,bufferedarray.length);
				try {
					socket.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("��"+n+": "+packet.getLength());
				fos.write(packet.getData(), 0,packet.getLength());
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		int count=0;
		while(count<length){
				packet=new DatagramPacket(bufferedarray,bufferedarray.length);
				try {
					socket.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count+=packet.getLength();
				System.out.println(packet.getLength()+"���L:"+count);	
				try {
					fos.write(packet.getData(), 0,packet.getLength());
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
		String m=message[0]+" "+message[1]+"\n"+"�����ļ�:"+message[2]+"	��СΪ:"+length+"bytes\n";
		StringBuffer bf=ClientDemo.getMap().get(user_sendfrom);
		bf.append(m);
		ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals(user_sendfrom)){
				p2p.ta.setText(bf.toString());
			}
		}
	}
	
	private void receiveMessage() {
		// TODO Auto-generated method stub
		String message=new String(data);
		System.out.println("������ϢΪ��"+message);
		StringBuffer bf=ClientDemo.getMap().get(user_sendfrom);
		bf.append(message);
		ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals(user_sendfrom)){
				p2p.ta.setText(bf.toString());
			}
		}
	}
	
	private void authentication() {
		// TODO Auto-generated method stub
		cmd=0x41;
		MessageTran mes=new MessageTran(cmd,ClientDemo.user.getBytes());
		bufferedarray=mes.getDataTran();
		try{
			socket=new DatagramSocket();
			packet=new DatagramPacket(bufferedarray,bufferedarray.length,address,port);
			socket.send(packet);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
