package messageTran;

import java.util.Arrays;

public class MessageTran {
	private byte cmd;
	private byte length;
	private byte[] data;
	private byte[] dataTran;
	public MessageTran(){}
	public MessageTran(byte cmd){
		this.cmd=cmd;
		length=(byte)0;
		dataTran=new byte[2];
		dataTran[0]=cmd;
		dataTran[1]=length;
	}
	
	public MessageTran(byte cmd,byte[] data){
		this.cmd=cmd;
		this.data=data;
		length=(byte)data.length;
		dataTran=new byte[data.length+2];
		dataTran[0]=cmd;
		dataTran[1]=length;
		System.arraycopy(data, 0, dataTran, 2, data.length);//合并byte数组
	}
	
	public byte[] getDataTran(){
		return dataTran;
	}
	
	public static void main(String[] args) {
		byte cmd=0x31;
		byte[] data="123 46".getBytes();
		MessageTran m=new MessageTran(cmd,data);
		byte t[]=m.getDataTran();
		byte d[]=Arrays.copyOfRange(t, 2, t.length);
		if(t[0]==0x31)
		System.out.println(t[0]+" "+t[1]);
		System.out.println(new String(d)+"  ling");
	}
}
