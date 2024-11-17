// This class is for vendor purpose only.
// And will NOT be packaged in the final application which will be shared with the public.

package org.prince.security.vendor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class VendorKeyGenerator {

	private static void generateKeyPair(String publicKeyPath, String privateKeyPath) {
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(2048);
			
			KeyPair keyPair = keyGenerator.generateKeyPair();
			
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			Files.write(Paths.get(publicKeyPath), publicKey.getEncoded());
			Files.write(Paths.get(privateKeyPath), privateKey.getEncoded());
			
			System.out.println("\n\nFiles generated at mentioned loaction.");
			
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Key Pair Generator.....");
		System.out.print("\nEnter path to save Public Key : ");
		String publicKeyPath = sc.nextLine();
		System.out.print("\nEnter path to save Private Key : ");
		String privateKeyPath = sc.nextLine();
		
		generateKeyPair(publicKeyPath+"\\publicKey.pem", privateKeyPath+"\\privateKey.pem");
		sc.close();
	}
}
