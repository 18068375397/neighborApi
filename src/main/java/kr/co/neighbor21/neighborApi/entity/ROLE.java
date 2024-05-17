package kr.co.neighbor21.neighborApi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import kr.co.neighbor21.neighborApi.common.jpa.baseEntity.BaseEntity;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.DefaultSort;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "ROLE")
@DefaultSort(columnName = "id", dir = SortOrder.DESC)
public class ROLE extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField(columnName = "id")
    private int id;

    @Column(name = "role_id")
    @SearchField(columnName = "roleId")
    private String roleId;

    @Column(name = "role_name")
    @SearchField(columnName = "roleName")
    private String roleName;

//    @JsonIgnoreProperties(value = {"roles"})
//    @ManyToMany(targetEntity = M_OP_OPERATOR.class)
//    private List<M_OP_OPERATOR> mOpOperators;

    @JsonIgnoreProperties(value = {"roles"})
    @ManyToMany(targetEntity = M_OP_AUTHORITY.class, fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "ROLE_AUTHORITY",
            //joinColumns,当前对象在中间表中的外键
            joinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "role_id")},
            //inverseJoinColumns ,对方对象在中间表中的外键
            inverseJoinColumns = {@JoinColumn(name = "authority_id",referencedColumnName = "authority_id")}
    )
    private List<M_OP_AUTHORITY> authorities;

}
