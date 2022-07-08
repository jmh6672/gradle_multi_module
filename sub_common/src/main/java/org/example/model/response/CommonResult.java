package org.example.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class CommonResult {

    @Schema(description = "응답 성공 결과 : true/false")
    protected boolean result;

    @Schema(description = "응답 코드 번호")
    protected String code;
    
    @Schema(description = "http 응답 코드")
    protected int status;

    @Schema(description = "응답 타이틀")
    protected String title;

    @Schema(description = "응답 메시지")
    protected String msg;
}
