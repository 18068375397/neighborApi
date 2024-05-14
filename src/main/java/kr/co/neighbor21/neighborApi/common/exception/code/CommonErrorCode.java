package kr.co.neighbor21.neighborApi.common.exception.code;

import lombok.Getter;

/**
 * 프로젝트 내에서 발생하는 공통 Exception Code, Message 관리
 *
 * @author GEONLEE
 * @since 2022-11-09<br />
 * 2023-03-21 GEONLEE - ID_VALIDATION 메시지 수정<br />
 * 2023-05-03 GEONLEE - ID_VALIDATION 메시지 수정<br />
 * 2023-10-13 GEONLEE - WRONG_DATA 메시지 수정<br />
 * 2023-11-28 GEONLEE - EXISTED_ID HttpStatus code 변경<br />
 * 2024-03-15 GEONLEE - RESOURCE_NOT_FOUND 제거<br />
 * 2024-03-29 GEONLEE - DUPLICATION_LOGIN 추가<br />
 * - API Code String 으로 변경<br />
 * 2024-03-29 GEONLEE - 에러 코드 체계 정리<br />
 */
@Getter
public enum CommonErrorCode implements ErrorCode {

    /*NS_ERR_01 -> Server exception*/
    SERVICE_ERROR("NS_ER_SV_01", "A problem occurred with the requested service. Please try again later."), /*Unchecked Exception*/
    LOGIC_ERROR("NS_ER_SV_02", "Processing data failed."), /*Checked exception*/
    /*NS_ERR_02 ->  Client exception*/
    URI_NOT_FOUND("NS_ER_CT_01", "The URI you requested could not be found."),
    METHOD_NOT_ALLOWED("NS_ER_CT_02", "The request method is not supported."),
    UNSUPPORTED_MEDIA_TYPE("NS_ER_CT_03", "The content type you requested is not supported."),
    INVALID_PARAMETER("NS_ER_CT_04", "Invalid parameters are passed."),
    REQUIRED_PARAMETER("NS_ER_CT_05", "Invalid parameters are passed."),
    NULL_POINTER("NS_ER_CT_06", "Invalid parameters are passed."),
    ENTITY_NOT_FOUND("NS_ER_CT_07", "Invalid parameters are passed."),
    ENTITY_DUPLICATED("NS_ER_CT_08", "Invalid parameters are passed."),
    /*NS_ERR_03 -> Authentication exception*/
    NOT_AUTHENTICATION("NS_ER_AT_01", "The credentials failed."),
    DUPLICATION_LOGIN("NS_ER_AT_02", "The credentials failed."),
    EXPIRED_TOKEN("NS_ER_AT_03", "The credentials failed."),
    NO_MATCHING_USER("NS_ER_AT_04", "The credentials failed."),
    WRONG_PASSWORD("NS_ER_AT_05", "The credentials failed."),
    /*NS_ERR_04 -> Authorization*/
    FORBIDDEN("NS_ER_FD", "User access is denied.");

    private final String resultCode;
    private final String resultMsg;

    CommonErrorCode(String rc, String rm) {
        resultCode = rc;
        resultMsg = rm;
    }
}
