package org.example.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.example.model.response.CommonResult;
import org.example.service.v1.EtcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.common.ResponseService;
import org.springframework.web.bind.annotation.*;

@Tags(value = @Tag(name = "Etc"))
@Slf4j
@RequestMapping(value = "/etc/v1")
@RestController
@RequiredArgsConstructor
public class EtcController {

    private final ResponseService responseService;
    private final EtcService service;

    @GetMapping("/timezone")
    @Operation(summary = "TimeZone 목록 조회", description = "TimeZone 목록 조회")
    public CommonResult gets() {
        return responseService.getListResult(service.getTimeZoneList());
    }

    @GetMapping("/env/{key}")
    @Operation(summary = "환경변수 조회", description = "환경변수 조회")
    public CommonResult getEnv(
            @PathVariable String key
    ) {
        return responseService.getSingleResult(service.getEnvironment(key));
    }
}
