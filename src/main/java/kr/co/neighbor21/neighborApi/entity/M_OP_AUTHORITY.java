package kr.co.neighbor21.neighborApi.entity;

import jakarta.persistence.*;
import kr.co.neighbor21.neighborApi.common.jpa.baseEntity.BaseEntity;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.DefaultSort;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;


/**
 * [마스터 운영 권한] Entity<br />
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */

@Getter
@Setter
@Entity
@Table(name = "M_OP_AUTHORITY")
@DefaultSort(columnName = "key.authorityId", dir = SortOrder.DESC)
public class M_OP_AUTHORITY extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField(columnName = "id")
    private BigInteger id;

    /* 키 */
    @Column(name = "authority_id")
    @SearchField(columnName = "authorityId")
    private String authorityId;
    /* 권한 명 */
    @Column(name = "authority_name")
    @SearchField(columnName = "authorityName")
    private String authorityName;
    /* 권한 코드 */
    @Column(name = "authority_code")
    @SearchField(columnName = "authorityCode")
    private String authorityCode;
    /* 설명 */
    @Column(name = "authority_description")
    @SearchField(columnName = "description")
    private String description;

//    @JsonIgnoreProperties(value = {"authorities"})
//    @ManyToMany(targetEntity = ROLE.class)
//    private List<ROLE> roles;
}
