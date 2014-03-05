import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class Auth {
	
	public Auth() {
		
	}
	
	/*Used to encrypt shared secret key*/
	public static byte[] encrypt(String plainText, /*Text to be encrypted*/ 
								 Key key /*An instance of SecretKeySpec*/) throws Exception {
		Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.ENCRYPT_MODE, key);
		return  aes.doFinal(plainText.getBytes());
	}
	
	/*Used to decrypt shared secret key*/
	public static String decrypt(byte[] cipherText, /*Ciphertext to be decrypted*/
								 Key key /*Same as above*/) throws Exception{
		Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.DECRYPT_MODE, key);
		return new String(aes.doFinal(cipherText));
	}
	
    protected static String generateSharedSecret() {
        byte[] buffer = new byte[10];
        new SecureRandom().nextBytes(buffer);
        String secret = new String(new Base32().encode(buffer));
        return secret;
    }
    
    
    public static String computeHash(String pswd,
    								 String salt,
    								 String HashAlgorithm) throws NoSuchAlgorithmException {
    	byte[] intermediate;
    	pswd = pswd + salt;
    	intermediate = pswd.getBytes();
    	MessageDigest messageDigest = MessageDigest.getInstance(HashAlgorithm);
    	for(int i=0; i<10; i++) {
    		intermediate = messageDigest.digest(pswd.getBytes());
    		pswd = new String(intermediate) + salt;
    	}
    	return new String(intermediate);
    }
    
    public static boolean validatePassword(String pswd) throws NoSuchAlgorithmException {
    	//get the stored hash and salt from the database
    	
    	//computing hash from the provided password
    	String pswdtoValidate = computeHash(pswd, "iamsalt", "SHA-256");
    	String pswdOriginal = null; //retrieved hash from database computeHash("iamsamrat", "iamsalt", "SHA-256"); 
    	
    	if(pswdOriginal.equals(pswdtoValidate))
    		return true;
    	else
    		return false;
    }

    public static boolean validateSecretCode(long ourCode, long usersCode) {
    	if (usersCode - ourCode == 0)
    		return true;
    	else
    		return false;
    }
    
    protected static long getSecretCode(String secret) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signKey = new SecretKeySpec(new Base32().decode(secret), "HmacSHA1");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(System.currentTimeMillis()/1000/30); //time index
        byte[] timeBytes = buffer.array();
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(timeBytes);
        int offset = hash[19] & 0xf;
        long truncatedHash = hash[offset] & 0x7f;
        for (int i = 1; i < 4; i++) {
            truncatedHash <<= 8;
            truncatedHash |= hash[offset + i] & 0xff;
        }
        return (truncatedHash %= 1000000);
    }
    
    public static void generateQRCode(String stringtoEncode) throws WriterException, IOException, IOException {   	  
    	int width = 300;  
    	int height = 300;
    	String imageFormat = "png";  
    	  
    	BitMatrix bitMatrix = new QRCodeWriter().encode(stringtoEncode, BarcodeFormat.QR_CODE, width, height);  
    	MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, new FileOutputStream(new File("/Users/administrator/Desktop/QR"+stringtoEncode+".png")));  
    }
}
