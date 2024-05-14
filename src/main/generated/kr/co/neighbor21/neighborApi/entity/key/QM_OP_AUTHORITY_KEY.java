package kr.co.neighbor21.neighborApi.entity.key;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QM_OP_AUTHORITY_KEY is a Querydsl query type for M_OP_AUTHORITY_KEY
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QM_OP_AUTHORITY_KEY extends BeanPath<M_OP_AUTHORITY_KEY> {

    private static final long serialVersionUID = 695765865L;

    public static final QM_OP_AUTHORITY_KEY m_OP_AUTHORITY_KEY = new QM_OP_AUTHORITY_KEY("m_OP_AUTHORITY_KEY");

    public final StringPath authorityId = createString("authorityId");

    public QM_OP_AUTHORITY_KEY(String variable) {
        super(M_OP_AUTHORITY_KEY.class, forVariable(variable));
    }

    public QM_OP_AUTHORITY_KEY(Path<? extends M_OP_AUTHORITY_KEY> path) {
        super(path.getType(), path.getMetadata());
    }

    public QM_OP_AUTHORITY_KEY(PathMetadata metadata) {
        super(M_OP_AUTHORITY_KEY.class, metadata);
    }

}

