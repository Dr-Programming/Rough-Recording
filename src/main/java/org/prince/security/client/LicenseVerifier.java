package org.prince.security.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

class LicenseVerifier {
	
	private static boolean verifySignature(String data, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		signature.update(data.getBytes());
		return signature.verify(signatureBytes);
	}
	
	protected static boolean verifyLicense(String publicKeyPath) throws IOException, InvalidKeySpecException,
	NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, 
	IllegalBlockSizeException, BadPaddingException, SignatureException {
		
		byte[] publicKeyBytes = Files.readAllBytes(Paths.get(publicKeyPath));
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
		
		String licenseContent = new String(LicenseDecryptor.decryptLicense());
		String[] parts = licenseContent.split(":");
		
		if(parts.length != 3) {
			throw new IllegalArgumentException("Invalid license file format");
		}
		
		byte[] dateSignature = Base64.getDecoder().decode(parts[2]);
		LocalDate dateObj = LocalDate.now();
		if(verifySignature(dateObj.toString(), dateSignature, publicKey)) {
			LicenseDecryptor.nullify();
			SecurityManager.isVerified();
			JOptionPane.showMessageDialog(null, "Your License has Expired!\n Contact Creator to resume your services.", "SparkleDi - Security Manager", JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException();
		}
		
		System.out.println("Date : " + verifySignature(dateObj.toString(), dateSignature, publicKey));
		
		String machineID = parts[0];
		byte[] signature = Base64.getDecoder().decode(parts[1]);
		String customerMachineId = HardwareFingerprint.getHardwareFingerprint();
		
		return machineID.equals(customerMachineId) && verifySignature(customerMachineId, signature, publicKey);
	}
}
