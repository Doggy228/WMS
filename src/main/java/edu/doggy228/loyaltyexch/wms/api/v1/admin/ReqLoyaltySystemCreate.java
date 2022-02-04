package edu.doggy228.loyaltyexch.wms.api.v1.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Конфігурація нової системи лояльності.")
@Getter
public class ReqLoyaltySystemCreate {
    @Schema(description = "Ідентифікатор системи лояльності", required = true)
    @JsonProperty(value = "id")
    private String id;
    @Schema(description = "Назва системи лояльності", required = true)
    @JsonProperty(value = "name")
    private String name;
}

