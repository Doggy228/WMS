package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqOperPayExec {
    @Schema(description = "Ідентифікатор операції.", required = true)
    @JsonProperty(value = "operId")
    private String operId;
    @Schema(description = "Код акцептування операції клієнтом.")
    @JsonProperty(value = "acceptCode")
    private String acceptCode;
}
