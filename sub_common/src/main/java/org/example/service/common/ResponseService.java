package org.example.service.common;

import org.example.model.dto.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.example.model.response.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ResponseService {

    /**
     * 단일 결과 처리
     * @param data
     * @param <T>
     * @return
     */
    public <T> SingleResult<T> getSingleResult(T data) {
        SingleResult<T> result = new SingleResult<>();
        result.setData(data);
        setSuccessResult(result);
        return result;
    }

    /**
     * 단일 결과 처리 - ID 추가
     * @param id 트랜젝션이 발생한 ID
     * @return
     */
    public SingleResult getSingleResultById(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        return getSingleResult(map);
    }

    /**
     * 단일 결과 처리 - ID 목록 추가
     * @param ids 트랜젝션이 발생한 ID 목록
     * @return
     */
    public SingleResult getSingleResultByIds(List<String> ids) {
        Map<String, Object> map = new HashMap<>();
        map.put("ids", ids);

        return getSingleResult(map);
    }

    /**
     * 다중 결과 처리 (list)
     * @param list
     * @param <T>
     * @return list
     */
    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setList(list);
        setSuccessResult(result);
        return result;
    }

    /**
     * api 요청 성공 데이터 세팅
     * @param result
     */
    private void setSuccessResult(CommonResult result) {
        result.setResult(Boolean.TRUE);
        result.setStatus(200);
        result.setCode("0");
        result.setMsg("success");
    }

    /**
     * 성공 결과 처리
     * @return
     */
    public CommonResult getSuccessResult() {
        CommonResult result = new CommonResult();
        setSuccessResult(result);
        return result;
    }

    public ErrorResult getFailResult(ErrorCode errorCode, List<ErrorResult.ErrorField> error) {
        return ErrorResult.of(errorCode,error);
    }
}
