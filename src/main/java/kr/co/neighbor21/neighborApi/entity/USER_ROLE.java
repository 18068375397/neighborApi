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
@Table(name = "USER_ROLE")
@DefaultSort(columnName = "id", dir = SortOrder.DESC)
public class USER_ROLE {


    @Id
    @GeneratedValue
    @Column(name = "id")
    @SearchField(columnName = "id")
    private int id;

    @Column(name = "user_id")
    @SearchField(columnName = "user_id")
    private String userId;

    @Column(name = "role_id")
    @SearchField(columnName = "role_id")
    private int roleId;
}
