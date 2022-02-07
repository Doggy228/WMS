package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqOperPayCreate {
    @Schema(description = "Ідентифікатор системи лояльності.", required = true)
    @JsonProperty(value = "loyaltySystemId")
    private String loyaltySystemId;
    @Schema(description = "Список використовуємих карт лояльності з вказаними сумами обміну.", required = true)
    @JsonProperty(value = "listLoyaltyUserPay")
    private LoyaltyUserPay[] listLoyaltyUserPay;
    @Schema(description = "Cума платіжної транзакціїї в гривнях.", required = true)
    @JsonProperty(value = "operAmount")
    private String operAmount;
    @Schema(description = "Призначення платіжної транзакції.", required = true)
    @JsonProperty(value = "operPurpose")
    private String operPurpose;
}
