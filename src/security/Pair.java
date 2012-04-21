package security;

import java.io.Serializable;

public class Pair implements Serializable{
	private byte[] hash = null;
	private byte[] salt = null;
	
	/**
	 * Class to hold a user's hashed password and the unique salt that corresponds
	 * to said user. Replaced the storing of actual passwords to be more secure.
	 * @param hash
	 * @param salt
	 */
	public Pair(byte[] hash, byte[] salt){
		this.hash = hash;
		this.salt = salt;
	}


	public byte[] getHash() {
		return hash;
	}


	public void setHash(byte[] hash) {
		this.hash = hash;
	}


	public byte[] getSalt() {
		return salt;
	}


	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	
}
