package des;

import java.util.Arrays;

public class DES {
	private static int plaintext[];
	private static int ciphertext[];
	private static int key[];
	private static int le[], re[], c[], d[];
	private static int rep[], rsp[], keychild[][];
	private static int ip[] = { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22,
			14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53,
			45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };
	private static int ip_1[] = { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22,
			62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2,
			42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25 };
	private static int p[] = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9,
			19, 13, 30, 6, 22, 11, 4, 25

	};
	private static int pc1[] = { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19,
			11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21,
			13, 5, 28, 20, 12, 4 };
	private static int pc2[] = { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
			41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32 };
	private static int ep[] = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16,
			17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1 };
	private static int s1[][] = { { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
			{ 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
			{ 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
			{ 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }, };
	private static int s2[][] = { { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
			{ 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
			{ 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
			{ 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }, };
	private static int s3[][] = { { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
			{ 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
			{ 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
			{ 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 },

	};
	private static int s4[][] = { { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
			{ 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
			{ 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
			{ 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }, };
	private static int s5[][] = { { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
			{ 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
			{ 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
			{ 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }, };
	private static int s6[][] = { { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
			{ 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
			{ 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
			{ 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 },

	};
	private static int s7[][] = { { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
			{ 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
			{ 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
			{ 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 },

	};
	private static int s8[][] = { { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
			{ 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
			{ 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
			{ 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 }, };
	private static int s[][][] = { s1, s2, s3, s4, s5, s6, s7, s8 };
	private static int keyshift[] = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };
	private static int count = 0;

	private static void init() {
		plaintext = new int[64];
		ciphertext = new int[64];
		key = new int[56];
		le = new int[32];
		re = new int[32];
		c = new int[28];
		d = new int[28];
		rep = new int[48];
		rsp = new int[32];
		keychild = new int[16][48];
	}

	public static byte[] encrypt(byte[] by,String key){
		init();
		getBinaryKey(key);
		StringBuilder s = new StringBuilder();
		int length = by.length / 8 + ((by.length % 8 == 0) ? 0 : 1);
		byte[] result = new byte[length * 8];
		for (int n = 0; n < length; n++) {
			count = 0;
			for (int a = 0; a < 64; a++) {
				plaintext[a] = 0;
			}
			for (int j = 0; n * 8 + j < by.length && j < 8; j++) {
				byte b = by[n * 8 + j];
				String bs = byteToBit(b);
				for (int m = 0; m < 8; m++) {
					plaintext[j * 8 + m] = Integer.parseInt(String.valueOf(bs.charAt(m)));
				}
			}
			beginPermutation();
			if (n == 0)
				getKeychild();
			for (int i = 0; i < 16; i++) {
				extend("加密");
				substitution();
				exchange();
			}
			endPermutation();
			s.delete(0, s.length());
			for (int i = 0; i < 64; i++) {
				s.append(String.valueOf(ciphertext[i]));
			}
			for (int i = 0; i < s.length() / 8; i++) {
				String str = s.toString().substring(i * 8, i * 8 + 8);
				int ss = Integer.parseInt(str, 2);
				result[n * 8 + i] = (byte) ss;
			}
		}
		return result;
	}
	
	public static byte[] encrypt(String M, String key) {
		// TODO Auto-generated method stub
		byte[] by = M.getBytes();
		return encrypt(by,key);
	}

	public static byte[] decrypt(byte[] C,String key){
		init();
		getBinaryKey(key);
		StringBuilder s = new StringBuilder();
		byte[] by = new byte[C.length];
		for (int n = 0; n < C.length / 8; n++) {
			count = 0;
			for (int a = 0; a < 64; a++) {
				plaintext[a] = 0;
			}
			for (int j = 0; j < 8; j++) {
				byte b = C[n * 8 + j];
				String bs = byteToBit(b);
				for (int m = 0; m < 8; m++) {
					plaintext[j * 8 + m] = Integer.parseInt(String.valueOf(bs.charAt(m)));
				}
			}
			beginPermutation();
			if (n == 0) {
				getKeychild();
			}
			for (int i = 0; i < 16; i++) {
				extend("解密");
				substitution();
				exchange();
			}
			endPermutation();
			s.delete(0, s.length());
			for (int i = 0; i < 64; i++) {
				s.append(String.valueOf(ciphertext[i]));
			}
			byte[] t = new byte[8];
			t = BinaryStringToByteArray(s.toString());
			for (int i = 0; i < 8; i++) {
				by[n * 8 + i] = t[i];
			}
		}
		return by;
	}
	
	public static String decryptStr(byte[] C, String key) {
		// TODO Auto-generated method stub
		byte[] by=decrypt(C,key);
		return new String(by);
	}

	private static String byteToBit(byte b) {
		return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1)
				+ (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 1) & 0x1)
				+ (byte) ((b >> 0) & 0x1);
	}

	private static byte[] BinaryStringToByteArray(String binaryString) {
		byte[] buffer = new byte[binaryString.length() / 8];
		for (int i = 0; i < buffer.length; i++) {
			String str = binaryString.substring(i * 8, i * 8 + 8);
			buffer[i] = ((byte) Integer.parseInt(str, 2));
		}
		return buffer;
	}

	private static void getBinaryKey(String K) {
		// TODO Auto-generated method stub
		String k = "";
		for (int i = 0; i < 56; i++)
			key[i] = 0;
		K=Byte2Hex(K.getBytes());
		for (int i = 0; i < K.length(); i++) {
			String str = K.substring(i, i + 1);
			int ss = Integer.parseInt(str, 16);
			System.out.println(ss);
			String binary = "0000" + Integer.toBinaryString(ss);
			k += binary.substring(binary.length() - 4);
		}
		for (int i = 0; i < k.length(); i++) {
			key[i] = Integer.parseInt(String.valueOf(k.charAt(i)));
			System.out.print(key[i]);
		}
		System.out.println();
	}

	private static void getKeychild() {
		// TODO Auto-generated method stub
		for (int i = 0; i < pc1.length; i++) {
			if (i < 28)
				c[i] = key[pc1[i] - pc1[i] / 8 - 1];
			else
				d[i - 28] = key[pc1[i] - pc1[i] / 8 - 1];
		}
		for (int n = 0; n < 16; n++) {
			int shift = keyshift[n];
			int temp1[] = new int[28];
			int temp2[] = new int[28];
			for (int i = 0; i < 28; i++) {
				temp1[i] = c[i];
				temp2[i] = d[i];
			}
			// temp1=c;temp2=d;
			for (int i = 0; i < 28; i++) {
				c[i] = temp1[(i + shift) % 28];
				d[i] = temp2[(i + shift) % 28];
			}
			for (int i = 0; i < 56; i++) {
				if (i < 28)
					key[i] = c[i];
				else
					key[i] = d[i - 28];
			}
			for (int i = 0; i < 48; i++) {
				keychild[n][i] = key[pc2[i] - 1];
			}
		}
	}
	
	private static String Byte2Hex(byte[] bytes){
		if(bytes.length>7){
			bytes=Arrays.copyOfRange(bytes, 0, 7);
		}
		 StringBuilder buf = new StringBuilder(bytes.length * 2);
	        for(byte b : bytes) { // 使用String的format方法进行转换
	            buf.append(String.format("%02x", new Integer(b & 0xff)));
	        }
	        return buf.toString();
	}
	
	private static void endPermutation() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 64; i++) {
			if (i < 32)
				plaintext[i] = le[i];
			else
				plaintext[i] = re[i - 32];
		}
		for (int i = 0; i < 64; i++) {
			ciphertext[i] = plaintext[ip_1[i] - 1];
		}
	}

	private static void beginPermutation() {
		// TODO Auto-generated method stub
		for (int i = 0; i < ip.length; i++) {
			if (i < 32)
				le[i] = plaintext[ip[i] - 1];
			else
				re[i - 32] = plaintext[ip[i] - 1];
		}
	}

	private static void extend(String s) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 48; i++) {
			rep[i] = re[ep[i] - 1];
		}
		if (s.equals("加密")) {
			for (int i = 0; i < 48; i++) {
				rep[i] = rep[i] ^ keychild[count][i];
			}
		} else if (s.equals("解密")) {
			for (int i = 0; i < 48; i++) {
				rep[i] = rep[i] ^ keychild[15 - count][i];
			}
		}
		count++;
	}

	private static void substitution() {
		// TODO Auto-generated method stub
		int h = 0, l = 0;
		int value;
		int a[] = new int[2];
		int b[] = new int[4];
		for (int i = 0; i < 32; i++) {
			rsp[i] = 0;
		}
		for (int i = 0; i < 48; i = i + 6) {
			a[0] = rep[i];
			a[1] = rep[i + 5];
			b[0] = rep[i + 1];
			b[1] = rep[i + 2];
			b[2] = rep[i + 3];
			b[3] = rep[i + 4];
			h = 2 * a[0] + a[1];
			l = 8 * b[0] + 4 * b[1] + 2 * b[2] + b[3];
			value = s[i / 6][h][l];
			int j = 3;
			while (value != 0) {
				rsp[(i / 6) * 4 + j] = value % 2;
				value = value / 2;
				j--;
			}
		}
		int temp[] = new int[32];
		for (int i = 0; i < 32; i++) {
			temp[i] = rsp[i];
		}
		for (int i = 0; i < 32; i++) {
			rsp[i] = temp[p[i] - 1];
		}
	}

	private static void exchange() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 32; i++) {
			le[i] = le[i] ^ rsp[i];
		}
		if (count != 16) {
			int temp[] = new int[32];
			for (int i = 0; i < 32; i++) {
				temp[i] = re[i];
			}
			re = le;
			le = temp;
		}
	}
public static void main(String[] args) {
	byte[] c=DES.encrypt("我是中国人4     dfds fdgdf      df           54545  ", "12dfsd3,.,/");
	String m=DES.decryptStr(c, "12dfsd3,.,/");
	System.out.println(m);
}
}
