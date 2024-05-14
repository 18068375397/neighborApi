package kr.co.neighbor21.neighborApi.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QM_OP_OPERATOR is a Querydsl query type for M_OP_OPERATOR
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QM_OP_OPERATOR extends EntityPathBase<M_OP_OPERATOR> {

    private static final long serialVersionUID = 1480337517L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QM_OP_OPERATOR m_OP_OPERATOR = new QM_OP_OPERATOR("m_OP_OPERATOR");

    public final kr.co.neighbor21.neighborApi.common.jpa.baseEntity.QBaseEntity _super = new kr.co.neighbor21.neighborApi.common.jpa.baseEntity.QBaseEntity(this);

    public final StringPath accessToken = createString("accessToken");

    public final QM_OP_AUTHORITY authority;

    public final StringPath cellphone = createString("cellphone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath department = createString("department");

    public final StringPath isUse = createString("isUse");

    public final kr.co.neighbor21.neighborApi.entity.key.QM_OP_OPERATOR_KEY key;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final StringPath password = createString("password");

    public final NumberPath<Long> passwordUpdateCycle = createNumber("passwordUpdateCycle", Long.class);

    public final DateTimePath<java.time.LocalDateTime> passwordUpdateDate = createDateTime("passwordUpdateDate", java.time.LocalDateTime.class);

    public final StringPath position = createString("position");

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath telephone = createString("telephone");

    public final StringPath userName = createString("userName");

    public QM_OP_OPERATOR(String variable) {
        this(M_OP_OPERATOR.class, forVariable(variable), INITS);
    }

    public QM_OP_OPERATOR(Path<? extends M_OP_OPERATOR> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QM_OP_OPERATOR(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QM_OP_OPERATOR(PathMetadata metadata, PathInits inits) {
        this(M_OP_OPERATOR.class, metadata, inits);
    }

    public QM_OP_OPERATOR(Class<? extends M_OP_OPERATOR> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.authority = inits.isInitialized("authority") ? new QM_OP_AUTHORITY(forProperty("authority"), inits.get("authority")) : null;
        this.key = inits.isInitialized("key") ? new kr.co.neighbor21.neighborApi.entity.key.QM_OP_OPERATOR_KEY(forProperty("key")) : null;
    }

}

