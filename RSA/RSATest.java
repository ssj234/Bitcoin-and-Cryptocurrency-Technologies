package test;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * 1.密钥长度需要是64的整数倍，在512~65536范围内；默认为1024，工作长度为ECB，填充方式...，实现方法JDK
 * 2.密钥长度为2048位的，工作模式为NONE，填充方式...,实现方法BC
 * @author shisj
 *
 */
public class RSATest {
	public static final String src = "hello world";
	public static void main(String[] args) throws Exception {
		jdkRSA();
	}
	
	public static void jdkRSA() throws Exception {
		//1.初始化密钥
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(521);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
		System.out.println("Public key is " + Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()));
		System.out.println("Private key is " +  Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded()));
		
		//2.私钥加密，公钥解密 -- 加密
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte [] result  = cipher.doFinal(src.getBytes());
		System.out.println("私钥加密，公钥解密 --- 加密：" + Base64.getEncoder().encodeToString(result));
		
		//3.私钥加密，公钥解密 -- 解密
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
		keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		result  = cipher.doFinal(result);
		System.out.println("私钥加密，公钥解密 --- 解密：" + new String(result));
		
		// 4.公钥加密，私钥解密 -- 加密
		x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
		keyFactory = KeyFactory.getInstance("RSA");
		publicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		result  = cipher.doFinal(src.getBytes());
		System.out.println("公钥加密，私钥解密 --- 加密：" + Base64.getEncoder().encodeToString(result));
		
		// 5.公钥加密，私钥解密 -- 解密
		pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
		keyFactory = KeyFactory.getInstance("RSA");
		privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		result  = cipher.doFinal(result);
		System.out.println("私钥加密，公钥解密 --- 解密：" + new String(result));
		
	}
}
