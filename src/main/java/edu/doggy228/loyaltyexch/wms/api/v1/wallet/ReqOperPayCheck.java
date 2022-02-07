package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqOperPayCheck {
    @Schema(description = "Ідентифікатор системи лояльності.", required = true)
    @JsonProperty(value = "loyaltySystemId")
    private String loyaltySystemId;
    @Schema(description = "Максимально допустима кількість балів для використання.", required = true)
    @JsonProperty(value = "bonusAmountInMax")
    private String bonusAmountInMax;
}
