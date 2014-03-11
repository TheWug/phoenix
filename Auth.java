import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
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
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class Auth {
	MessageDigest pwhasher;
	Cipher aes;
	Mac mac;
	Base32 b32;
	SecureRandom secureRandom;

	public static void main(String[] args) throws Exception {
		Auth x = new Auth();

		String pw1 = "secret1";
		String pw2 = "secret2";
		String salt1 = "!@#$%^&*()";
		String salt2 = ")(*&^%$#@!";

		String code1 = x.generateSharedSecret();
		String code2 = x.generateSharedSecret();

		String hash1 = x.computeHash(pw1, salt1);
		String hash2 = x.computeHash(pw2, salt2);

		boolean validate1 = x.validatePassword(pw1, salt1, hash1);
		boolean validate2 = x.validatePassword(pw2, salt2, hash2);
		boolean validateFalse = x.validatePassword("foo", "bar", hash1);

		System.out.println(code1);
		System.out.println(code2);

		System.out.println(x.generateQRCode(code1));
		System.out.println(x.generateQRCode(code2));

		System.out.println(hash1);
		System.out.println(hash2);

		System.out.println(validate1 + " " + validate2 + " " + validateFalse);

	}

	public Auth() throws GeneralSecurityException {
		pwhasher = MessageDigest.getInstance("SHA-256");
		aes = Cipher.getInstance("AES");
		b32 = new Base32();
		secureRandom = new SecureRandom();
		mac = Mac.getInstance("HmacSHA1");
	}

// 	If we have time to make this work (which is doubtful) we can pursue this then.
//	barring that, don't bother trying to encrypt secret keys.

//	/* Used to encrypt shared secret key */
//	public byte[] encrypt(String plainText, /* Text to be encrypted */
//			Key key /* An instance of SecretKeySpec */) throws Exception {
//		aes.init(Cipher.ENCRYPT_MODE, key);
//		return aes.doFinal(plainText.getBytes());
//	}

//	/* Used to decrypt shared secret key */
//	public String decrypt(byte[] cipherText, /* Ciphertext to be decrypted */
//			Key key /* Same as above */) throws Exception {
//		aes.init(Cipher.DECRYPT_MODE, key);
//		return new String(aes.doFinal(cipherText));
//	}

	public String generateSharedSecret() {
		byte[] buffer = new byte[32];
		secureRandom.nextBytes(buffer);
		String secret = new String(b32.encode(buffer));
		return secret;
	}

	public String computeHash(String pswd, String salt)
			throws GeneralSecurityException {
		byte[] saltBytes = salt.getBytes(Charset.forName("UTF-8"));
		byte[] pswdBytes = pswd.getBytes(Charset.forName("UTF-8"));
		ByteBuffer buf = ByteBuffer.allocate(pswdBytes.length
				+ saltBytes.length);

		buf.put(pswd.getBytes(Charset.forName("UTF-8")));

		for (int i = 0; true; ++i) {
			buf.put(saltBytes);
			byte[] digest = pwhasher.digest(buf.array());
			if (!(i < 1024))
				return new String(Base64.encodeBase64String(digest));
			buf = ByteBuffer.allocate(digest.length + saltBytes.length);
			buf.put(digest);
		}
	}

	public boolean validatePassword(String pswd, String salt, String hash)
			throws GeneralSecurityException {
		String hashToValidate = computeHash(pswd, salt);
		return (hashToValidate.equals(hash));
	}

	protected long getSecretCode(String secret, long timestep) throws GeneralSecurityException {
		SecretKeySpec signKey = new SecretKeySpec(b32.decode(secret),
				"HmacSHA1");

		if (timestep == -1)
			timestep = System.currentTimeMillis() / 1000 / 30;

		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(timestep); // time index
		byte[] timeBytes = buffer.array();
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

	public String generateQRCode(String stringtoEncode) throws WriterException,
			IOException, IOException {
		int width = 300;
		int height = 300;
		String imageFormat = "png";

		BitMatrix bitMatrix = new QRCodeWriter().encode(stringtoEncode,
				BarcodeFormat.QR_CODE, width, height);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
		MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, baos);
		return Base64.encodeBase64String(baos.toByteArray());
	}
}
