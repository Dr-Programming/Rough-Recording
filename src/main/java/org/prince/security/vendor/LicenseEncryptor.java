// This class is for vendor purpose only.
// And will NOT be packaged in the final application which will be shared with the public.

package org.prince.security.vendor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LicenseEncryptor {
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("license Encryptor....");
		
		try {
			System.out.print("Enter Location of Customer Public Key : ");
			String customerPublicKeyPath = sc.nextLine();
			
			byte[] customerPublicKeyBytes = Files.readAllBytes(Paths.get(customerPublicKeyPath));
			PublicKey customerPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(customerPublicKeyBytes));
			
			System.out.print("Enter Location of License File : ");
			String licensePath = sc.nextLine();
			byte[] licenseData = Files.readAllBytes(Paths.get(licensePath));
			
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			
			SecretKey aesKey = keyGen.generateKey();
			
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			
			byte[] encryptedLicenseData = aesCipher.doFinal(licenseData);
			
			Cipher rsaCipher = Cipher.getInstance("RSA");
			rsaCipher.init(Cipher.ENCRYPT_MODE, customerPublicKey);
			
			byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
			
			System.out.print("Enter Location to Save Encrypted License : ");
			String encryptedLicensePath = sc.nextLine();
			
			System.out.print("Enter Location to Save Encrypted AES Key : ");
			String encryptedAesKeyPath = sc.nextLine();
			
			Files.write(Paths.get(encryptedLicensePath+"\\license.enc"), Base64.getEncoder().encode(encryptedLicenseData));
			Files.write(Paths.get(encryptedAesKeyPath+"\\AesKey.key"), Base64.getEncoder().encode(encryptedAesKey));
			
			System.out.println("\n\nFiles generated at mentioned loaction.");
			sc.close();
			
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
	}
}
