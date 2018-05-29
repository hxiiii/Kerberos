package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import des.DES;

public class fileServerThread extends Thread{
		ServerSocket serverSocket;
		Socket socket=null;
		Boolean done=false;
		File file;
		String key;
		//long length;
		ArrayList<fileRunnable> filethreads=new ArrayList<fileRunnable>();
		public fileServerThread(ServerSocket serverSocket, File file, String key) {
			this.serverSocket=serverSocket;
			this.file=file;
			this.key=key;
		//	this.length=file.length();
		}

	//如果socket不同则启用新线程，否则端口占用
		public void run(){
			int i=0;
			while(!done){
				try {
				//	if(i==ClientDemo.getMap().size()-1)break;
					socket=serverSocket.accept();
					System.out.println("客户端已连接"+i);
					fileRunnable fileThread=new fileRunnable(socket,++i);
					new Thread(fileThread).start();
					filethreads.add(fileThread);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(serverSocket!=null){
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		class fileRunnable implements Runnable{
			Socket socket;
			int num;
			DataOutputStream output=null;
			byte[] buffer=new byte[1024];
			public fileRunnable(){}
			public fileRunnable(Socket socket, int i){
				this.socket=socket;
				this.num=i;
				try {
					output=new DataOutputStream(socket.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void run() {
				System.out.println(num);
				if(num<ClientDemo.getMap().size()-1)return;
				FileInputStream fis = null;
				try {
					fis=new FileInputStream(file);
					System.out.println(fis.available());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			byte[] content;
			int readsize = 0;
			while (true) {
				try {
					readsize = fis.read(buffer, 0, buffer.length);
					System.out.println(readsize);
					if (readsize == -1)
						break;
					for (int i = 0; i < filethreads.size(); i++) {

						try {
							// content=Arrays.copyOfRange(buffer, 0, readsize);
							content = new DES().encrypt(Arrays.copyOfRange(buffer, 0, readsize), key);
							filethreads.get(i).getOutputStream().write(content, 0, content.length);
							// System.out.println(new
							// String(DES.encrypt(content, key)));
							filethreads.get(i).getOutputStream().flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				if (socket != null) {
					socket.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				 done=true;
			}
		
			/*	while(true){
					try {
						len = input.read(buffer);
						if(len==-1)break;
						count+=len;
						System.out.println(len+"總長:"+count);	
						for (int i = 0; i < threads.size(); i++) {
							if (threads.get(i)!=this) {
								try {
									threads.get(i).getOutputStream().write(buffer, 0, len);
									threads.get(i).getOutputStream().flush();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("客户端异常退出！！");
						break;
					}
				}
				
			
		}*/
			private DataOutputStream getOutputStream() {
				// TODO Auto-generated method stub
				return output;
			}
		}
}
		
