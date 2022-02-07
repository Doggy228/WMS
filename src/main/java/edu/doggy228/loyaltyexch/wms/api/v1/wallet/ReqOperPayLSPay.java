package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqOperPayLSPay {
    @Schema(description = "Ідентифікатор операції.", required = true)
    @JsonProperty(value = "operId")
    private String operId;
    @Schema(description = "Ідентифікатор транзакції в торговій точкі.")
    @JsonProperty(value = "lsTransId")
    private String lsTransId;
    @Schema(description = "Дата та час транзакції в торговій точкі.")
    @JsonProperty(value = "lsTransDt")
    private String lsTransDt;
    @Schema(description = "Сума, транзакції в гривнях.", required = true)
    @JsonProperty(value = "lsTransAmount")
    private String lsTransAmount;
    @Schema(description = "Сума, оплачена в гривнях.")
    @JsonProperty(value = "lsTransAmountPay")
    private String lsTransAmountPay;
    @Schema(description = "Використано бонусних балів.")
    @JsonProperty(value = "lsBonusAmountIn")
    private String lsBonusAmountIn;
    @Schema(description = "Нараховано бонусних балів.")
    @JsonProperty(value = "lsBonusAmountOut")
    private String lsBonusAmountOut;
    @Schema(description = "Призначення платежу в торговій точкі.")
    @JsonProperty(value = "lsTransPurpose")
    private String lsTransPurpose;
}
