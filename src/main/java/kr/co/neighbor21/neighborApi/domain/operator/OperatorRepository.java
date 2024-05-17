package kr.co.neighbor21.neighborApi.domain.operator;

import kr.co.neighbor21.neighborApi.common.jpa.querydsl.repository.JpaDynamicRepository;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorSearchResponse;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * 운영자 Repository
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Repository
public interface OperatorRepository extends JpaDynamicRepository<M_OP_OPERATOR, BigInteger> {

    List<M_OP_OPERATOR> findByUserIdLikeOrUserNameLike(String userId, String userName);

    @Query(nativeQuery = true)
    List<OperatorSearchResponse> findOperator(@Param("userId") String userId, @Param("userName") String userName);

    boolean existsByUserId(String userId);

    M_OP_OPERATOR findOneByUserId(String userId);

    M_OP_OPERATOR findOneByUserIdAndRefreshToken(String userId, String refreshToken);

    M_OP_OPERATOR findOneByUserIdAndAccessToken(String userId, String accessToken);
}
