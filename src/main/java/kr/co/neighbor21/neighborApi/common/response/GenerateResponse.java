package kr.co.neighbor21.neighborApi.common.response;

import jakarta.persistence.PersistenceException;
import kr.co.neighbor21.neighborApi.common.contextHolder.ApplicationContextHolder;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.custom.ServiceException;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.config.message.MessageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * ResponseEntity 를 만들어주는 객체.<br />
 * 로직을 callback 에서 받아서 사용하므로, 로직은 service 에서 작성하고 여기에선 ResponseEntity 만 만들어줌.<br />
 * Sample - CodeService 참고.<br />
 * generateItemsResponse -> size 체크의 유무에 따라 generateItemsWithPageResponse
 * generateItemsResponse 로 분리.<br />
 *
 * @author jisu
 * @since 2023-07-28<br />
 * 2023-10-11 jisu - jpaDynamic exception 처리가 바뀜에따라 exception 변경 (DynamicRepository 에서 return true, false 등을 빼면서, id로 entity 를 찾는 과정 등을 DynamicRepository 에서 처리하기로 함. 이 과정에서의 변경사항.)<br />
 * 2023-10-11 jisu - generateItemsResponse 에 size 추가<br />
 * 2024-03-15 GEONLEE - insert, update, delete 시 결과 전달하도록 변경<br />
 * 2024-03-19 GEONLEE - ResponseStructure 사용에서 ItemsResponse, ItemResponse 로 분리<br />
 * 2024-03-22 GEONLEE - 전체 로직 개선<br />
 * 2024-03-30 GEONLEE - 에러 코드 정리에 따른 Exception 로그 출력 방식 개선, 로직 정리<br />
 */
public class GenerateResponse<DTO> {

    private final MessageConfig messageConfig = ApplicationContextHolder.getContext().getBean(MessageConfig.class);
    private final Logger LOGGER = LoggerFactory.getLogger(GenerateResponse.class);
    private Callback<List<DTO>> listResponseCallback;
    private Callback<Long> sizeResponseCallback;

    public void setListCallback(Callback<List<DTO>> responseCallback) {
        this.listResponseCallback = responseCallback;
    }

    public void setSizeCallback(Callback<Long> responseCallback) {
        this.sizeResponseCallback = responseCallback;
    }

    /**
     * Grid 등 size 가 있는 List 를 return 할 때 사용. ResponseEntity 를 return.<br />
     * 로직은 callback 으로 받음. listResponseCallback, sizeResponseCallback 이 필요함.<br />
     * setter 로 먼저 callback 을 넣어주고 실행해야함.<br />
     *
     * @author jisu
     * @since 2023-07-28<br />
     * 2024-03-15 GEONLEE - make -> generate 로 변경<br />
     */
    public ResponseEntity<ItemsResponse<DTO>> generateItemsWithPageResponse() {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("SEARCH.SUCCESS.MSG");
        List<DTO> list = null;
        Long size = 0L;
        try {
            try {
                size = sizeResponseCallback.call();
                if (size > 0) {
                    list = listResponseCallback.call();
                } else {
                    resultMsg = messageConfig.getMsg("NO.DATA.MSG");
                }
            } catch (PropertyReferenceException | NullPointerException e) {
                LOGGER.error("Wrong Reference or access null object.", e);
                throw new ServiceException(CommonErrorCode.NULL_POINTER, e);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ServiceException(CommonErrorCode.INVALID_PARAMETER, e.getCause());
            } catch (PersistenceException e) {
                LOGGER.error("PersistenceException", e);
                throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
            }
        } catch (ServiceException e) {
            LOGGER.error("[{}] {}", e.errorCode.getResultCode(), e.errorCode, e);
            resultCode = messageConfig.getCode("FAIL.CODE");
            resultMsg = messageConfig.getMsg("SEARCH.FAIL.MSG");
            size = null;
        }
        return ResponseEntity.ok()
                .body(ItemsResponse.<DTO>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .size(size)
                        .items(list)
                        .build());
    }

    /**
     * List 인데 siz e를 체크할 필요 없는 list 를 return 할 때 사용. ResponseEntity 를 return.<br />
     * 로직은 callback 파라메터로 받음.
     *
     * @author jisu
     * @since 2023-07-31<br />
     * 2024-03-15 GEONLEE - make -> generate 로 변경<br />
     */
    public ResponseEntity<ItemsResponse<DTO>> generateItemsResponse(Callback<List<DTO>> callback) {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("SEARCH.SUCCESS.MSG");
        List<DTO> list = null;
        Long size = null;
        try {
            try {
                list = callback.call();
                size = (long) list.size();
                if (list.size() == 0) {
                    resultMsg = messageConfig.getMsg("NO.DATA.MSG");
                }
            } catch (PropertyReferenceException e) {
                LOGGER.error("Wrong Reference. \n", e);
                throw new ServiceException(CommonErrorCode.NULL_POINTER, e);
            } catch (NullPointerException e) {
                LOGGER.error("Access null variable.\n", e);
                throw new ServiceException(CommonErrorCode.NULL_POINTER, e);
            } catch (ArrayIndexOutOfBoundsException e) {
                LOGGER.error("Date type check\n", e);
                throw new ServiceException(CommonErrorCode.NULL_POINTER, e);
            } catch (PersistenceException e) {
                LOGGER.error("PersistenceException", e);
                throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
            }
        } catch (ServiceException e) {
            LOGGER.error("[{}] {}", e.errorCode.getResultCode(), e.errorCode, e);
            resultCode = messageConfig.getCode("FAIL.CODE");
            resultMsg = messageConfig.getMsg("SEARCH.FAIL.MSG");
        }
        return ResponseEntity.ok()
                .body(ItemsResponse.<DTO>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .items(list)
                        .size(size)
                        .build());
    }

    /**
     * 객체 하나만 return 할때 사용. ResponseEntity 를 return.<br />
     * callback 에서 CustomNoDataException 을 던지면,<br />
     * resultCode 200, resultMsg Fail 이 나간다.<br />
     *
     * @param callback 실행될 콜백 lambda. DTO 를 return
     * @author jisu
     * @since 2023-07-28<br />
     * 2024-03-15 GEONLEE - make -> generate 로 변경<br />
     * 2023-03-30 GEONLEE - CustomNoDataException 제거<br />
     */
    public ResponseEntity<ItemResponse<DTO>> generateItemResponse(Callback<DTO> callback) {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("SEARCH.SUCCESS.MSG");
        DTO item = null;
        try {
            try {
                item = callback.call();
            } catch (PersistenceException e) {
                LOGGER.error(e.getMessage());
                throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
            }
            if (item == null) {
                resultMsg = messageConfig.getMsg("NO.DATA.MSG");
            }
        } catch (ServiceException e) {
            LOGGER.error("[{}] {}", e.errorCode.getResultCode(), e.errorCode, e);
            resultCode = messageConfig.getCode("FAIL.CODE");
            resultMsg = messageConfig.getMsg("SEARCH.FAIL.MSG");
        }
        return ResponseEntity.ok()
                .body(ItemResponse.<DTO>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .item(item)
                        .build());
    }

    /**
     * insert 할 때 사용. ResponseEntity 를 return.
     *
     * @param callback 실행될 콜백 lambda.
     * @author jisu
     * @since 2023-07-28
     * 2023.08.07 jisu - callback 이 반드시 true|false 를 return 하도록 되여있어서<br />
     * Callable callback -> VoidCallback callback 으로 바꿈.<br />
     * 2024-03-15 GEONLEE - ResponseDTO 객체 전달 하도록 VoidCallback -> Callback 으로 변경<br />
     * 2023-03-30 GEONLEE - CustomNoDataException 제거<br />
     */
    public ResponseEntity<ItemResponse<DTO>> generateCreateResponse(Callback<DTO> callback) {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("INSERT.SUCCESS.MSG");
        DTO item = null;
        try {
            try {
                item = callback.call();
            } catch (PersistenceException e) {
                LOGGER.error(e.getMessage(), e);
                throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
            }
        } catch (ServiceException e) {
            LOGGER.error("[{}] {}", e.errorCode.getResultCode(), e.errorCode, e);
            resultCode = e.errorCode.getResultCode();
            resultMsg = messageConfig.getMsg("INSERT.FAIL.MSG");
        }
        return ResponseEntity.ok()
                .body(ItemResponse.<DTO>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .item(item)
                        .build());
    }

    /**
     * update 할 때 사용. ResponseEntity 를 return.
     *
     * @param callback 실행될 콜백 lambda.
     * @author jisu
     * @since 2023-07-28<br />
     * 2023.08.07 jisu - callback 이 반드시 true|false 를 return 하도록 되여있어서<br />
     * Callable callback -> VoidCallback callback 으로 바꿈.<br />
     * 2024-03-15 GEONLEE - ResponseDTO 객체 전달 하도록 VoidCallback -> Callback 으로 변경<br />
     * 2023-03-30 GEONLEE - CustomNoDataException 제거<br />
     */
    public ResponseEntity<ItemResponse<DTO>> generateModifyResponse(Callback<DTO> callback) {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("UPDATE.SUCCESS.MSG");
        DTO item = null;
        try {
            try {
                item = callback.call();
            } catch (PersistenceException e) {
                LOGGER.error(e.getMessage());
                throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
            }
            if (item == null) {
                resultCode = messageConfig.getCode("FAIL.CODE");
                resultMsg = messageConfig.getMsg("UPDATE.FAIL.MSG");
            }
        } catch (ServiceException e) {
            LOGGER.error("[{}] {}", e.errorCode.getResultCode(), e.errorCode, e);
            resultCode = messageConfig.getCode("FAIL.CODE");
            resultMsg = messageConfig.getMsg("UPDATE.FAIL.MSG");
        }
        return ResponseEntity.ok()
                .body(ItemResponse.<DTO>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .item(item)
                        .build());
    }

    /**
     * delete 할 때 사용. ResponseEntity 를 return.
     *
     * @param callback 실행될 콜백 lambda.
     * @author jisu
     * @since 2023-07-28<br />
     * 2023.08.07 jisu - callback 이 반드시 true|false 를 return 하도록 되어있어서<br />
     * Callable callback -> VoidCallback callback 으로 바꿈.<br />
     * 2024-03-15 GEONLEE - 삭제된 개수 전달 하도록 VoidCallback -> Callback 으로 변경<br />
     * 2023-03-30 GEONLEE - CustomNoDataException 제거<br />
     */
    public ResponseEntity<ItemResponse<Long>> generateDeleteResponse(Callback<Long> callback) {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("DELETE.SUCCESS.MSG");
        Long deleteSize = null;
        try {
            deleteSize = callback.call();
        } catch (ServiceException e) {
            LOGGER.error("[{}] {}", e.errorCode.getResultCode(), e.errorCode, e);
            resultCode = messageConfig.getCode("FAIL.CODE");
            resultMsg = messageConfig.getMsg("DELETE.FAIL.MSG");
        }
        return ResponseEntity.ok()
                .body(ItemResponse.<Long>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .item(deleteSize)
                        .build());
    }
}
