package rsa;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.*;
import java.security.SecureRandom;
import java.util.Date;

public class RSA {
	private static  BigInteger p;
	private static  BigInteger q;
	private static  BigInteger e;
	private static  BigInteger d;
	private static  BigInteger n;
	private static  BigInteger k;
	private static  BigInteger x;
	private static  BigInteger y;
	public static  int bitlength=512;
	private static String PUBLIC_KEY_FILE="PublicKey.dat";
	private static String PRIVATE_KEY_FILE="PrivateKey.dat";
	public RSA(){
		init();
	}
	
	private static void init(){
		SecureRandom random=new SecureRandom();
		random.setSeed(new Date().getTime());
		while(!(p=BigInteger.probablePrime(bitlength,random)).isProbablePrime(1)){
		            continue;
		        }//生成大素数p
		        System.out.println("p="+p);
		while(!(q=BigInteger.probablePrime(bitlength,random)).isProbablePrime(1)){
		            continue;
		        }//生成大素数q
		System.out.println("q="+q);
		n=p.multiply(q);//生成n
		k=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		e=BigInteger.probablePrime(bitlength-1, random);   
		while(k.mod(e).equals(BigInteger.ZERO)){
			e=BigInteger.probablePrime(bitlength-1, random); 
		}
		//e=new BigInteger("65537");
	    System.out.println("e="+e);
	    d=cal(e,k);
	    System.out.println("d="+d);
	}
	 //欧几里得扩展算法
	private static BigInteger ex_gcd(BigInteger a, BigInteger b) {
		// TODO Auto-generated method stub
		 if(b.intValue()==0){
	            x=new BigInteger("1");
	            y=new BigInteger("0");
	            return a;
	        }
	        BigInteger ans=ex_gcd(b,a.mod(b));
	        BigInteger temp=x;
	        x=y;
	        y=temp.subtract(a.divide(b).multiply(y));
	        return ans;
	}
	 //求反模 
	 private static  BigInteger cal(BigInteger e,BigInteger k){
        BigInteger gcd=ex_gcd(e,k);
        if(BigInteger.ONE.mod(gcd).intValue()!=0){
            return new BigInteger("-1");
        }
        x=x.multiply(BigInteger.ONE.divide(gcd));
        k=k.abs();
        BigInteger ans=x.mod(k);
        if(ans.compareTo(BigInteger.ZERO)<0) ans=ans.add(k);
        return ans;  
    }
	
	public static void generateKey() {
		init();
		rsa.PublicKey publicKey = new rsa.PublicKey(e, n);
		rsa.PrivateKey privateKey = new rsa.PrivateKey(d, n);
		try {
			ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
			ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
			oos1.writeObject(publicKey);
			oos2.writeObject(privateKey);
			/** 清空缓存，关闭文件输出流 */
			oos1.close();
			oos2.close();
		} catch (Exception e) {
		}
	}
	public static void main(String[] args) {
		RSA.generateKey();
	}
}
