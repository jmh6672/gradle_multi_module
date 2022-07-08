package org.example.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import org.example.model.response.SingleResult;
import org.example.service.common.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonController {
    @Value("${app.name}")
    String appName;

    @Value("${app.version}")
    String appVersion;

    private final ResponseService responseService;

    @GetMapping("/version")
    @Operation(summary = "버전 조회", description = "상세 버전")
    public SingleResult<HashMap<String, Object>> getVersion() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", appName);
        map.put("version", appVersion);
        return responseService.getSingleResult(map);
    }
}
