package rsa;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import sun.misc.BASE64Encoder;

public class RSAUtil {
	public static BigInteger encrypt(KEY key,byte[] plaint){
		BigInteger m=new BigInteger(1,plaint);///注意。。。。!!!!!!!!!
		BigInteger c=m.modPow(key.getE(), key.getN());
		return c;
	}
	
	public static byte[] decrypt(KEY key,BigInteger c){
		BigInteger m=c.modPow(key.getE(), key.getN());
		byte[] cryptedData=m.toByteArray();
		//cryptedData=Arrays.copyOfRange(cryptedData, 1, cryptedData.length);
		return cryptedData;
	}
	
	public static boolean verify(String s, BigInteger b,KEY key) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		 MessageDigest md = MessageDigest.getInstance("MD5");
         // 获得数据的数据指纹
         byte[] digest = md.digest(s.getBytes());
         BASE64Encoder encoder = new BASE64Encoder();
         System.out.println("hm="+encoder.encode(digest));
         byte[] hm=decrypt(key,b);
         if(hm.length==17)hm=Arrays.copyOfRange(hm, 1, hm.length);
         System.out.println(hm.length+new String(hm));
         System.out.println("v="+encoder.encode(hm));
         if(encoder.encode(digest).equals(encoder.encode(hm)))return true;
         //if(digest.equals(hm))return true;
		 return false;
	}

	public static void main(String[] args) {
		String data = "hello world!hhh我是我爱     放水电费水电费第三方           大姐夫莱克斯顿返回计算 ";
		ObjectInputStream ois1;
		ObjectInputStream ois2;
		try {
			ois1 = new ObjectInputStream(new FileInputStream("PublicKey.dat"));
			KEY key1 = (PublicKey) ois1.readObject();
			ois1.close();
			ois2 = new ObjectInputStream(new FileInputStream("PrivateKey.dat"));
			KEY key2 = (PrivateKey) ois2.readObject();
			ois2.close();
			BigInteger m = RSAUtil.encrypt(key2, data.getBytes());
			System.out.println(new String(RSAUtil.decrypt(key1, m)));
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
