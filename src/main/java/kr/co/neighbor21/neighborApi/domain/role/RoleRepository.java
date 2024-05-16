package kr.co.neighbor21.neighborApi.domain.role;

import kr.co.neighbor21.neighborApi.entity.ROLE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<ROLE, Integer> {

//    @Query(nativeQuery = true, value = "SELECT * FROM role r where r.name = ?1")
//    List<ROLE> getRoleList(@Param("name") String name);

    List<ROLE> findAllByRoleNameLike(String name);
}