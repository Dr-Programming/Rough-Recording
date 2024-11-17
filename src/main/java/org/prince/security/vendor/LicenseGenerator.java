// This class is for vendor purpose only.
// And will NOT be packaged in the final application which will be shared with the public.

package org.prince.security.vendor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class LicenseGenerator {
	
	private static byte[] signData(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(data.getBytes());
		return signature.sign();
	}
	
	private static void generateLicense(String machineID, String privateKeyPath, String licenseFilePath) {
		
		try {
			byte[] privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
			PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
			
			byte[] signature = signData(machineID, privateKey);
			
			String licenseContent = machineID + ":" + Base64.getEncoder().encodeToString(signature);
			Files.write(Paths.get(licenseFilePath), licenseContent.getBytes());
			
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("License Generator.......\n");
		
		System.out.print("Enter Machine ID : ");
		String machineID = sc.nextLine();
		
		System.out.println("\nEnter Private Key Path : ");
		String privateKeyPath = sc.nextLine();
		
		System.out.println("\nEnter Location to save the License File : ");
		String licenseFilePath = sc.nextLine();
		
		generateLicense(machineID, privateKeyPath, licenseFilePath+"\\license.lic");
		System.out.println("License Generated.");
		sc.close();
	}
}
