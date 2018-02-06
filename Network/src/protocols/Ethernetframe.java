package protocols;

import java.nio.ByteBuffer;


public class Ethernetframe {
	private String preamble;
	private String SFD;
	private Ethernetheader eHeader;
	private byte[] data;
	private int CRC;
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public Ethernetframe(byte[] destinationMAC, byte[] sourceMAC,
            int ethertype, String str) {
		preamble = "5555555";//hex of 1010...., 7 bytes
		SFD = "57";//hex of 10101011, 1 bytes
		eHeader = new Ethernetheader(destinationMAC, sourceMAC, ethertype);
		data = str.getBytes();
		CRC = CRC32.crc32(str);
	}
	
	public String toString() {
		return preamble+SFD+bytesToHex(eHeader.destinationMAC)+
				bytesToHex(eHeader.sourceMAC)+
				bytesToHex(ByteBuffer.allocate(4).putInt(eHeader.ethertype).array())+
				bytesToHex(data)+
				bytesToHex(ByteBuffer.allocate(4).putInt(CRC).array());
	}
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}

