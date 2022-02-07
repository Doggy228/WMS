package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoyaltyUserPayInfo {
    @Schema(description = "Ідентифікатор картки користувача в WMS")
    @JsonProperty(value = "loyaltyUserId")
    private String loyaltyUserId;
    @Schema(description = "Ідентифікатор картки користувача в системі лояльності")
    @JsonProperty(value = "loyaltyUserExtrnId")
    private String loyaltyUserExtrnId;
    @Schema(description = "Поточний баланс картки в системі лояльності")
    @JsonProperty(value = "balanceAmount")
    private String balanceAmount;
    @Schema(description = "Ідентифікатор системи лояльності")
    @JsonProperty(value = "loyaltySystemId")
    private String loyaltySystemId;
    @Schema(description = "Назва системи лояльності")
    @JsonProperty(value = "loyaltySystemName")
    private String loyaltySystemName;
    @Schema(description = "Код віртуальної валюти")
    @JsonProperty(value = "vcAlias")
    private String vcAlias;
    @Schema(description = "Назва віртуальної валюти")
    @JsonProperty(value = "vcName")
    private String vcName;
    @Schema(description = "Кількість цифр дробної частини")
    @JsonProperty(value = "vcScale")
    private int vcScale;
    @Schema(description = "Ознака локальної транзакції. true - локальна, false - з іншої системи лояльності.")
    @JsonProperty(value = "localTrans")
    private boolean localTrans;
    @Schema(description = "Курс конвертації в системну валюту.")
    @JsonProperty(value = "rateBuy")
    private String rateBuy;
    @Schema(description = "Максимально допустима сума балів для використання клієнтом в даній операції.")
    @JsonProperty(value = "bonusAmountInMax")
    private String bonusAmountInMax;
}
