package rsa;

import java.math.BigInteger;

public class PrivateKey extends KEY{
	public BigInteger d;

	public BigInteger n;
	    
	    public PrivateKey(BigInteger d,BigInteger n){
	        this.d=d;
	        this.n=n;
	    }

	    public BigInteger getE() {
	        return d;
	    }

	    public BigInteger getN() {
	        return n;
	    }
}
