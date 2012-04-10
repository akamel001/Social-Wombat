package security;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * A class with a single static 
 * @author chris d
 *
 */
public final class CheckSum {

	/**
	 * Constructor is private to disallow instantiation.
	 */
	private CheckSum(){}
	
	/**
	 * Give the checksum for a passed object
	 * @param o The object to be checksumed. NOTE: This object must be completely serializable!!!!
	 * @return Returns the checksum on success, -1 if the object is not serializable.
	 */
	public static long getChecksum(Object o){
		long out = -1;
		
		ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
		ObjectOutputStream object_out;
		
		try {
			object_out = new ObjectOutputStream(byte_out);
			object_out.writeObject(o);
			byte[] bytes = byte_out.toByteArray();
			Checksum checksum = new CRC32();
			checksum.update(bytes,0,bytes.length);
			out = checksum.getValue();
		} catch (IOException e) {
			//e.printStackTrace();
			return -1;
		}
		return out;
	}
	
}
