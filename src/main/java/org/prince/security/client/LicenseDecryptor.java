package org.prince.security.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class LicenseDecryptor {
	
	protected static byte[] decryptLicense() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
		
		String passphrase = HardwareFingerprint.getHardwareFingerprint();
		
		byte[] encryptedPrivateKeyBytes = Files.readAllBytes(Paths.get("src/main/resources/sysFiles/customerPrivateKey.key"));
		EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKeyBytes);
		
		PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
		
		Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
		cipher.init(Cipher.DECRYPT_MODE, secretKeyFactory.generateSecret(pbeKeySpec), encryptedPrivateKeyInfo.getAlgParameters());
		
		byte[] decryptedPrivateKeyBytes = cipher.doFinal(encryptedPrivateKeyInfo.getEncryptedData());
		PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKeyBytes));
		
		byte[] encryptedAesKey = Base64.getDecoder().decode(Files.readAllBytes(Paths.get("C:\\Users\\princ\\OneDrive\\Desktop\\App Setup\\AesKey.key")));
		byte[] encryptedAesKey = Base64.getDecoder().decode(Files.readAllBytes(Paths.get("src/main/resources/sysFiles/AesKey.key")));
		
		Cipher rsaCipher = Cipher.getInstance("RSA");
		rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
		SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
		
		byte[] encryptedLicenseData = Base64.getDecoder().decode(Files.readAllBytes(Paths.get("src/main/resources/sysFiles/license.enc")));
		
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
		
		return aesCipher.doFinal(encryptedLicenseData);
	}
}
