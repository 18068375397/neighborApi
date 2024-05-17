package kr.co.neighbor21.neighborApi.domain.role;

import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.custom.ServiceException;
import kr.co.neighbor21.neighborApi.common.response.GenerateResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.domain.role.record.*;
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

            List<ROLE> roleList = roleRepository.findAllByRoleNameLike("%" + parameter.roleName() + "%");
            return roleMapper.toSearchResponseList(roleList);
        });

    }

    public ResponseEntity<ItemResponse<RoleCreateResponse>> createRole(RoleCreateRequest parameter) {
        GenerateResponse<RoleCreateResponse> generateResponse = new GenerateResponse<>();
        return generateResponse.generateCreateResponse(() -> {

            if (roleRepository.findByRoleId(parameter.roleId()) != null) {
                throw new ServiceException(CommonErrorCode.ENTITY_DUPLICATED, null);
            }
            ROLE createRequestEntity = roleMapper.toEntity(parameter);

            ROLE createdEntity = roleRepository.save(createRequestEntity);
            return roleMapper.toCreateResponse(createdEntity);
        });
    }

    public ResponseEntity<ItemResponse<RoleModifyResponse>> modifyRole(RoleModifyRequest parameter) {
        GenerateResponse<RoleModifyResponse> generateResponse = new GenerateResponse<>();
        return generateResponse.generateModifyResponse(() -> {
            roleRepository.findById(Integer.valueOf(parameter.id()))
                    .orElseThrow(() -> new ServiceException(CommonErrorCode.ENTITY_NOT_FOUND, null));

            ROLE modifiedEntity = roleMapper.updateFromRequest(parameter);
            roleRepository.save(modifiedEntity);
            return roleMapper.toModifyResponse(modifiedEntity);
        });
    }

    public ResponseEntity<ItemResponse<Long>> deleteRole(RoleDeleteRequest parameter) {
        GenerateResponse<Long> generateResponse = new GenerateResponse<>();
        return generateResponse.generateDeleteResponse(() -> {
            ROLE entity = roleRepository.findById(Integer.valueOf(parameter.id()))
                    .orElseThrow(() -> new ServiceException(CommonErrorCode.ENTITY_NOT_FOUND, null));
            roleRepository.delete(entity);
            return 1L;
        });
    }
}
