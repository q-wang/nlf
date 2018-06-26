/**
 *
 */
package net.lc4ever.framework.sm;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.digests.SM3Digest;

/**
 * @author q-wang
 */
public class SM4 {

	public static byte[] decrypt(byte[] iv, byte[] key, byte[] data) throws GeneralSecurityException {
		SecretKeySpec keySm4 = new SecretKeySpec(key, "SM4");
		IvParameterSpec IvSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS7Padding", "BC");
		cipher.init(Cipher.DECRYPT_MODE, keySm4, IvSpec);
		return cipher.doFinal(data);
	}

	public static byte[] encrypt(byte[] iv, byte[] key, byte[] data) throws GeneralSecurityException {
		SecretKeySpec keySm4 = new SecretKeySpec(key, "SM4");
		IvParameterSpec IvSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS7Padding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, keySm4, IvSpec);
		return cipher.doFinal(data);
	}

	public static byte[] decrypt(byte[] pass, byte[] data) throws GeneralSecurityException {
		byte[] iv = new byte[16];
		byte[] key = new byte[16];
		byte[] hash = KDF(pass);
		System.arraycopy(hash, 0, iv, 0, 16);
		System.arraycopy(hash, 16, key, 0, 16);

		return decrypt(iv, key, data);
	}

	public static byte[] encrypt(byte[] pass, byte[] data) throws GeneralSecurityException {
		byte[] iv = new byte[16];
		byte[] key = new byte[16];
		byte[] hash = KDF(pass);
		System.arraycopy(hash, 0, iv, 0, 16);
		System.arraycopy(hash, 16, key, 0, 16);

		return encrypt(iv, key, data);
	}

	private static byte[] KDF(byte[] z) {
		byte[] ct = { 0, 0, 0, 1 };
		SM3Digest sm3 = new SM3Digest();
		sm3.update(z, 0, z.length);
		sm3.update(ct, 0, ct.length);
		byte[] hash = new byte[32];
		sm3.doFinal(hash, 0);
		return hash;
	}
}
