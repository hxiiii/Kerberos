package client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import des.DES;

class sendFileThread extends Thread {
	private ServerSocket server;
	private File file;
	private String key;
	public sendFileThread(ServerSocket serverSocket,File file,String key){
		this.server=serverSocket;
		this.file=file;
		this.key=key;
	}
	public void run() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
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
			/*
			 * int readsize = 0; // for(int i=0;i<block;i++) while (true) {
			 * readsize = fis.read(buffer, 0, buffer.length); if (readsize
			 * == -1) break; out.write(buffer, 0, readsize);
			 * System.out.println(readsize); out.flush(); }
			 */
			byte[] content;
			int readsize = 0;
			DES des=new DES();
			while (true) {
				try {
					readsize = fis.read(buffer, 0, buffer.length);
					System.out.println(readsize);
					if (readsize == -1)
						break;
					content = des.encrypt(Arrays.copyOfRange(buffer, 0, readsize), key);
					out.write(content, 0, content.length);
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (socket != null) {
				socket.close();
			}
			if(fis!=null){
				fis.close();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
