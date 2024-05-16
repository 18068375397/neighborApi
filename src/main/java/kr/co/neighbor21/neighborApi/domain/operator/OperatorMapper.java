package kr.co.neighbor21.neighborApi.domain.operator;

import kr.co.neighbor21.neighborApi.common.jpa.mapStruct.MapstructConverter;
import kr.co.neighbor21.neighborApi.domain.operator.record.*;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import kr.co.neighbor21.neighborApi.entity.key.M_OP_OPERATOR_KEY;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 운영자 Mapper
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Mapper(componentModel = "spring", imports = {MapstructConverter.class})
public interface OperatorMapper /*extends GenericMapper<OperatorSearchResponseDTO, M_OP_OPERATOR>*/ {
    OperatorMapper INSTANCE = Mappers.getMapper(OperatorMapper.class);

    /**
     * 운영자 Entity to SearchResponse 변환 메서드
     *
     * @author GEONLEE
     * @since 2024-03-21생성<br />
     */
    @Mappings({
            @Mapping(target = "userId", source = "key.userId"),
            @Mapping(target = "isUse", expression = "java(MapstructConverter.stringToBoolean(entity.getIsUse()))"),
            @Mapping(target = "modifyDate", expression = "java(MapstructConverter.localDateTimeToString(entity.getModifyDate()))"),
    })
    OperatorSearchResponse toSearchResponse(M_OP_OPERATOR entity);

    /**
     * 운영자 List<Entity> to List<OperatorSearchResponse> 변환 메서드<br />
     * MapStruct 에서 toSearchResponse 메서드 를 활용 하기 때문에 따로 매핑 불필요.
     *
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    List<OperatorSearchResponse> toSearchResponseList(List<M_OP_OPERATOR> entity);

    /**
     * 운영자 Entity to OperatorCreateResponse 변환 메서드<br />
     *
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Mappings({
            @Mapping(target = "userId", source = "key.userId"),
            @Mapping(target = "isUse", expression = "java(MapstructConverter.stringToBoolean(entity.getIsUse()))"),
            @Mapping(target = "modifyDate", expression = "java(MapstructConverter.localDateTimeToString(entity.getModifyDate()))")
    })
    OperatorCreateResponse toCreateResponse(M_OP_OPERATOR entity);

    /**
     * 운영자 Entity to OperatorModifyResponse 변환 메서드<br />
     *
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Mappings({
            @Mapping(target = "userId", source = "key.userId"),
            @Mapping(target = "isUse", expression = "java(MapstructConverter.stringToBoolean(entity.getIsUse()))"),
            @Mapping(target = "modifyDate", expression = "java(MapstructConverter.localDateTimeToString(entity.getModifyDate()))"),
    })
    OperatorModifyResponse toModifyResponse(M_OP_OPERATOR entity);

    /**
     * 추가 시 키 추출용 메서드
     *
     * @author GEONLEE
     * @since 2024-04-01<br />
     */
    M_OP_OPERATOR_KEY toEntityKey(OperatorCreateRequest operatorModifyRequest);

    /**
     * 삭제 시 키 추출용 메서드
     *
     * @author GEONLEE
     * @since 2024-04-01<br />
     */
    M_OP_OPERATOR_KEY toEntityKey(OperatorDeleteRequest operatorDeleteRequest);

    /**
     * 수정 시 키 추출용 메서드
     *
     * @author GEONLEE
     * @since 2024-04-01<br />
     */
    M_OP_OPERATOR_KEY toEntityKey(OperatorModifyRequest operatorModifyRequest);


    /**
     * OperatorCreateRequest to 운영자 Entity 변환 메서드<br />
     * 복수 건을 저장하고 싶을 경우에는 toEntityCreateList 를 추가<br />
     * List<M_OP_OPERATOR> toEntityCreateResponseList(List<OperatorCreateRequest> operatorCreateRequest);
     *
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Mappings({
            @Mapping(target = "key.userId", source = "userId"),
            @Mapping(target = "isUse", expression = "java(MapstructConverter.booleanToString(operatorCreateRequest.isUse()))"),
            @Mapping(target = "passwordUpdateDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    M_OP_OPERATOR toEntity(OperatorCreateRequest operatorCreateRequest);

    /**
     * record 로 update 처리
     * 호출 만으로 entity 변환과 save 작업 수행
     *
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mappings({
            @Mapping(target = "key.userId", source = "userId"),
            @Mapping(target = "isUse", expression = "java(MapstructConverter.booleanToString(operatorModifyRequest.isUse()))"),
    })
    M_OP_OPERATOR updateFromRequest(OperatorModifyRequest operatorModifyRequest, @MappingTarget M_OP_OPERATOR entity);
}
