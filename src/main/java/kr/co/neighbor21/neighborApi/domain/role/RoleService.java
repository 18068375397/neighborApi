package kr.co.neighbor21.neighborApi.domain.role;

import kr.co.neighbor21.neighborApi.common.response.GenerateResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleCreateRequest;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleCreateResponse;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleSearchRequest;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleSearchResponse;
import kr.co.neighbor21.neighborApi.entity.ROLE;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper = RoleMapper.INSTANCE;

    public ResponseEntity<ItemsResponse<RoleSearchResponse>> getRoleList(RoleSearchRequest parameter) {
        GenerateResponse<RoleSearchResponse> generateResponse = new GenerateResponse<>();
        return generateResponse.generateItemsResponse(() -> {

            List<ROLE> roleList = roleRepository.findAllByRoleNameLike("%" + parameter.name() + "%");
            return roleMapper.toSearchResponseList(roleList);
        });

    }

    public ResponseEntity<ItemsResponse<RoleCreateResponse>> createRole(RoleCreateRequest parameter) {
        return null;
    }
}
