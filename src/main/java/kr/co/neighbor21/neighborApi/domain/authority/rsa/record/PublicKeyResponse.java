package kr.co.neighbor21.neighborApi.domain.authority.rsa.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * RSA Public key 응답 DTO
 *
 * @author GEONLEE
 * @since 2023-06-05<br />
 * 2024-03-06 record 로 변경<br/>
 **/
@Schema(description = "RSA PublicKey 응답 DTO")
@Builder
public record PublicKeyResponse(
        @Schema(description = "RSA Public key", example = "MIIBIjANBgkqhkiG9w0BAQEFAAOC...")
        String publicKey) {
}