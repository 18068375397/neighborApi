package kr.co.neighbor21.neighborApi.domain.authority;

import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface AuthorityRepository extends JpaRepository<M_OP_AUTHORITY, BigInteger> {

    List<M_OP_AUTHORITY> findAllByAuthorityNameLike(String name);
}
