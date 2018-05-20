package login;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

import messageTran.MessageTran;

public class Login {
	private static Socket socket_AS;
	private static Socket socket_TGS;
	private static Socket socket_SS;
	private static int port_AS=6666;
	private static int port_TGS=7777;
	private static int port_SS=8888;
	private static String ip_AS="192.168.1.106";
	private static String ip_TGS;
	private static String ip_SS;
	private static DataInputStream input;
	private static DataOutputStream  output;
	public Login(){}
	public static boolean login(String user,String password) throws IOException{
		try{
			System.out.println("dasd");
			socket_AS=new Socket(ip_AS,port_AS);	
			input=new DataInputStream(socket_AS.getInputStream());
			output=new DataOutputStream(socket_AS.getOutputStream());
			byte[] dataTran=sendMessage(user);	
			output.write(dataTran);
			output.flush();
			byte[] buffer=new byte[1024];
			int len=input.read(buffer);	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(input!=null){
				input.close();
			}
			if(output!=null){
				output.close();
			}
			if(socket_AS!=null){
				socket_AS.close();
			}
		}
		return true;
	}
	private static byte[] sendMessage(String user) {
		// TODO Auto-generated method stub
		byte cmd=0x01;
		long t=new Date().getTime();
		String time=String.valueOf(t);
		String IDv="1";
		String data=user+" "+IDv+" "+t;
		MessageTran m=new MessageTran(cmd,data.getBytes());
		return m.getDataTran();
	}
	
/*	public static String Byte2Hex(byte[] bytes){
		if(bytes.length>7){
			bytes=Arrays.copyOfRange(bytes, 0, 7);
		}
		 StringBuilder buf = new StringBuilder(bytes.length * 2);
	        for(byte b : bytes) { // 使用String的format方法进行转换
	            buf.append(String.format("%02x", new Integer(b & 0xff)));
	        }
	        return buf.toString();

	}*/

public static void main(String[] args) throws IOException {
	Login.login("hxii","12334");
	//System.out.println(Login.Byte2Hex("123354".getBytes()));
	System.out.println(new Date().getTime());
	System.out.println(System.currentTimeMillis());
}
}
