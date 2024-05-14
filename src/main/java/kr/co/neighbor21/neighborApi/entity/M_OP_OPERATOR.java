package kr.co.neighbor21.neighborApi.entity;

import jakarta.persistence.*;
import kr.co.neighbor21.neighborApi.common.jpa.baseEntity.BaseEntity;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.DefaultSort;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;
import kr.co.neighbor21.neighborApi.entity.key.M_OP_OPERATOR_KEY;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * [마스터 운영 운영자] Entity<br />
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */

@Getter
@Setter
@Entity
@Table(name = "m_op_operator")
@DefaultSort(columnName = "key.userId", dir = SortOrder.DESC)
public class M_OP_OPERATOR extends BaseEntity {
    /*권한*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id")
    M_OP_AUTHORITY authority = new M_OP_AUTHORITY();
    /* 키 */
    @EmbeddedId
    @SearchField(columnName = {"key.userId"})
    private M_OP_OPERATOR_KEY key;
    /* 운영자 비밀번호 */
    @Column(name = "password")
    private String password;
    /* 운영자 명 */
    @Column(name = "user_name")
    @SearchField(columnName = "userName")
    private String userName;
    /* 운영자 전화번호 */
    @Column(name = "telephone")
    @SearchField(columnName = "telephone")
    private String telephone;
    /* 운영자 휴대전화 */
    @Column(name = "cellphone")
    @SearchField(columnName = "cellphone")
    private String cellphone;
    /* 운영자 직책 */
    @Column(name = "position")
    @SearchField(columnName = "position")
    private String position;
    /* 운영자 부서 */
    @Column(name = "department")
    @SearchField(columnName = "department")
    private String department;
    /* 사용 여부 */
    @Column(name = "use_yn")
    @SearchField(columnName = "isUse")
    private String isUse;
    /* 어세스토큰*/
    @Column(name = "access_token")
    private String accessToken;
    /* 리프레쉬토큰*/
    @Column(name = "refresh_token")
    private String refreshToken;
    /* 패스워드갱신 일자 */
    @Column(name = "password_update_date")
    private LocalDateTime passwordUpdateDate;
    /* 패스워드수정주기 */
    @Column(name = "password_update_cycle")
    private Long passwordUpdateCycle;
}
