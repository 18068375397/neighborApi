package kr.co.neighbor21.neighborApi.domain.role;

import kr.co.neighbor21.neighborApi.common.jpa.mapStruct.MapstructConverter;
import kr.co.neighbor21.neighborApi.domain.role.record.*;
import kr.co.neighbor21.neighborApi.entity.ROLE;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", imports = {MapstructConverter.class})
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleSearchResponse toSearchResponse(ROLE entity);

    List<RoleSearchResponse> toSearchResponseList(List<ROLE> entity);

    ROLE toEntity(RoleCreateRequest entity);

    RoleCreateResponse toCreateResponse(ROLE entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mappings({
            @Mapping(target = "roleId", source = "parameter.roleId"),
            @Mapping(target = "roleName", source = "parameter.roleName"),
            @Mapping(target = "authorities", source = "parameter.authorities"),
            @Mapping(target = "id", source = "parameter.id"),
    })
    ROLE updateFromRequest(RoleModifyRequest parameter);

    RoleModifyResponse toModifyResponse(ROLE modifiedEntity);


//    ROLE updateFromRequest(RoleModifyRequest roleModifyRequest, @MappingTarget ROLE entity);

}
