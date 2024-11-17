package org.prince.security.client;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class HardwareFingerprint {
	
	private static String MAC_ADDRESS;
	private static String PROCESSOR_ID;

	public static String getHardwareFingerprint() {
		
		try {
			MAC_ADDRESS = getMacAddress();
			PROCESSOR_ID = getProcessorId();
			String info = MAC_ADDRESS + PROCESSOR_ID;
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(info.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for(byte b : hash) {
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
			
		} catch (SocketException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getMacAddress() throws SocketException {
		
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while(networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			byte[] mac  = networkInterface.getHardwareAddress();
			if(mac != null) {
				StringBuilder stringBuilder = new StringBuilder();
				for(byte b : mac) {
					stringBuilder.append(String.format("%02x", b));
				}
				return stringBuilder.toString();
			}
		}
		return null;
	}
	
	private static String getProcessorId() {
		SystemInfo systemInfo = new SystemInfo();
		CentralProcessor processor = systemInfo.getHardware().getProcessor();
		return processor.getProcessorIdentifier().getProcessorID();
	}
}
