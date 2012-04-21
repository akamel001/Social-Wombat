package util;

import java.io.Serializable;

public class DataObject implements Serializable{
	private static final long serialVersionUID = 1L;
	
	Object data;
	String checksum;
	
	public DataObject(Object data, String checksum){
		this.data = data;
		this.checksum = checksum;
	}

	public Object getData() {
		return data;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	
}
