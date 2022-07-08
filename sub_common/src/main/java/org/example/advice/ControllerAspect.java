package org.example.advice;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.service.common.ValidatorService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


@Aspect
@Component
@EnableAspectJAutoProxy

@RequiredArgsConstructor
@Slf4j
public class ControllerAspect {
    private final ValidatorService validatorService;

    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private HttpServletRequest request;

    /**
     * 컨트롤러 로그 AOP
     * pointcut: controller 하위 패키지의 모든 method
    * */
    @Around(
            value = "execution(* org.example.controller..*.*(..)) " +
                    "&& !@annotation(org.example.advice.annotation.NoAspect)"
    )
    private Object doControllerAspect(ProceedingJoinPoint pjp) throws Throwable {
        //request 파라미터 값
        Object[] args = pjp.getArgs();
        Parameter[] parameters = ((MethodSignature) pjp.getSignature()).getMethod().getParameters();
        String bodySb = null;
        for(int i=0; i<parameters.length; i++){
            if(parameters[i].isAnnotationPresent(RequestBody.class)) {
                BindingResult bindingResult = validatorService.validate(args[i]);
                if(bindingResult.hasErrors()){
                    ((MethodSignature) pjp.getSignature()).getMethod();
                    throw new MethodArgumentNotValidException(
                            new MethodParameter(((MethodSignature) pjp.getSignature()).getMethod(), i)
                            ,bindingResult
                    );
                }
                bodySb = objectMapper.writeValueAsString(args[i]);
            }
        }

        //Request 로그
        this.requestLog(request, bodySb);

        //Method 수행
        Object returnValue = pjp.proceed();

        //Response 로그{
        this.responseLog(request, returnValue);

        return returnValue;
    }


    /**
     * 예외 로그 AOP
     * pointcut: ExceptionHandler 로 핸들링된 모든 method
    * */
    @Around(
            value = "@annotation(org.springframework.web.bind.annotation.ExceptionHandler) " +
                    "&& !@annotation(org.example.advice.annotation.NoAspect)"
    )
    private Object doExceptionAspect(ProceedingJoinPoint pjp) throws Throwable {
        Object returnValue = null;

        Throwable throwable = (Throwable) pjp.getArgs()[0];
        Throwable cuase = throwable.getCause() == null ? throwable : throwable.getCause();
        Class cuaseClass = cuase.getClass();

        Class exceptionAdviceClass = pjp.getTarget().getClass();
        for (Method method:exceptionAdviceClass.getDeclaredMethods()) {
            for (Class type : method.getParameterTypes()) {
                if (cuaseClass.isAssignableFrom(type)) {
                    try {
                        returnValue = method.invoke(pjp.getTarget(), type.cast(cuase));
                    } catch (Exception e){
                        returnValue = null;
                    }
                    throwable = new Throwable(cuase);
                }
            }
        }

        //ExceptionAdvice 수행
        if(returnValue == null) {
            returnValue = pjp.proceed();
        }

        //exception 로그
        this.exceptionLog(throwable);

        //Response 로그
        this.responseLog(this.request, returnValue);

        return returnValue;
    }




    //======================================================================================
    //=================== util method - START
    //======================================================================================
    /**
     * Request 로그
     * @param request : 서블렛 request 요청값
     * @param body : request body
     * */
    public void requestLog(HttpServletRequest request, String body) throws JsonProcessingException {
        if(request==null) {
            return;
        }

        StringBuilder requestDataString = new StringBuilder();
        if(body!=null && !body.isEmpty()){
            requestDataString.append(" - RequestBody: ");
            requestDataString.append(body);
        }
        if(request.getParameterMap()!=null){
            requestDataString.append(" - RequestParams: ");
            requestDataString.append(objectMapper.writeValueAsString(request.getParameterMap()));
        }

        if(log.isInfoEnabled()){
            log.info("Request: [{}] {} {}", request.getMethod(), request.getRequestURI(), requestDataString);
        }
    }

    /**
     * Response 로그
     * @param request : 서블렛 request 요청값
     * @param returnValue : response value
     * */
    public void responseLog(HttpServletRequest request, Object returnValue) throws Throwable{
        if(request==null) {
            return;
        }

        String responseBody;
        if(returnValue instanceof ResponseEntity){
            responseBody = objectMapper.writeValueAsString(((ResponseEntity) returnValue).getBody());
        }else{
            responseBody = objectMapper.writeValueAsString(returnValue);
        }
        if(log.isInfoEnabled()) {
            log.info("Response: [{}] {} - Result: {}", request.getMethod(), request.getRequestURI(), responseBody);
        }
    }

    /**
     * Exception 로그
     * @param throwable : Exception
     * */
    public void exceptionLog(Throwable throwable){
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        if(stackTraceElements==null || stackTraceElements.length < 1){
            stackTraceElements = throwable.getCause().getStackTrace();
        }
        String methodName = stackTraceElements[0].getMethodName();
        String fileName = stackTraceElements[0].getFileName();
        int lineNumber = stackTraceElements[0].getLineNumber();

        //Exception 발생 위치와 message만 로깅
        log.error("({}:{}.{}) Message: {}", fileName, lineNumber, methodName, throwable.getMessage());
        
        // 상세로그 보기 위하여 추가
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        log.debug(sw.toString());
    }

    //======================================================================================
    //=================== util method - END
    //======================================================================================
}
