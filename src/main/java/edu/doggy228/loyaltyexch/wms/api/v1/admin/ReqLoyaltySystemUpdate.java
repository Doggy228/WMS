package edu.doggy228.loyaltyexch.wms.api.v1.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqLoyaltySystemUpdate {
    @Schema(description = "Назва системи лояльності")
    @JsonProperty(value = "name")
    private String name;
}
