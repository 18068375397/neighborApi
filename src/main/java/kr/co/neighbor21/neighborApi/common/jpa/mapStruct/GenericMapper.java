package kr.co.neighbor21.neighborApi.common.jpa.mapStruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper interface 생성 시 기본적으로 생성해야될 method 설정 mapper<br />
 * Mapper 에서 extends 받으면서 mapStruct 에서 자동으로 구현체를 생성한다.<br />
 * ex) public interface ActivityMapper extends GenericMapper<사용 할 DTO, 사용 할Entity><br />
 *
 * @author GEONLEE
 * @since 2023-01-30
 * 2023-03-15 GEONLEE - 자동 생성 처리로 @Deprecated 추가
 */
//@Deprecated
public interface GenericMapper<DTO, Entity> {

    DTO toDTO(Entity entity);

    Entity toEntity(DTO dto);

    ArrayList<DTO> toDtoList(List<Entity> list);

    ArrayList<Entity> toEntityList(List<DTO> dtoList);

    /**
     * Null 값이 전달될 경우 변화 시키지 않도록 설정
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(DTO dto, @MappingTarget Entity entity);
}