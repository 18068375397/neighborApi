package kr.co.neighbor21.neighborApi.common.exception;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.PersistenceException;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.code.ErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.custom.ServiceException;
import kr.co.neighbor21.neighborApi.common.exception.custom.UnauthorizedException;
import kr.co.neighbor21.neighborApi.common.response.structure.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.time.DateTimeException;
import java.util.List;
import java.util.StringJoiner;

/**
 * 전역 예외 처리 클래스, Exception 별로 구분하여 처리
 * ResponseEntityExceptionHandler 를 extends 받아 처리하는 것이 효율적일지 고민 필요.
 *
 * @author GEONLEE
 * @since 2022-11-09<br />
 * 2022-11-14 GEONLEE - 응답 Header 에 Status, Content-type 추가, InternalAuthenticationServiceException (활성화, 권한정보 없음)<br />
 * - BadCredentialsException (id, pw 다름) 통합<br />
 * 2023-02-06 GEONLEE - IllegalArgumentException 에 같은 로직으로 PropertyReferenceException(org.springframework.data.mapping) 추가<br />
 * 2023-02-21 GEONLEE - InvalidDataAccessApiUsageException 추가<br />
 * 2023-02-22 GEONLEE - IOException 추가 (카카오, 네이버 로그인)<br />
 * 2023-03-02 GEONLEE - ConstraintViolationException 추가 (VALIDATION 관련)<br />
 * 2023-03-21 GEONLEE - DataIntegrityViolationException 응답 메시지를 서비스 오류로 변경(로그는 그대로)<br />
 * 2023-05-12 GEONLEE - KT 검증 사항 적용, HTTP 응답은 200, 자세한 상태코드는 BODY 메시지로 전달, MethodArgumentNotValidException 추가로 @Valid 공통 처리<br />
 * 2023-10-13 GEONLEE - ServiceException 분기 (전달받은 메시지로 처리 하기 위해) RuntimeException 주석 처리<br />
 * 2024-03-15 GEONLEE - @RestControllerAdvice 으로 변경, makeErrorResponse method 삭제, 모든 메서드에서 리턴 Object 를 ErrorResponse 로 변경<br />
 * 2024-03-20 GEONLEE - 전체 코드 정리<br />
 * 2024-03-26 GEONLEE - 500 에러 ClassCastException 추가 (AOP에서 발생 가능성 있음)<br />
 * 2024-03-29 GEONLEE - 코드체계 변경에 따른 처리<br />
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Internal Server error 처리용<br />
     * 서버 내부 오류<br />
     *
     * @author GEONLEE
     * @since 2022-11-09<br />
     */
    @ExceptionHandler(value = {ServiceException.class, IOException.class, NoSuchMethodError.class, DateTimeException.class,
            PersistenceException.class, TransactionException.class, ClassCastException.class, JwtException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) /*ErrorResponse 를 스키마에 등록하기 위해 사용*/
    public ResponseEntity<ErrorResponse> handleIOException(Exception e) {
        ErrorCode errorCode = CommonErrorCode.SERVICE_ERROR;
        return handleExceptionInternal(errorCode, e);
    }

    /**
     * 415 지원하지 않는 http method 에러 처리
     *
     * @author GEONLEE
     * @since 2022-11-09<br />
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
//    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaTypeStatusException(Exception e) {
        ErrorCode errorCode = CommonErrorCode.UNSUPPORTED_MEDIA_TYPE;
        return handleExceptionInternal(errorCode, e);
    }

    /**
     * 자격증명 실패 관련 통합 처리<br />
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    @ExceptionHandler(UnauthorizedException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        return handleExceptionInternal(e.code(), e);
    }

    /**
     * 매핑된 주소가 없을 경우 처리<br />
     * swagger 표출 안됨.
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    @ExceptionHandler(NoHandlerFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoHandlerFoundException e) {
        ErrorCode errorCode = CommonErrorCode.URI_NOT_FOUND;
        return handleExceptionInternal(errorCode, e);
    }

    /**
     * 요청한 HTTP Method 가 올바르지 않을 경우 처리<br />
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
//    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = CommonErrorCode.METHOD_NOT_ALLOWED;
        return handleExceptionInternal(errorCode, e);
    }

//    @ExceptionHandler(DuplicateKeyException.class)
////    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(Exception e) {
//        ErrorCode errorCode = CommonErrorCode.EXISTED_ID;
//        LOGGER.error(errorCode.getResultMsg(), e);
//        return handleExceptionInternal(errorCode);
//    }

    /**
     * 잘못된 파라미터 관련 에러 처리<br />
     * 클라이언트 오류<br />
     *
     * @author GEONLEE
     * @since 2023-03-06<br />
     */
    @ExceptionHandler(value = {HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgument(Exception e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e);
    }

    /**
     * Valid 어노테이션에서 발생하는 에러 로그 처리<br />
     * 클라이언트 오류<br />
     *
     * @author GEONLEE
     * @since 2022-11-09<br />
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder stringBuilder = new StringBuilder();
        StringJoiner stringJoiner = new StringJoiner(",");
        LOGGER.error("======================@Valid Exception START======================");
        LOGGER.error("object : {}", e.getBindingResult().getObjectName());
        List<FieldError> fieldList = e.getFieldErrors();
        for (FieldError field : fieldList) {
            stringJoiner.add("{field: " + field.getField() + ", message: " + field.getDefaultMessage() + "}");
        }
        stringBuilder.append(stringJoiner);
        LOGGER.error(stringBuilder.toString());
        LOGGER.error("======================@Valid Exception End========================");
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode, Exception e) {
        /* KT Validation fix, all HTTP Status code is forwarded to 200 and internal code is passed in detail */
        LOGGER.error(errorCode.getResultMsg(), e);
        return ResponseEntity.ok()
                .header("Content-type", String.valueOf(MediaType.APPLICATION_JSON))
                .body(ErrorResponse.builder()
                        .status(errorCode.getResultCode())
                        .message(errorCode.getResultMsg()).build());
    }
}
