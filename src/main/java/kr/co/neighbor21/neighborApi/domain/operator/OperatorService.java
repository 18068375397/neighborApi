package kr.co.neighbor21.neighborApi.domain.operator;

import jakarta.transaction.Transactional;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.custom.ServiceException;
import kr.co.neighbor21.neighborApi.common.request.DynamicSearchRequest;
import kr.co.neighbor21.neighborApi.common.response.GenerateResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.domain.operator.record.*;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import kr.co.neighbor21.neighborApi.entity.key.M_OP_OPERATOR_KEY;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 운영자 Service
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Service
@RequiredArgsConstructor
public class OperatorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorService.class);

    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper = OperatorMapper.INSTANCE;

    /**
     * 운영자 정보 리스트 조회 (DynamicRequest 활용)
     *
     * @param parameter 운영자 조회 조건
     * @return OperatorSearchResponse 운영자 조회 결과 응답 결과
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Transactional
    public ResponseEntity<ItemsResponse<OperatorSearchResponse>> getOperatorListByDynamicRequest(DynamicSearchRequest parameter) throws ServiceException {
        GenerateResponse<OperatorSearchResponse> generateResponse = new GenerateResponse<>();
        generateResponse.setListCallback(() -> {
            List<M_OP_OPERATOR> list = operatorRepository.findDynamicWithPageable(parameter);
            return operatorMapper.toSearchResponseList(list);
        });
        generateResponse.setSizeCallback(() -> operatorRepository.countDynamic(parameter));
        return generateResponse.generateItemsWithPageResponse();
    }

    /**
     * 운영자 정보 리스트 조회
     *
     * @param parameter 운영자 조회 조건
     * @return OperatorSearchResponse 운영자 조회 결과 응답 결과
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Transactional
    public ResponseEntity<ItemsResponse<OperatorSearchResponse>> getOperatorList(OperatorSearchRequest parameter) throws ServiceException {
        GenerateResponse<OperatorSearchResponse> generateResponse = new GenerateResponse<>();
        return generateResponse.generateItemsResponse(() -> {
            /*namedNativeQuery 방식*/
//            return operatorRepository.findOperator(parameter.userId(), parameter.userName());

            /*queryDls 방식*/
//            QM_OP_OPERATOR operator = QM_OP_OPERATOR.m_OP_OPERATOR;
//            List<M_OP_OPERATOR> list = operatorRepository.getQueryFactory()
//                    .selectFrom(operator)
//                    .where(operator.key.userId.like(parameter.userId())
//                            .or(operator.userName.like(parameter.userName())))
//                    .fetch();
//            return operatorMapper.toSearchResponseList(list);

            /*queryMethod 방식*/
            List<M_OP_OPERATOR> list = operatorRepository.findByKeyUserIdLikeOrUserNameLike(
                    "%" + parameter.userId() + "%", "%" + parameter.userName() + "%");
            return operatorMapper.toSearchResponseList(list);
        });
    }

    /**
     * 운영자 정보 추가
     *
     * @param parameter 운영자 추가 요청 정보
     * @return OperatorCreateResponse 생성 된 운영자 정보 응답
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Transactional
    public ResponseEntity<ItemResponse<OperatorCreateResponse>> createOperator(OperatorCreateRequest parameter) throws ServiceException {
        GenerateResponse<OperatorCreateResponse> generateResponse = new GenerateResponse<>();
        return generateResponse.generateCreateResponse(() -> {
            M_OP_OPERATOR_KEY id = operatorMapper.toEntityKey(parameter);
            if (operatorRepository.existsById(id)) {
                throw new ServiceException(CommonErrorCode.ENTITY_DUPLICATED, null);
            }
            M_OP_OPERATOR createRequestEntity = operatorMapper.toEntity(parameter);
            M_OP_OPERATOR createdEntity = operatorRepository.save(createRequestEntity);
            return operatorMapper.toCreateResponse(createdEntity);
        });
    }

    /**
     * 운영자 정보 수정
     *
     * @param parameter 운영자 수정 요청 정보
     * @return OperatorModifyResponse 수정된 운영자 정보 응답
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Transactional
    public ResponseEntity<ItemResponse<OperatorModifyResponse>> modifyOperator(OperatorModifyRequest parameter) throws ServiceException {
        GenerateResponse<OperatorModifyResponse> generateResponse = new GenerateResponse<>();
        return generateResponse.generateModifyResponse(() -> {
            M_OP_OPERATOR_KEY entityKey = operatorMapper.toEntityKey(parameter);
            M_OP_OPERATOR entity = operatorRepository.findById(entityKey)
                    .orElseThrow(() -> new ServiceException(CommonErrorCode.ENTITY_NOT_FOUND, null));
            M_OP_OPERATOR modifiedEntity = operatorMapper.updateFromRequest(parameter, entity);
            operatorRepository.saveAndFlush(modifiedEntity);
            return operatorMapper.toModifyResponse(modifiedEntity);
        });
    }

    /**
     * 운영자 정보 삭제
     *
     * @param parameter 운영자 삭제 요청 정보
     * @return Long 삭제된 데이터 개수
     * @author GEONLEE
     * @since 2024-03-21<br />
     */
    @Transactional
    public ResponseEntity<ItemResponse<Long>> deleteOperator(OperatorDeleteRequest parameter) throws ServiceException {
        GenerateResponse<Long> generateResponse = new GenerateResponse<>();
        return generateResponse.generateDeleteResponse(() -> {
            M_OP_OPERATOR_KEY entityKey = operatorMapper.toEntityKey(parameter);
            M_OP_OPERATOR entity = operatorRepository.findById(entityKey)
                    .orElseThrow(() -> new ServiceException(CommonErrorCode.ENTITY_NOT_FOUND, null));
            operatorRepository.delete(entity);
            return 1L;
        });
    }
}
