package kr.co.neighbor21.neighborApi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import kr.co.neighbor21.neighborApi.common.jpa.baseEntity.BaseEntity;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.DefaultSort;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
@Entity
@Table(name = "ROLE")
@DefaultSort(columnName = "id", dir = SortOrder.DESC)
public class ROLE extends BaseEntity {

    @Id
    @Column(name = "id")
    @SearchField(columnName = "id")
    private int id;

    @Column(name = "role_id")
    @SearchField(columnName = "roleId")
    private String roleId;

    @Column(name = "role_name")
    @SearchField(columnName = "roleName")
    private String roleName;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<M_OP_OPERATOR> mOpOperators;

    @JsonIgnoreProperties(value = {"roles"})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_AUTHORITY",
            joinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id",referencedColumnName = "authority_id")}
    )
    private List<M_OP_AUTHORITY> authorities;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ROLE entity = (ROLE) o;
        return Objects.equals(id, entity.id);
    }
}
