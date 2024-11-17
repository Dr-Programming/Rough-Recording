// This class is for vendor purpose only.
// And will NOT be packaged in the final application which will be shared with the public.

package org.prince.security.vendor;

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

	protected static String getHardwareFingerprint() {
		
		try {
			MAC_ADDRESS = getMacAddress();
			if(MAC_ADDRESS != null) {
				System.out.println("MAC ADDRESS : " + MAC_ADDRESS);
			}else {
				System.out.println("Error Fetching the MAC Address.");
				return null;
			}
			
			PROCESSOR_ID = getProcessorId();
			if(PROCESSOR_ID != null) {
				System.out.println("PROCESSOR ID : " + PROCESSOR_ID);
			}else {
				System.out.println("Error Fetching the Processor ID.");
				return null;
			}
			
			String info = MAC_ADDRESS + PROCESSOR_ID;
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(info.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for(byte b : hash) {
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
			
		} catch (SocketException | NoSuchAlgorithmException e) {
			System.out.println("Fatal Error Occured while Creating the Unique Hardware Fingerprint!");
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
	
	public static void main(String[] args) {
		System.out.println("Getting the System Information.....");
		String fingerprint = getHardwareFingerprint();
		if(fingerprint != null) {
			System.out.println("\nUnique Hardware Fingerprint for the above mentioned Information...");
			System.out.println("Hardware Fingerprint => " + fingerprint);
		}else {
			System.out.println("\nFailed to generate Unique Hardware Fingerprint.\nCannot get necessary information.");
		}
	}
}
