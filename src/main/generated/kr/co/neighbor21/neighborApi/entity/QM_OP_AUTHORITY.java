package kr.co.neighbor21.neighborApi.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QM_OP_AUTHORITY is a Querydsl query type for M_OP_AUTHORITY
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QM_OP_AUTHORITY extends EntityPathBase<M_OP_AUTHORITY> {

    private static final long serialVersionUID = -1541275526L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QM_OP_AUTHORITY m_OP_AUTHORITY = new QM_OP_AUTHORITY("m_OP_AUTHORITY");

    public final kr.co.neighbor21.neighborApi.common.jpa.baseEntity.QBaseEntity _super = new kr.co.neighbor21.neighborApi.common.jpa.baseEntity.QBaseEntity(this);

    public final StringPath authorityCode = createString("authorityCode");

    public final StringPath authorityName = createString("authorityName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath description = createString("description");

    public final kr.co.neighbor21.neighborApi.entity.key.QM_OP_AUTHORITY_KEY key;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final ListPath<M_OP_OPERATOR, QM_OP_OPERATOR> operator = this.<M_OP_OPERATOR, QM_OP_OPERATOR>createList("operator", M_OP_OPERATOR.class, QM_OP_OPERATOR.class, PathInits.DIRECT2);

    public QM_OP_AUTHORITY(String variable) {
        this(M_OP_AUTHORITY.class, forVariable(variable), INITS);
    }

    public QM_OP_AUTHORITY(Path<? extends M_OP_AUTHORITY> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QM_OP_AUTHORITY(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QM_OP_AUTHORITY(PathMetadata metadata, PathInits inits) {
        this(M_OP_AUTHORITY.class, metadata, inits);
    }

    public QM_OP_AUTHORITY(Class<? extends M_OP_AUTHORITY> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.key = inits.isInitialized("key") ? new kr.co.neighbor21.neighborApi.entity.key.QM_OP_AUTHORITY_KEY(forProperty("key")) : null;
    }

}

