package net.anfet.simple.support.library.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class Bytes {

	private static final Inflater inflater = new Inflater();

	final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
	final private static CRC32 crc32 = new CRC32();

	public static String toHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String readableHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 3];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = ' ';
		}
		return new String(hexChars).trim();
	}

	synchronized public static long crc(byte[] payload) {
		crc32.reset();
		crc32.update(payload);
		return crc32.getValue();
	}

	public static void compactByteBufferAt(ByteBuffer buffer, int position) {
		int current_position = buffer.position();
		if (position > current_position)
			throw new IllegalArgumentException("compact position > buffers current position");

		buffer.position(position);
		buffer.compact();
		buffer.position(current_position - position);
	}

	public static ByteBuffer sliceCompatByteBuffer(ByteBuffer buffer, int position) {
		ByteBuffer slice = ByteBuffer.allocate(position);
		slice.put(buffer.array(), 0, position);
		slice.rewind();
		compactByteBufferAt(buffer, position);
		return slice;
	}

	public static String getString(DataInputStream dis) throws IOException {
		InputStreamReader isr = new InputStreamReader(dis, "UTF-8");
		StringWriter sw = new StringWriter();
		char[] buf = new char[1024];

		int read = -1;
		while (-1 != (read = isr.read(buf))) {
			sw.write(buf, 0, read);
		}
		return sw.toString();

	}

	public static byte[] getBytes(DataInputStream dis) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b = 0;
		while ((b = dis.read()) != -1)
			baos.write(b);
		return baos.toByteArray();
	}

	public static byte[] decompress(final byte[] bytes) throws DataFormatException {

		byte[] buffer = new byte[128];
		ByteArrayOutputStream dis = new ByteArrayOutputStream();
		try {

			inflater.setInput(bytes);
			while (!inflater.needsInput()) {
				int numDecompressed = inflater.inflate(buffer);
				dis.write(buffer, 0, numDecompressed);
			}

			return dis.toByteArray();
		} finally {
			inflater.reset();
			try {
				dis.close();
			} catch (IOException ignored) {

			}
		}

	}

	public static String formatBytes(long bytes) {
		if (bytes < 1024) {
			return "" + bytes;
		} else if (bytes < 1024 * 1024) {
			return (bytes / 1024) + "KB";
		} else if (bytes < 1024 * 1024 * 1024) {
			return (bytes / (1024 * 1024)) + "MB";
		}

		return bytes / (1024 * 1024 * 1024) + "GB";
	}

}
