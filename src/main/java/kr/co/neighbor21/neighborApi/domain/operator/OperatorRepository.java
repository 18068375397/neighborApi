package kr.co.neighbor21.neighborApi.domain.operator;

import kr.co.neighbor21.neighborApi.common.jpa.querydsl.repository.JpaDynamicRepository;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorSearchResponse;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import kr.co.neighbor21.neighborApi.entity.key.M_OP_OPERATOR_KEY;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 운영자 Repository
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Repository
public interface OperatorRepository extends JpaDynamicRepository<M_OP_OPERATOR, M_OP_OPERATOR_KEY> {
    Optional<M_OP_OPERATOR> findOneByKeyUserId(String userId);

    Optional<M_OP_OPERATOR> findOneByKeyUserIdAndAccessToken(String userId, String accessToken);

    Optional<M_OP_OPERATOR> findOneByKeyUserIdAndRefreshToken(String userId, String refreshToken);

    List<M_OP_OPERATOR> findByKeyUserIdLikeOrUserNameLike(String userId, String userName);

    @Query(nativeQuery = true)
    List<OperatorSearchResponse> findOperator(@Param("userId") String userId, @Param("userName") String userName);
}
