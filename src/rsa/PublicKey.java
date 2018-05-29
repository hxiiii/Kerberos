package rsa;

import java.math.BigInteger;

public class PublicKey extends KEY{
	    
	public BigInteger e;

	public BigInteger n;
	    
	    public PublicKey(BigInteger e,BigInteger n){
	        this.e=e;
	        this.n=n;
	    }

	    public BigInteger getE() {
	        return e;
	    }

	    public BigInteger getN() {
	        return n;
	    }
}
