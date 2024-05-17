package kr.co.neighbor21.neighborApi.domain.authority;

import kr.co.neighbor21.neighborApi.common.jpa.mapStruct.MapstructConverter;
import kr.co.neighbor21.neighborApi.domain.authority.record.AuthoritySearchResponse;
import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", imports = {MapstructConverter.class})
public interface AuthorityMapper {
    AuthorityMapper INSTANCE = Mappers.getMapper(AuthorityMapper.class);


    List<AuthoritySearchResponse> toSearchResponseList(List<M_OP_AUTHORITY> authorities);
}
