package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RspOperPayCheck {
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
    @Schema(description = "Курс конвертації з системної валюту.")
    @JsonProperty(value = "rateSell")
    private String rateSell;
    @Schema(description = "Максимально допустима кількість балів для використання в платіжній операції.")
    @JsonProperty(value = "bonusAmountInMax", required = true)
    private String bonusAmountInMax;
    @Schema(description = "Список карт лояльності с можливістю їх використання.")
    @JsonProperty(value = "listLoyaltyUserPayInfo")
    private LoyaltyUserPayInfo[] listLoyaltyUserPayInfo;
}
