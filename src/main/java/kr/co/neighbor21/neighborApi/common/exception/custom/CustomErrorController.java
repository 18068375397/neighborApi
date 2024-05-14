package kr.co.neighbor21.neighborApi.common.exception.custom;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.response.structure.ErrorResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * spring errorController 구현체
 *
 * @author GEONLEE
 * @since 2024-03-15<<br />
 * 2024-03-30 GEONLEE - ServiceException throw 하도록 변경<br />
 */
@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping(value = "/error")
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();

        System.out.println(status);  //오류 상태
        System.out.println(request.getRequestURI());
        throw new ServiceException(CommonErrorCode.SERVICE_ERROR, null);
    }
}
