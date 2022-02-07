package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.doggy228.loyaltyexch.wms.modeljson.Oper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RspOperPayExec {
    @Schema(description = "Операція")
    @JsonProperty(value = "oper")
    private Oper oper;
    @Schema(description = "Ідентифікатор системи лояльності для платіжної операції")
    @JsonProperty(value = "loyaltySystemId")
    private String loyaltySystemId;
    @Schema(description = "Назва системи лояльності для платіжної операції")
    @JsonProperty(value = "loyaltySystemName")
    private String loyaltySystemName;
    @Schema(description = "Код віртуальної валюти для платіжної операції")
    @JsonProperty(value = "vcAlias")
    private String vcAlias;
    @Schema(description = "Назва віртуальної валюти для платіжної операції")
    @JsonProperty(value = "vcName")
    private String vcName;
    @Schema(description = "Кількість цифр дробної частини для платіжної операції")
    @JsonProperty(value = "vcScale")
    private int vcScale;
    @Schema(description = "Ідентифікатор картки користувача в WMS")
    @JsonProperty(value = "loyaltyUserId")
    private String loyaltyUserId;
    @Schema(description = "Ідентифікатор картки користувача в зовнішній системі лояльності")
    @JsonProperty(value = "loyaltyUserExtrnId")
    private String loyaltyUserExtrnId;
    @Schema(description = "Кількість бонусних балів для платіжної операції")
    @JsonProperty(value = "bonusAmountIn")
    private String bonusAmountIn;

}
