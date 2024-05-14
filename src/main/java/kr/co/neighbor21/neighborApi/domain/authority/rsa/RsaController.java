package kr.co.neighbor21.neighborApi.domain.authority.rsa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.config.message.MessageConfig;
import kr.co.neighbor21.neighborApi.config.rsa.RsaKeyGenerator;
import kr.co.neighbor21.neighborApi.domain.authority.rsa.record.PublicKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * RSA Public key 를 리턴해준다.
 *
 * @author GEONLEE
 * @since 2023-02-20<br />
 */
@RestController
@Tag(name = "[API-999] RSA public key request", description = "[담당자 : GEONLEE]")
@RequiredArgsConstructor
public class RsaController {
    private final RsaKeyGenerator rsaKeyGenerator;
    private final MessageConfig messageConfig;

    /**
     * RSA Public 응답 메서드
     *
     * @author GEONLEE
     * @since 2023-02-20<br />
     * 2024-03-15 GEONLEE - ResponseStructure 로 리턴하도록 변경, description 수정<br />
     */
    @PostMapping(value = "/v1/public-key", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "보안이 필요한 정보를 암호화 하기 위한 RSA publicKey 요청", description = """
            # No Parameter
                        
            ※ 보안이 필요한 데이터를 public key 로 암호화 후 전송 한다. ex) password
                        
            """,
            operationId = "API-999-01")
    public ResponseEntity<ItemResponse<PublicKeyResponse>> getPublicKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("SEARCH.SUCCESS.MSG");
        PublicKey key = rsaKeyGenerator.getPublicKey();
        String keyString = Base64.getEncoder().encodeToString(key.getEncoded());
        PublicKeyResponse publicKeyResponse = PublicKeyResponse.builder().publicKey(keyString).build();
        return ResponseEntity.ok()
                .body(ItemResponse.<PublicKeyResponse>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .item(publicKeyResponse)
                        .build());
    }
}