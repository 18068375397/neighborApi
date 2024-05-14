package kr.co.neighbor21.neighborApi.domain.operator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.neighbor21.neighborApi.common.jpa.mapStruct.MapstructConverter;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorCreateRequest;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorCreateResponse;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorDeleteRequest;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorModifyRequest;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorModifyResponse;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorSearchResponse;
import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import kr.co.neighbor21.neighborApi.entity.key.M_OP_AUTHORITY_KEY;
import kr.co.neighbor21.neighborApi.entity.key.M_OP_OPERATOR_KEY;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-13T15:27:33+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class OperatorMapperImpl implements OperatorMapper {

    @Override
    public OperatorSearchResponse toSearchResponse(M_OP_OPERATOR entity) {
        if ( entity == null ) {
            return null;
        }

        String userId = null;
        String authorityId = null;
        String authorityName = null;
        String userName = null;
        String telephone = null;
        String cellphone = null;
        String position = null;
        String department = null;

        userId = entityKeyUserId( entity );
        authorityId = entityAuthorityKeyAuthorityId( entity );
        authorityName = entityAuthorityAuthorityName( entity );
        userName = entity.getUserName();
        telephone = entity.getTelephone();
        cellphone = entity.getCellphone();
        position = entity.getPosition();
        department = entity.getDepartment();

        Boolean isUse = MapstructConverter.stringToBoolean(entity.getIsUse());
        String modifyDate = MapstructConverter.localDateTimeToString(entity.getModifyDate());

        OperatorSearchResponse operatorSearchResponse = new OperatorSearchResponse( userId, userName, authorityId, authorityName, telephone, cellphone, position, department, modifyDate, isUse );

        return operatorSearchResponse;
    }

    @Override
    public List<OperatorSearchResponse> toSearchResponseList(List<M_OP_OPERATOR> entity) {
        if ( entity == null ) {
            return null;
        }

        List<OperatorSearchResponse> list = new ArrayList<OperatorSearchResponse>( entity.size() );
        for ( M_OP_OPERATOR m_OP_OPERATOR : entity ) {
            list.add( toSearchResponse( m_OP_OPERATOR ) );
        }

        return list;
    }

    @Override
    public OperatorCreateResponse toCreateResponse(M_OP_OPERATOR entity) {
        if ( entity == null ) {
            return null;
        }

        String userId = null;
        String authorityName = null;
        String userName = null;
        String telephone = null;
        String cellphone = null;
        String position = null;
        String department = null;

        userId = entityKeyUserId( entity );
        authorityName = entityAuthorityAuthorityName( entity );
        userName = entity.getUserName();
        telephone = entity.getTelephone();
        cellphone = entity.getCellphone();
        position = entity.getPosition();
        department = entity.getDepartment();

        Boolean isUse = MapstructConverter.stringToBoolean(entity.getIsUse());
        String modifyDate = MapstructConverter.localDateTimeToString(entity.getModifyDate());

        OperatorCreateResponse operatorCreateResponse = new OperatorCreateResponse( userId, userName, authorityName, telephone, cellphone, position, department, modifyDate, isUse );

        return operatorCreateResponse;
    }

    @Override
    public OperatorModifyResponse toModifyResponse(M_OP_OPERATOR entity) {
        if ( entity == null ) {
            return null;
        }

        String userId = null;
        String authorityName = null;
        String userName = null;
        String telephone = null;
        String cellphone = null;
        String position = null;
        String department = null;

        userId = entityKeyUserId( entity );
        authorityName = entityAuthorityAuthorityName( entity );
        userName = entity.getUserName();
        telephone = entity.getTelephone();
        cellphone = entity.getCellphone();
        position = entity.getPosition();
        department = entity.getDepartment();

        Boolean isUse = MapstructConverter.stringToBoolean(entity.getIsUse());
        String modifyDate = MapstructConverter.localDateTimeToString(entity.getModifyDate());

        OperatorModifyResponse operatorModifyResponse = new OperatorModifyResponse( userId, userName, authorityName, telephone, cellphone, position, department, modifyDate, isUse );

        return operatorModifyResponse;
    }

    @Override
    public M_OP_OPERATOR_KEY toEntityKey(OperatorCreateRequest operatorModifyRequest) {
        if ( operatorModifyRequest == null ) {
            return null;
        }

        M_OP_OPERATOR_KEY m_OP_OPERATOR_KEY = new M_OP_OPERATOR_KEY();

        m_OP_OPERATOR_KEY.setUserId( operatorModifyRequest.userId() );

        return m_OP_OPERATOR_KEY;
    }

    @Override
    public M_OP_OPERATOR_KEY toEntityKey(OperatorDeleteRequest operatorDeleteRequest) {
        if ( operatorDeleteRequest == null ) {
            return null;
        }

        M_OP_OPERATOR_KEY m_OP_OPERATOR_KEY = new M_OP_OPERATOR_KEY();

        m_OP_OPERATOR_KEY.setUserId( operatorDeleteRequest.userId() );

        return m_OP_OPERATOR_KEY;
    }

    @Override
    public M_OP_OPERATOR_KEY toEntityKey(OperatorModifyRequest operatorModifyRequest) {
        if ( operatorModifyRequest == null ) {
            return null;
        }

        M_OP_OPERATOR_KEY m_OP_OPERATOR_KEY = new M_OP_OPERATOR_KEY();

        m_OP_OPERATOR_KEY.setUserId( operatorModifyRequest.userId() );

        return m_OP_OPERATOR_KEY;
    }

    @Override
    public M_OP_OPERATOR toEntity(OperatorCreateRequest operatorCreateRequest) {
        if ( operatorCreateRequest == null ) {
            return null;
        }

        M_OP_OPERATOR m_OP_OPERATOR = new M_OP_OPERATOR();

        m_OP_OPERATOR.setKey( operatorCreateRequestToM_OP_OPERATOR_KEY( operatorCreateRequest ) );
        m_OP_OPERATOR.setAuthority( operatorCreateRequestToM_OP_AUTHORITY( operatorCreateRequest ) );
        if ( operatorCreateRequest.passwordUpdateDate() != null ) {
            m_OP_OPERATOR.setPasswordUpdateDate( LocalDateTime.parse( operatorCreateRequest.passwordUpdateDate(), DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" ) ) );
        }
        m_OP_OPERATOR.setPassword( operatorCreateRequest.password() );
        m_OP_OPERATOR.setUserName( operatorCreateRequest.userName() );
        m_OP_OPERATOR.setTelephone( operatorCreateRequest.telephone() );
        m_OP_OPERATOR.setCellphone( operatorCreateRequest.cellphone() );
        m_OP_OPERATOR.setPosition( operatorCreateRequest.position() );
        m_OP_OPERATOR.setDepartment( operatorCreateRequest.department() );
        m_OP_OPERATOR.setPasswordUpdateCycle( operatorCreateRequest.passwordUpdateCycle() );

        m_OP_OPERATOR.setIsUse( MapstructConverter.booleanToString(operatorCreateRequest.isUse()) );

        return m_OP_OPERATOR;
    }

    @Override
    public M_OP_OPERATOR updateFromRequest(OperatorModifyRequest operatorModifyRequest, M_OP_OPERATOR entity) {
        if ( operatorModifyRequest == null ) {
            return null;
        }

        if ( entity.getKey() == null ) {
            entity.setKey( new M_OP_OPERATOR_KEY() );
        }
        operatorModifyRequestToM_OP_OPERATOR_KEY( operatorModifyRequest, entity.getKey() );
        entity.setUserName( operatorModifyRequest.userName() );
        entity.setTelephone( operatorModifyRequest.telephone() );
        entity.setCellphone( operatorModifyRequest.cellphone() );
        entity.setPosition( operatorModifyRequest.position() );
        entity.setDepartment( operatorModifyRequest.department() );

        entity.setIsUse( MapstructConverter.booleanToString(operatorModifyRequest.isUse()) );

        return entity;
    }

    private String entityKeyUserId(M_OP_OPERATOR m_OP_OPERATOR) {
        if ( m_OP_OPERATOR == null ) {
            return null;
        }
        M_OP_OPERATOR_KEY key = m_OP_OPERATOR.getKey();
        if ( key == null ) {
            return null;
        }
        String userId = key.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    private String entityAuthorityKeyAuthorityId(M_OP_OPERATOR m_OP_OPERATOR) {
        if ( m_OP_OPERATOR == null ) {
            return null;
        }
        M_OP_AUTHORITY authority = m_OP_OPERATOR.getAuthority();
        if ( authority == null ) {
            return null;
        }
        M_OP_AUTHORITY_KEY key = authority.getKey();
        if ( key == null ) {
            return null;
        }
        String authorityId = key.getAuthorityId();
        if ( authorityId == null ) {
            return null;
        }
        return authorityId;
    }

    private String entityAuthorityAuthorityName(M_OP_OPERATOR m_OP_OPERATOR) {
        if ( m_OP_OPERATOR == null ) {
            return null;
        }
        M_OP_AUTHORITY authority = m_OP_OPERATOR.getAuthority();
        if ( authority == null ) {
            return null;
        }
        String authorityName = authority.getAuthorityName();
        if ( authorityName == null ) {
            return null;
        }
        return authorityName;
    }

    protected M_OP_OPERATOR_KEY operatorCreateRequestToM_OP_OPERATOR_KEY(OperatorCreateRequest operatorCreateRequest) {
        if ( operatorCreateRequest == null ) {
            return null;
        }

        M_OP_OPERATOR_KEY m_OP_OPERATOR_KEY = new M_OP_OPERATOR_KEY();

        m_OP_OPERATOR_KEY.setUserId( operatorCreateRequest.userId() );

        return m_OP_OPERATOR_KEY;
    }

    protected M_OP_AUTHORITY_KEY operatorCreateRequestToM_OP_AUTHORITY_KEY(OperatorCreateRequest operatorCreateRequest) {
        if ( operatorCreateRequest == null ) {
            return null;
        }

        M_OP_AUTHORITY_KEY m_OP_AUTHORITY_KEY = new M_OP_AUTHORITY_KEY();

        m_OP_AUTHORITY_KEY.setAuthorityId( operatorCreateRequest.authorityId() );

        return m_OP_AUTHORITY_KEY;
    }

    protected M_OP_AUTHORITY operatorCreateRequestToM_OP_AUTHORITY(OperatorCreateRequest operatorCreateRequest) {
        if ( operatorCreateRequest == null ) {
            return null;
        }

        M_OP_AUTHORITY m_OP_AUTHORITY = new M_OP_AUTHORITY();

        m_OP_AUTHORITY.setKey( operatorCreateRequestToM_OP_AUTHORITY_KEY( operatorCreateRequest ) );

        return m_OP_AUTHORITY;
    }

    protected void operatorModifyRequestToM_OP_OPERATOR_KEY(OperatorModifyRequest operatorModifyRequest, M_OP_OPERATOR_KEY mappingTarget) {
        if ( operatorModifyRequest == null ) {
            return;
        }

        mappingTarget.setUserId( operatorModifyRequest.userId() );
    }
}
