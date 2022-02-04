package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseError {
    @Schema(description = "Текстове повідомлення помилки.")
    @JsonProperty(value = "msg")
    private String msg;
    @Schema(description = "Детальна інформація про помилку.")
    @JsonProperty(value = "detail")
    private String detail;
}
