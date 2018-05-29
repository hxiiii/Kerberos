package client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import des.DES;
import messageTran.MessageTran;
import rsa.KEY;
import rsa.PrivateKey;
import rsa.PublicKey;
import rsa.RSAUtil;
import sun.misc.BASE64Encoder;

public class p2pMessageThread implements Runnable {
	byte[] data;
	InetAddress address;
	int port;
	String user_sendfrom;
	byte cmd;
	int len;
	String key=null;
	DatagramSocket socket=null;
	DatagramPacket packet=null;
	byte[] bufferedarray;
	String path="E:\\";
	String ip;
	public p2pMessageThread(){}
	public p2pMessageThread(byte[] data){
		this.data=data;
		String[] message=new String(new DES().decrypt(data, ClientDemo.getPasswd())).trim().split(" ");
		user_sendfrom=message[0];
		try {
			this.address=InetAddress.getByName(message[1]);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port=Integer.parseInt(message[2]);
		key=message[3];
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!authentication()){
			socket.close();
			return;
		}
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
				System.out.println("关闭p2p连接");
				socket.close();
				return;//关闭连接
			}else if(cmd==0x42){
				data=Arrays.copyOfRange(packet.getData(),2, packet.getLength());
				receiveMessage();//接收消息
			}else if(cmd==0x43){
				//接收文件
				data=Arrays.copyOfRange(packet.getData(),2, packet.getLength());
				System.out.println(new String(new DES().decrypt(data, key)));
			//	receiveFile();
				new receiveFileThread().start();
			}
		}
	}
	
	
	class receiveFileThread extends Thread{
		public void run(){
			String[] message = new String(new DES().decrypt(data, key)).trim().split("#");
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
				
				int readsize=0;
				byte[] content = null;
				byte[] temp;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while(count<length){
					try {
						readsize = input.read(buffer);
						count+=readsize;
						System.out.println(readsize+"總長:"+count);
						out.write(buffer,0,readsize);
						System.out.println(out.size());
						if(out.size()>=1024){
						temp = out.toByteArray();

						content = new DES().decrypt(Arrays.copyOfRange(out.toByteArray(), 0, 1024), key);
						out.reset();

						if (temp.length > 1024) {
							out.write(Arrays.copyOfRange(temp, 1024, temp.length));
						}
						fos.write(content, 0, content.length);
						fos.flush();
						}
						if(count>=length){
							content=new DES().decrypt(out.toByteArray(), key);
							content=Arrays.copyOfRange(content, 0, out.size()-(count-length));
							fos.write(content, 0, content.length);
							fos.flush();
						}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
			/*	int readsize;
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
				}*/
				
				
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
	
	
	
	/*private void receiveFile() {
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
	/*	String m=message[0]+" "+message[1]+"\n"+"发送文件:"+message[2]+"	大小为:"+length+"bytes\n";
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
				System.out.println("第"+n+": "+packet.getLength());
				fos.write(packet.getData(), 0,packet.getLength());
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	/*	int count=0;
		while(count<length){
				packet=new DatagramPacket(bufferedarray,bufferedarray.length);
				try {
					socket.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count+=packet.getLength();
				System.out.println(packet.getLength()+"總長:"+count);	
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
		String m=message[0]+" "+message[1]+"\n"+"发送文件:"+message[2]+"	大小为:"+length+"bytes\n";
		StringBuffer bf=ClientDemo.getMap().get(user_sendfrom);
		bf.append(m);
		ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals(user_sendfrom)){
				p2p.ta.setText(bf.toString());
			}
		}
	}*/
	
	private void receiveMessage() {
		// TODO Auto-generated method stub
		String message=new String(new DES().decrypt(data, key));
		System.out.println("接收消息为："+message);
		StringBuffer bf=ClientDemo.getMap().get(user_sendfrom);
		bf.append(message);
		ArrayList<p2pThread> p2pthreads=ClientDemo.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals(user_sendfrom)){
				p2p.ta.setText(bf.toString());
			}
		}
	}
	
	private Boolean authentication() {
		// TODO Auto-generated method stub
		cmd=0x41;
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
    			MessageTran mes=new MessageTran(cmd,m.toByteArray());
    			bufferedarray=mes.getDataTran();
    		} catch (IOException | ClassNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
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
		return receiveACK();
	}
	
	private Boolean receiveACK() {
		// TODO Auto-generated method stub
		boolean flag=false;
		byte[] buffer=new byte[1024];
		try {
			packet=new DatagramPacket(buffer,buffer.length);
			socket.receive(packet);
			byte[] data=Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
			System.out.println(new String(data));
			if(new String(data).equals("NOTACK")){
				System.out.println("认证失败");
				flag=false;
				return flag;
			}
			//RSA认证
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(user_sendfrom+"_PublicKey.dat"));
			KEY key=(PublicKey)ois.readObject();
			flag=RSAUtil.verify(user_sendfrom, new BigInteger(data), key);
			sendACK(flag);
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
		return flag;
	}
	
	private void sendACK(boolean flag) {
		// TODO Auto-generated method stub
		String message;
		if(flag==true){
			message="ACK";
		}else{
			message="NOTACK";
		}
		packet=new DatagramPacket(message.getBytes(),message.getBytes().length,address,port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
