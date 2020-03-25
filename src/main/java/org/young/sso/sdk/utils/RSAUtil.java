package org.young.sso.sdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.util.Assert;

public final class RSAUtil {
	
	private static final String PUB_PEM_FILE  = "edpadmin_pub.pem";
	private static final int MAX_BYTES_LENGTH = 2048;
	private static final int MAX_ENCRYPT_BLOCK_LENGTH = 245;
	
	private RSAUtil() {}
	
	/**
	 * 加密，被加密数据不能超过2048字节
	 * @param data 数据
	 * @return 已加密数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data) throws Exception {
		
		PublicKey publicKey = null;
		PEMParser parser    = null;
		try {
			Reader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(PUB_PEM_FILE));
			parser = new PEMParser(reader);
			SubjectPublicKeyInfo info = (SubjectPublicKeyInfo) parser.readObject();
			X509EncodedKeySpec spec = new X509EncodedKeySpec(info.getEncoded());
			publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
		} finally {
			IOUtils.closeQuietly(parser);
		}
		
		
		Assert.notNull(publicKey, "The publicKey must not be null");
		Assert.notNull(data, "The data must not be null");
		if (data.length > MAX_BYTES_LENGTH) {
			throw new IllegalArgumentException(String.format("Data must not be longer than %s bytes", 2048));
		}

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] cache = null;

		int maxBlock = MAX_ENCRYPT_BLOCK_LENGTH;
		int length   = data.length;

		int offSet = 0;
		int count  = 0;
		try {
			// 对数据分段解密
			while (length-offSet > 0) {
				if (length-offSet > maxBlock) {
					cache = cipher.doFinal(data, offSet, maxBlock);
				} else {
					cache = cipher.doFinal(data, offSet, length-offSet);
				}
				bytes.write(cache, 0, cache.length);

				count++;
				offSet = count * maxBlock;
			}

			
			Encoder encoder = Base64.getEncoder();
			return encoder.encode(bytes.toByteArray());

		} finally {
			IOUtils.closeQuietly(bytes);
		}
	}

}
