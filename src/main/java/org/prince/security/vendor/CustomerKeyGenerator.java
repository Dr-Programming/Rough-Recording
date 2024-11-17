// This class is for vendor purpose only.
// And will NOT be packaged in the final application which will be shared with the public.

package org.prince.security.vendor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class CustomerKeyGenerator {
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Customer Key Pair Generator.......");
		
		System.out.print("Enter Machine ID : ");
		String passphrase = sc.nextLine();
		
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			System.out.print("Enter location to save the Customer Public Key : ");
			String customerPublicKey = sc.nextLine();
			
			Files.write(Paths.get(customerPublicKey+"\\customerPublicKey.key"), publicKey.getEncoded());
			
			byte[] privateKeyBytes = privateKey.getEncoded();
			
			PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
			Cipher pbeCipher = Cipher.getInstance("PBEWithSHA1AndDESede");
			
			byte[] salt = "DHy(knh(iUlEe*mL".getBytes();
			int iterationCount = 1000;
			
			PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterationCount);
			
			pbeCipher.init(Cipher.ENCRYPT_MODE, secretKeyFactory.generateSecret(pbeKeySpec), parameterSpec);
			
			byte[] encryptedPrivateKeyBytes = pbeCipher.doFinal(privateKeyBytes);
			
			EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(pbeCipher.getParameters(), encryptedPrivateKeyBytes);
			
			System.out.print("Enter location to save the Customer Private Key : ");
			String customerPrivateKey = sc.nextLine();
			
			Files.write(Paths.get(customerPrivateKey+"\\customerPrivateKey.key"), encryptedPrivateKeyInfo.getEncoded());
			
			System.out.println("\n\nFiles generated at mentioned loaction.");
			sc.close();
			
		} catch (NoSuchAlgorithmException | IOException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
	}

}
