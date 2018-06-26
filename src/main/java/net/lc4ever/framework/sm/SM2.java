/**
 *
 */
package net.lc4ever.framework.sm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author q-wang
 */
public class SM2 {

	private static final BigInteger P = new BigInteger(1, Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF"));
	private static final BigInteger A = new BigInteger(1, Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC"));
	private static final BigInteger B = new BigInteger(1, Hex.decode("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93"));
	private static final BigInteger N = new BigInteger(1, Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123"));
	private static final BigInteger X = new BigInteger(1, Hex.decode("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7"));
	private static final BigInteger Y = new BigInteger(1, Hex.decode("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"));

	private static final ECCurve CURVE = new ECCurve.Fp(P, A, B);
	private static final ECPoint G = CURVE.createPoint(X, Y);
	private static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(CURVE, G, N);

	private static final byte[] headBytes = "-----BEGIN CERTIFICATE-----".getBytes();
	private static final int headLength = headBytes.length;
	private static final byte[] endBytes = "-----END CERTIFICATE-----".getBytes();
	private static final int endLength = endBytes.length;

	private static final SecureRandom RANDOM;

	static {
		try {
			RANDOM = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private BigInteger D;

	private ECPoint userKey;

	private ECKeyPairGenerator keyPairGenerator;

	public SM2(BigInteger d, SecureRandom random) {
		this.D = d;
		initKeyPairGenerator(random);
	}

	public SM2(BigInteger d) {
		this(d, RANDOM);
	}

	public SM2(ECPoint userKey) {
		this.userKey = userKey;
	}

	public SM2(ECPoint userKey, BigInteger userD) {
		this(userD, RANDOM);
		this.userKey = userKey;
	}

	public SM2(ECPoint userKey, BigInteger userD, SecureRandom random) {
		this(userD, random);
		this.userKey = userKey;
	}

	public void initKeyPairGenerator(SecureRandom random) {
		ECKeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(DOMAIN_PARAMS, random);
		keyPairGenerator = new ECKeyPairGenerator();
		keyPairGenerator.init(keyGenerationParameters);
	}

	public static SM2 newInstance(byte[] keyFile, String password) throws GeneralSecurityException {
		ASN1Sequence asn1Sequence;
		if (needDecode(keyFile)) {
			asn1Sequence = ASN1Sequence.getInstance(Base64.decodeBase64(keyFile));
		} else {
			asn1Sequence = ASN1Sequence.getInstance(keyFile);
		}
		// TODO assert asn1Sequence.size == 3
		// TODO assert pub.size == 2, priv.size == 3
		ASN1Sequence priv = (ASN1Sequence) asn1Sequence.getObjectAt(1);
		ASN1Sequence pub = (ASN1Sequence) asn1Sequence.getObjectAt(2);

		byte[] certBytes = ((ASN1OctetString) pub.getObjectAt(1)).getOctets();
		certBytes = filterPEMText(certBytes);
		if (needDecode(certBytes)) {
			asn1Sequence = ASN1Sequence.getInstance(Base64.decodeBase64(certBytes));
		} else {
			asn1Sequence = ASN1Sequence.getInstance(certBytes);
		}
		Certificate certificate = Certificate.getInstance(asn1Sequence);

		SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
		byte[] pubData = subjectPublicKeyInfo.getPublicKeyData().getBytes();
		if ((pubData == null) || ((pubData.length != 64) && (pubData.length != 65))) {
			// not valid
			throw new IllegalStateException();
		}
		int starter = pubData.length == 65 ? 1 : 0;

		byte[] pubX = new byte[32];
		byte[] pubY = new byte[32];
		System.arraycopy(pubData, starter, pubX, 0, 32);
		System.arraycopy(pubData, starter + 32, pubY, 0, 32);

		ECPoint userKey = CURVE.createPoint(new BigInteger(1, pubX), new BigInteger(1, pubY));

		ASN1OctetString priOctString = (ASN1OctetString) priv.getObjectAt(2);
		byte[] encryptedData = priOctString.getOctets();
		byte[] d = SM4.decrypt(password.getBytes(), encryptedData);
		BigInteger D = new BigInteger(1, d);
		return new SM2(userKey, D);
	}

	private void nextKey(SM3Digest sm3KeyBase, int ct, byte[] key) {
		SM3Digest sm3keycur = new SM3Digest(sm3KeyBase);
		sm3keycur.update((byte) (ct >> 24 & 0xFF));
		sm3keycur.update((byte) (ct >> 16 & 0xFF));
		sm3keycur.update((byte) (ct >> 8 & 0xFF));
		sm3keycur.update((byte) (ct & 0xFF));
		sm3keycur.doFinal(key, 0);
	}

	public byte[] encrypt(byte[] data) {
		data = data.clone();
		AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();
		ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) keyPair.getPrivate();
		ECPublicKeyParameters ecpub = (ECPublicKeyParameters) keyPair.getPublic();
		BigInteger k = ecpriv.getD();
		// 65bytes c1
		byte[] c1 = ecpub.getQ().getEncoded(false);

		ECPoint p2 = userKey.multiply(k);

		byte[] px = p2.normalize().getXCoord().getEncoded();
		byte[] py = p2.normalize().getYCoord().getEncoded();
		// reset
		SM3Digest sm3keybase = new SM3Digest();
		SM3Digest sm3c3 = new SM3Digest();

		sm3keybase.update(px, 0, px.length);
		sm3c3.update(px, 0, px.length);
		sm3keybase.update(py, 0, py.length);
		int ct = 1;

		// encrypt
		byte[] key = new byte[32];
		int keyOff = 0;

		nextKey(sm3keybase, ct, key);
		ct++;

		sm3c3.update(data, 0, data.length);
		for (int i = 0; i < data.length; i++) {
			if (keyOff == key.length) {
				nextKey(sm3keybase, ct, key);
				keyOff = 0;
				ct++;
			}
			data[i] ^= key[keyOff++];
		}

		// final
		byte[] c3 = new byte[32];
		sm3c3.update(py, 0, py.length);
		sm3c3.doFinal(c3, 0);

		byte[] result = new byte[data.length + 32 + 64];
		System.arraycopy(c1, 1, result, 0, 64);
		System.arraycopy(c3, 0, result, 64, 32);
		System.arraycopy(data, 0, result, 96, data.length);
		return result;
	}

	public byte[] decrypt(byte[] data) {
		byte[] c1x = new byte[32];
		byte[] c1y = new byte[32];
		System.arraycopy(data, 0, c1x, 0, 32);
		System.arraycopy(data, 32, c1y, 0, 32);

		int encryptedLen = data.length - 64 - 32;
		byte[] encrypted = new byte[encryptedLen];
		System.arraycopy(data, 96, encrypted, 0, encryptedLen);

		ECPoint c1 = CURVE.createPoint(new BigInteger(1, c1x), new BigInteger(1, c1y));
		ECPoint p2 = c1.multiply(D);

		byte[] px = p2.normalize().getXCoord().getEncoded();
		byte[] py = p2.normalize().getYCoord().getEncoded();

		SM3Digest sm3KeyBase = new SM3Digest();
		sm3KeyBase.update(px, 0, px.length);
		sm3KeyBase.update(py, 0, py.length);

		SM3Digest sm3C3 = new SM3Digest();
		sm3C3.update(px, 0, px.length);

		int ct = 1;
		byte[] key = new byte[32];
		int keyOff = 0;

		nextKey(sm3KeyBase, ct, key);
		ct++;

		for (int i = 0; i < encryptedLen; i++) {
			if (keyOff == 32) {
				nextKey(sm3KeyBase, ct, key);
				keyOff = 0;
				ct++;
			}
			encrypted[i] = ((byte) (encrypted[i] ^ key[keyOff++]));
		}
		sm3C3.update(encrypted, 0, encryptedLen);
		sm3C3.update(py, 0, py.length);

		byte[] c3 = new byte[32];
		sm3C3.doFinal(c3, 0);

		byte[] old_c3 = new byte[32];
		System.arraycopy(data, 64, old_c3, 0, 32);
		if (Arrays.equals(c3, old_c3)) {
			return encrypted;
		}
		throw new IllegalStateException("can not decrypted use SM2 encryp/decrypt,please check if the data is right");
	}

	private static final byte[] filterPEMText(byte[] certData) {
		byte[] certHead = new byte[headLength];
		byte[] certEnd = new byte[endLength];

		System.arraycopy(certData, 0, certHead, 0, headLength);
		boolean hasHead = Arrays.equals(certHead, headBytes);
		if (hasHead) {
			certData = deleteCRLF(certData);
		}
		int certDataLength = certData.length;

		System.arraycopy(certData, certDataLength - endLength, certEnd, 0, endLength);
		boolean hasEnd = Arrays.equals(certEnd, endBytes);

		int datStarter = 0;
		int datLength = 0;
		byte[] certBytes = null;
		if ((hasHead) && (hasEnd)) {
			datStarter = headLength;
			datLength = certDataLength - headLength - endLength;
		} else if ((!hasHead) && (hasEnd)) {
			datStarter = 0;
			datLength = certDataLength - endLength;
		} else if ((hasHead) && (!hasEnd)) {
			datStarter = headLength;
			datLength = certDataLength - headLength;
		} else {
			certBytes = certData;
		}
		if (certBytes == null) {
			certBytes = new byte[datLength];
			System.arraycopy(certData, datStarter, certBytes, 0, certBytes.length);
		}
		return certBytes;
	}

	private static byte[] deleteCRLF(byte[] data) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte tmp;
		while ((tmp = (byte) bis.read()) != -1) {
			if ((tmp != 10) && (tmp != 13)) {
				bos.write(tmp);
			}
		}
		return bos.toByteArray();
	}

	private static boolean needDecode(byte[] data) {
		if (data[0] != 48) {
			return true;
		}
		int len = data[1] & 0xFF;
		if (len == 128) {
			return data[data.length - 1] != 0 || data[data.length - 2] != 0;
		} else if (len < 128) {
			return len + 2 != data.length;
		} else {
			int dLen = len & 0x7F;
			if (dLen > 4) {
				return true;
			}
			int offset = 2;
			len = 0;
			int next = 0;
			for (int i = 0; i < dLen; i++) {
				next = data[(offset++)] & 0xFF;
				len = (len << 8) + next;
			}
			if (len < 0) {
				return true;
			}
			return data.length != offset + len;
		}
	}
}
