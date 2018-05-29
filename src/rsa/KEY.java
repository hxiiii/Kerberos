package rsa;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class KEY implements Serializable{
	public abstract BigInteger getE();

	public abstract BigInteger getN();

}
