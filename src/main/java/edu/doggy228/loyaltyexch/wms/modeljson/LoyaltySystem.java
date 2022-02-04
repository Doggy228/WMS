package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoyaltySystem {
    @Schema(description = "Ідентифікатор системи лояльності")
    @JsonProperty(value = "id")
    private String id;
    @Schema(description = "Назва системи лояльності")
    @JsonProperty(value = "name")
    private String name;
    @Schema(description = "Код віртуальної валюти", example = "SLT")
    @JsonProperty(value = "vcAlias")
    private String vcAlias;
    @Schema(description = "Назва віртуальної валюти")
    @JsonProperty(value = "vcName")
    private String vcName;
    @Schema(description = "Курс відповідності до гривні. Наприклад, якщо балл це копійка, то курс 0.01", example = "0.01")
    @JsonProperty(value = "vcRate")
    private String vcRate;
    @Schema(description = "Кількість цифр дробної частини", example = "2")
    @JsonProperty(value = "vcScale")
    private int vcScale;
}
