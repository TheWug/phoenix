package hello;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class Auth {

	Base32 b32;
	SecureRandom secureRandom; // guaranteed threadsafe

	public Auth() throws GeneralSecurityException {
		b32 = new Base32();
		secureRandom = new SecureRandom();
	}

	/*Used to encrypt shared secret key*/
	public byte[] encrypt(String plainText, /*Text to be encrypted*/
								 Key key /*An instance of SecretKeySpec*/) throws Exception {
		Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.ENCRYPT_MODE, key);
		return aes.doFinal(plainText.getBytes(Charset.forName("UTF-8")));
	}
	
	/*Used to decrypt shared secret key*/
	public String decrypt(byte[] cipherText, /*Ciphertext to be decrypted*/
								 Key key /*Same as above*/) throws Exception{
		Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.DECRYPT_MODE, key);
		return new String(aes.doFinal(cipherText), Charset.forName("UTF-8"));
	}
	
	public String generateSharedSecret() {
        byte[] buffer = new byte[30];
        new SecureRandom().nextBytes(buffer);
        String secret = new String(new Base32().encode(buffer));
        return secret;
    }
    
	public String computeHash(String pswd, String salt) throws GeneralSecurityException{
		byte[] saltBytes = salt.getBytes(Charset.forName("UTF-8"));
		byte[] pswdBytes = pswd.getBytes(Charset.forName("UTF-8"));
		ByteBuffer buf = ByteBuffer.allocate(pswdBytes.length
				+ saltBytes.length);

		buf.put(pswd.getBytes(Charset.forName("UTF-8")));

		for (int i = 0; true; ++i) {
			buf.put(saltBytes);
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(buf.array());
			if (!(i < 1024))
				return new String(Base64.encodeBase64String(digest));
			buf = ByteBuffer.allocate(digest.length + saltBytes.length);
			buf.put(digest);
		}
	}
    
	public boolean validatePassword(String pswd, String salt, String hash) throws GeneralSecurityException{
		String hashToValidate = computeHash(pswd, salt);
		return (hashToValidate.equals(hash));
	}

    public boolean validateSecretCode(long ourCode, String secretKey, long timeIndex) throws GeneralSecurityException {
        long correctCode = getSecretCode(secretKey, timeIndex);
        return (correctCode == ourCode);
    }
    
    public long getSecretCode(String secret, long timeIndex) throws GeneralSecurityException {
        SecretKeySpec signKey = new SecretKeySpec(b32.decode(secret), "HmacSHA1");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(timeIndex); //time index

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(buffer.array());
        
        int offset = hash[19] & 0xf;
        long truncatedHash = hash[offset] & 0x7f;
        for (int i = 1; i < 4; i++) {
            truncatedHash <<= 8;
            truncatedHash |= hash[offset + i] & 0xff;
        }
        return (truncatedHash %= 1000000);
    }

    public long getTimeIndex(long time)
    {
        return time / 30000;
    }
    
    public String generateQRCode(String stringtoEncode) throws WriterException, IOException, IOException {   	  
    	int width = 300;  
    	int height = 300;
    	String imageFormat = "png";  
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	BitMatrix bitMatrix = new QRCodeWriter().encode(stringtoEncode, BarcodeFormat.QR_CODE, width, height);  
    	MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, bos);
    	byte[] byteArray = bos.toByteArray();
    	return Base64.encodeBase64String(byteArray);  
    }
}
