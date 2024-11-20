//Still this Class is under development...
//But it has currently one use in the application.

package org.prince.security.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SecurityManager {
	
	public static boolean isVerified() {
		try {
			return LicenseVerifier.verifyLicense("src/main/resources/sysFiles/publicKey.pem");
//			return LicenseVerifier.verifyLicense("C:\\Users\\princ\\OneDrive\\Desktop\\App Setup\\publicKey.pem");
		} catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| SignatureException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
