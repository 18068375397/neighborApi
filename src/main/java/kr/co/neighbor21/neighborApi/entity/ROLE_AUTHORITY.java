package kr.co.neighbor21.neighborApi.entity;

import jakarta.persistence.*;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.DefaultSort;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "ROLE_AUTHORITY")
@DefaultSort(columnName = "id", dir = SortOrder.DESC)
public class ROLE_AUTHORITY {


    @Id
    @GeneratedValue
    @Column(name = "id")
    @SearchField(columnName = "id")
    private int id;

    @Column(name = "role_id")
    @SearchField(columnName = "role_id")
    private int roleId;

    @Column(name = "authority_id")
    @SearchField(columnName = "authority_id")
    private String authorityId;

}
