package kr.co.neighbor21.neighborApi.config.rsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 키를 생성한다. properties 파일 내 keyPair.path 경로에 private.pem, public.pem 을 생성한다.
 * 만일 파일이 있다면 해당 파일의 데이터를 읽어온다.
 * 파일이나 폴더 중 하나라도 없다면 다시 생성한다.
 * 키 요청이 올 경우 파일을 읽어 키를 생성해 리턴한다.
 * 검증 : https://cryptotools.net/rsagen
 * 서버가 이중화되는 경우 하드웨어 하나에서 키를 관리해야 한다.
 *
 * @author GEONLEE
 * @since 2023-02-20<br />
 * 2023-02-22 GEONLEE 암/복호화 메소드 추가<br />
 * 2023-03-06 GEONLEE decryptRSA Exception 처리 방식 변경, 복호화 Exception 발생 시 IllegalArgumentException 으로 통합<br />
 * 2023-04-10 GEONLEE modify static final to @value, Logger msg 변경<br />
 * 2023-12-04 GEONLEE 코드 정리<br />
 */
@Configuration
public class RsaKeyGenerator implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RsaKeyGenerator.class);

    @Value("${rsa.keyPair.path}")
    private String path = "keyPair";

    @Value("${rsa.keyPair.algorithm}")
    private String algorithm = "RSA";

    @Value("${rsa.keyPair.keySize}")
    private int keySize = 2048;

    @Override
    public void afterPropertiesSet() throws NoSuchAlgorithmException, IOException {
        if (!checkKeyFileExist()) {
            createKeyFile();
        } else {
            LOGGER.info("There are RSA keys, so use these");
        }
    }

    /**
     * 키 파일이나 폴더가 존재하는지 체크하는 메소드
     *
     * @return boolean (파일 존재여부)
     * @author GEONLEE
     * @since 2023-02-20<br />
     */
    private boolean checkKeyFileExist() {
        File folder = new File(File.separator + this.path + File.separator);
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }
        String[] keyFiles = {"public.pem", "private.pem"};
        for (String keyFile : keyFiles) {
            File file = new File(folder, keyFile);
            if (!file.exists() || file.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 키 파일을 생성하는 메소드, 무조건 파일을 모두 새로 생성한다.
     *
     * @author GEONLEE
     * @since 2023-02-20
     */
    private void createKeyFile() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(this.algorithm);
        keyPairGenerator.initialize(this.keySize);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        Key[] keys = new Key[]{keyPair.getPublic(), keyPair.getPrivate()};
        FileOutputStream fos = null;
        File file = null;
        try {
            String defaultPath = File.separator + this.path + File.separator;
            for (Key key : keys) {
                String path = null;
                if (key.equals(keyPair.getPublic())) {
                    path = defaultPath + "public.pem";
                } else {
                    path = defaultPath + "private.pem";
                }

                file = new File(path);
                if (!file.exists()) {
                    boolean result = file.createNewFile();
                    if (result) {
                        fos = new FileOutputStream(file);
                        fos.write(key.getEncoded());
                        LOGGER.info("Create new RSA keys. path: {}", path);
                        fos.close();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create key files.");
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
                fos.flush();
            }
        }
    }

    /**
     * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
     *
     * @author GEONLEE
     * @since 2023-02-20<br />
     */
    public PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!checkKeyFileExist()) {
            createKeyFile();
        }
        byte[] bytes = Files.readAllBytes(Paths.get(File.separator + this.path + File.separator + "private.pem"));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
     *
     * @author GEONLEE
     * @since 2023-02-20<br />
     */
    public PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!checkKeyFileExist()) {
            createKeyFile();
        }
        byte[] bytes = Files.readAllBytes(Paths.get(File.separator + this.path + File.separator + "public.pem"));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
        return keyFactory.generatePublic(spec);
    }

    /**
     * public 키로 암호화를 한다.
     *
     * @author GEONLEE
     * @since 2023-02-22<br />
     */
    public String encryptRSA(String plainText) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        PublicKey publicKey = getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytePlain = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(bytePlain);
    }

    /**
     * private 키로 복호화를 한다.
     *
     * @author GEONLEE
     * @since 2023-02-22<br />
     */
    public String decryptRSA(String encrypted) {
        String decrypted = null;
        try {
            PrivateKey privateKey = getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA");
            byte[] byteEncrypted = Base64.getDecoder().decode(encrypted.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytePlain = cipher.doFinal(byteEncrypted);
            decrypted = new String(bytePlain, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | IllegalArgumentException e) {
            LOGGER.error("Failed to RSA decryption, check the encrypted password: " + encrypted);
            e.printStackTrace();
        } catch (BadPaddingException e) {
            LOGGER.error("RSA key is incorrect: " + encrypted);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.error("Fail to get private key or Cipher.");
        }
        return decrypted;
    }
}
