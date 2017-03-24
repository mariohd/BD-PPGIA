package db;
import java.io.UnsupportedEncodingException;

public class Utils {

	public static int toInt(byte[] bytes, int offset) {
	  int ret = 0;
	  for (int i = 0; i<4 && i+offset<bytes.length; i++) {
	    ret <<= 8;
	    ret |= (int)bytes[i] & 0xFF;
	  }
	  return ret;
	}

	public static byte[] toByteArray(int in, int size) {
		byte[] bytes = new byte[size];
		
		for (int i = size - 1; i >= 0; i-- ) {
			bytes[(size - 1) - i] = (byte) (in >>> 8 * i);
		}
		
		return bytes;
	}
	
	public static byte[] toByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}

	public static String asString(byte[] b) {
		try {
			return new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}