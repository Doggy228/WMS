package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Oper {
    @Schema(description = "Ідентифікатор операції в WMS")
    @JsonProperty(value = "id")
    private String id;
    @Schema(description = "Ідентифікатор гаманця в WMS")
    @JsonProperty(value = "walletUserId")
    private String walletUserId;
    @Schema(description = "Тип операції. pay - платіж в магазині")
    @JsonProperty(value = "operType")
    private OperType operType;
    @Schema(description = "Стан операції. create - створена, exec - виконується, ok - успішна, err - помилка, cancel - відміна")
    @JsonProperty(value = "state")
    private OperStateType state;
    @Schema(description = "Текст ппомилки")
    @JsonProperty(value = "stateMsg")
    private String stateMsg;
    @Schema(description = "Дата та час створення", pattern = "YYYY-MM-DDThh:mm:ss", example = "2021-12-08T13:43:01")
    @JsonProperty(value = "operCreateDt")
    private String operCreateDt;
    @Schema(description = "Дата та час виконання", pattern = "YYYY-MM-DDThh:mm:ss", example = "2021-12-08T13:43:01")
    @JsonProperty(value = "operExecDt")
    private String operExecDt;
    @Schema(description = "Ідентифікатор системи лояльності.")
    @JsonProperty(value = "loyaltySystemId")
    private String loyaltySystemId;
    @Schema(description = "Назва системи лояльності")
    @JsonProperty(value = "loyaltySystemName")
    private String loyaltySystemName;
    @Schema(description = "Ідентифікатор картки лояльності")
    @JsonProperty(value = "loyaltyUserId")
    private String loyaltyUserId;
    @Schema(description = "Сума опеерації")
    @JsonProperty(value = "operAmount")
    private String operAmount;
    @Schema(description = "Тип валюти операції. fc - фіат, sc - системна, vc - бали лояльності")
    @JsonProperty(value = "operCurrencyType")
    private CurrencyType operCurrencyType;
    @Schema(description = "Ідентифікатор валюти операції")
    @JsonProperty(value = "operCurrencyAlias")
    private String operCurrencyAlias;
    @Schema(description = "Призначення операції")
    @JsonProperty(value = "operPurpose")
    private String operPurpose;
    @Schema(description = "Додаткові атрибути операції")
    @JsonProperty(value = "details")
    private AttrValue[] details;
    @Schema(description = "Список тразакцій")
    @JsonProperty(value = "listTrans")
    private Trans[] listTrans;
}
