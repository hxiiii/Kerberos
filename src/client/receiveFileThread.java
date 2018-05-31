package client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import des.DES;

public class receiveFileThread extends Thread{
	private byte[] data;
	private String key;
	String path="E:\\";
	String user_sendfrom;
	String address;
	public receiveFileThread(){}
	
	public receiveFileThread(byte[] data,String key,String user_sendfrom,InetAddress address){
		this.data=data;
		this.key=key;
		this.user_sendfrom=user_sendfrom;
		this.address=address.getHostAddress();
	}
	
	public receiveFileThread(byte[] data){
		this.data=data;
		this.user_sendfrom="All Online Users";
		this.key=Client.getPasswd();
	}
	
	public void run(){
		String[] message = new String(new DES().decrypt(data, key)).trim().split("#");
		int port;
		if(user_sendfrom.equals("All Online Users")){
			key=message[4];
			address=message[6];
			port=Integer.parseInt(message[5]);
		}else{
			port=Integer.parseInt(message[4]);
		}
		File file = new File(path + message[2]);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int length = Integer.parseInt(message[3]);
		String m=message[0]+"	"+message[1]+"\n"+"发送文件:"+message[2]+"		大小为:"+length+"bytes\n";
		StringBuffer bf=Client.getMap().get(user_sendfrom);
		bf.append(m);
		ArrayList<p2pThread> p2pthreads=Client.getp2pThreads();
		for(p2pThread p2p:p2pthreads){
			if(p2p.user_sendfor.equals(user_sendfrom)){
				p2p.demo.ta.setText(bf.toString());
				break;
			}
		}
		Socket socket=null;
		try {
			System.out.println("socket");
			socket = new Socket(address,port);
			DataInputStream input=new DataInputStream(socket.getInputStream());
			int count=0;
			byte[] buffer=new byte[1024];
			System.out.println("socket");	
			int readsize=0;
			byte[] content = null;
			byte[] temp;
			DES des=new DES();
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

					content = des.decrypt(Arrays.copyOfRange(out.toByteArray(), 0, 1024), key);
					out.reset();

					if (temp.length > 1024) {
						out.write(Arrays.copyOfRange(temp, 1024, temp.length));
					}
					fos.write(content, 0, content.length);
					fos.flush();
					}
					if(count>=length){
						content=des.decrypt(out.toByteArray(), key);
						content=Arrays.copyOfRange(content, 0, out.size()-(count-length));
						fos.write(content, 0, content.length);
						fos.flush();
					}
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