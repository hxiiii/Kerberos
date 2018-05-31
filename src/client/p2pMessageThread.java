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
	private InetAddress address;
	private int port;
	private String user_sendfrom;
	private String key=null;
	private DatagramSocket socket=null;
	private DatagramPacket packet=null;
	public p2pMessageThread(){}
	public p2pMessageThread(byte[] data){
		String[] message=new String(new DES().decrypt(data, Client.getPasswd())).trim().split(" ");
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
		byte[] bufferedarray=new byte[1024];
		byte cmd = 0;
		byte[] data=null;
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
				receiveMessage(data);//接收消息
			}else if(cmd==0x43){
				//接收文件
				data=Arrays.copyOfRange(packet.getData(),2, packet.getLength());
				System.out.println(new String(new DES().decrypt(data, key)));
			//	receiveFile();
				new receiveFileThread(data,key,user_sendfrom,address).start();
			}
		}
	}
	
	
	private void receiveMessage(byte[] data) {
		// TODO Auto-generated method stub
		String message=new String(new DES().decrypt(data, key));
		System.out.println("接收消息为："+message);
		StringBuffer bf=Client.getMap().get(user_sendfrom);
		bf.append(message);
		ArrayList<p2pThread> p2pthreads=Client.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals(user_sendfrom)){
				p2p.demo.ta.setText(bf.toString());
				break;
			}
		}
	}
	
	private Boolean authentication() {
		// TODO Auto-generated method stub
		byte cmd=0x41;
		byte[] bufferedarray = null;
		try {
            // 获得一个指定编码的信息摘要算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 获得数据的数据指纹
            byte[] digest = md.digest(Client.user.getBytes());
            ObjectInputStream ois;
            BigInteger m ;
    		try {
    			ois = new ObjectInputStream(new FileInputStream(Client.user+"_PrivateKey.dat"));
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
