package kr.co.neighbor21.neighborApi.config.jwt.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Jwt token valid process 에서 사용하는 dto
 *
 * @author GEONLEE
 * @since 2024-03-29<br />
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class JwtValidDto {
    private boolean valid;
    private String userId;
    private String accessToken;
}
