package kr.co.neighbor21.neighborApi.domain.role;

import kr.co.neighbor21.neighborApi.common.jpa.mapStruct.MapstructConverter;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleSearchResponse;
import kr.co.neighbor21.neighborApi.entity.ROLE;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", imports = {MapstructConverter.class})
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleSearchResponse toSearchResponse(ROLE entity);

    List<RoleSearchResponse> toSearchResponseList(List<ROLE> entity);




//    ROLE updateFromRequest(RoleModifyRequest roleModifyRequest, @MappingTarget ROLE entity);

}
