package kr.co.neighbor21.neighborApi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.DefaultSort;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "ROLE")
@DefaultSort(columnName = "id", dir = SortOrder.DESC)
public class ROLE {


    @Id
    @GeneratedValue
    @Column(name = "id")
    @SearchField(columnName = "id")
    private int id;

    @Column(name = "role_id")
    @SearchField(columnName = "role_id")
    private String roleId;

    @Column(name = "role_name")
    @SearchField(columnName = "role_name")
    private String roleName;

    @JsonIgnoreProperties(value = {"roles"})
    @ManyToMany(targetEntity = M_OP_OPERATOR.class, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE",
            //joinColumns,当前对象在中间表中的外键
            joinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "role_id")},
            //inverseJoinColumns ,对方对象在中间表中的外键
            inverseJoinColumns = {@JoinColumn(name = "user_id",referencedColumnName = "user_id")}
    )
    List<M_OP_OPERATOR> mOpOperators = new ArrayList<>();

    @JsonIgnoreProperties(value = {"roles"})
    @ManyToMany(targetEntity = M_OP_AUTHORITY.class, fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_AUTHORITY",
            //joinColumns,当前对象在中间表中的外键
            joinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "role_id")},
            //inverseJoinColumns ,对方对象在中间表中的外键
            inverseJoinColumns = {@JoinColumn(name = "authority_id",referencedColumnName = "authority_id")}
    )
    List<M_OP_AUTHORITY> authorities = new ArrayList<>();

}
