package regist;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Regist {
	private static String ip;
	private static int port;
	private static Socket socket;
	private static DataInputStream input;
	private static DataOutputStream  output;
	public Regist(){}
	
	public static boolean regist(String user,String password,String mail) throws IOException{
		try{
			socket=new Socket(ip,port);
			input=new DataInputStream(socket.getInputStream());
			output=new DataOutputStream(socket.getOutputStream());
			sendMessage(user,password,mail);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(input!=null){
				input.close();
			}
			if(output!=null){
				output.close();
			}
			if(socket!=null){
				socket.close();
			}
		}
		return true;
	}
	
	
	private static void sendMessage(String user, String password, String mail) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
