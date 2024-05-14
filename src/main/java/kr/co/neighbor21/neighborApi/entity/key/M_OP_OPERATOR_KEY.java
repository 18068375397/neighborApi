package kr.co.neighbor21.neighborApi.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;


/**
 * [마스터 운영 운영자] Entity Key<br />
 *
 * @author 
 * @since 2024-03-21<br />
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class M_OP_OPERATOR_KEY implements Serializable {

    /* 운영자 ID */
    @Column(name = "user_id")
    private String userId;

}
