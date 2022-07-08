package org.example.advice;

import org.example.advice.exception.*;
import org.example.model.dto.ErrorCode;
import org.example.model.response.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 로그인 시도시 실패 오류
     */
    @ExceptionHandler(FailedLoginException.class)
    public ResponseEntity<ErrorResult> handleHttpException(FailedLoginException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.FAIL_LOGIN);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * jwt Token 의 유효성 체크가 실패 했을때 발생
     */
    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ErrorResult> handleHttpException(ExpiredTokenException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.EXPIRED_TOKEN);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * jwt Token 못 찾을시 발생
     */
    @ExceptionHandler(NotFoundTokenException.class)
    public ResponseEntity<ErrorResult> handleHttpException(NotFoundTokenException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.NOT_FOUND_TOKEN);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResult> handleHttpException(UnauthorizedException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.UNAUTHORIZED);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 중복된 데이터
     */
    @ExceptionHandler(DuplicatedDataException.class)
    public ResponseEntity<ErrorResult> handleHttpException(DuplicatedDataException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.DUPLICATED_DATA);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * request contentType이 json/application일때, json request를 모델객체에 파싱할때 발생
     * 주로 @RequestBody 어노테이션에서 발생
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final HttpMessageNotReadableException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final MethodArgumentNotValidException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE, e.getBindingResult());

        StringBuilder sb = new StringBuilder("Validation failed for argument in ")
            .append(e.getParameter().getExecutable().toGenericString());

        response.setMsg(sb.toString());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ModelAttribut 으로 binding error 발생시 BindException 발생
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final BindException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE, e.getBindingResult());
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final MethodArgumentTypeMismatchException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final HttpRequestMethodNotSupportedException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.METHOD_NOT_ALLOWED);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생
     * Security 에서 던지는 예외
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final AccessDeniedException e) {
        final ErrorCode code = ErrorCode.ACCESS_DENIED;
        final ErrorResult response = ErrorResult.of(code);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(code.getStatus()));
    }

    /**
     * Not Found (404) 예외 발생
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final NotFoundException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.NOT_FOUND_REQUEST);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * interServerErrorException (500) 예외 발생
     */
    @ExceptionHandler(InterServerErrorException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final InterServerErrorException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INTERNAL_SERVER_ERROR);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorCommonException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final ErrorCommonException e) {
        final ErrorCode code = e.getErrorCode();
        ErrorResult response = ErrorResult.of(code);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(code.getStatus()));
    }

    /**
     *
     */
    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final NoSuchFieldException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.NO_SUCH_FIELD);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * DB 커넥션 실패시 처리
     */
    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final DataAccessResourceFailureException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.DB_CONNECTION_ERROR);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Data dao 호출시 유효성 오류가 발생하면 처리
     * */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final DataIntegrityViolationException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.DB_VALIDATOR_ERROR);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final ClassCastException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INTERNAL_SERVER_ERROR);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Null Point Exception
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final NullPointerException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.NULL_POINTER_ERROR);
        if(response.getMsg().isEmpty()) {
            if(e.getMessage()==null){
                StringBuffer sb = new StringBuffer();
                sb.append(e.getStackTrace()[0]);
                sb.append("\n");
                sb.append(e.getStackTrace()[1]);
                sb.append("\n");
                sb.append(e.getStackTrace()[2]);
                response.setMsg(sb.toString());
            }else {
                response.setMsg(e.getMessage());
            }
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * 예기치 못한 모든 예외 처리, Null Point Exception 등등..
     * 직접 핸들링하지 않은 모든 예외처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleHttpException(final Exception e) {
        ErrorResult response = ErrorResult.of(ErrorCode.UNKNOWN_ERROR);
        if(response.getMsg().isEmpty()) {
            response.setMsg(e.getMessage() != null ? e.getMessage() : (e.getCause() != null ? e.getCause().getMessage() : null));
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
