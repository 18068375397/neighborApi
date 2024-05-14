package kr.co.neighbor21.neighborApi.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;


/**
 * [마스터 운영 권한] Entity Key<br />
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class M_OP_AUTHORITY_KEY implements Serializable {

    /* 권한 ID */
    @Column(name = "authority_id")
    private String authorityId;

}
