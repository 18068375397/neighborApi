package kr.co.neighbor21.neighborApi.domain.role;

import kr.co.neighbor21.neighborApi.entity.ROLE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface RoleRepository extends JpaRepository<ROLE, BigInteger> {

//    @Query(nativeQuery = true, value = "SELECT * FROM role r where r.name = ?1")
//    List<ROLE> getRoleList(@Param("name") String name);

    List<ROLE> findAllByRoleNameLike(String name);

    ROLE findByRoleId(String s);
}
