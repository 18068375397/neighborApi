package kr.co.neighbor21.neighborApi.common.jpa.baseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공통 필드 처리를 위한 Base entity<br />
 * main class 에 @EnableJpaAuditing 추가 필요.<br />
 *
 * @author GEONLEE
 * @since 2024-03-30
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
    @CreatedDate
    @SearchField(columnName = "createDate")
    private LocalDateTime createDate;

    @LastModifiedDate
    @SearchField(columnName = "modifyDate")
    @Column(name = "update_date")
    private LocalDateTime modifyDate;
}
