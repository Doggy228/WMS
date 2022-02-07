package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trans {
    @Schema(description = "Ідентифікатор транзакції в WMS")
    @JsonProperty(value = "id")
    private String id;
    @Schema(description = "Ідентифікатор операції в WMS")
    @JsonProperty(value = "operId")
    private String operId;
    @Schema(description = "Ідентифікатор гаманця в WMS")
    @JsonProperty(value = "walletUserId")
    private String walletUserId;
    @Schema(description = "Тип транзакції. lsExch - обмін балів, lsPay - платіж в магазині")
    @JsonProperty(value = "transType")
    private TransType transType;
    @Schema(description = "Стан транзакції. create - створена, exec - виконується, ok - успішна, err - помилка, cancel - відміна")
    @JsonProperty(value = "state")
    private TransStateType state;
    @Schema(description = "Текст ппомилки")
    @JsonProperty(value = "stateMsg")
    private String stateMsg;
    @Schema(description = "Дата та час створення", pattern = "YYYY-MM-DDThh:mm:ss", example = "2021-12-08T13:43:01")
    @JsonProperty(value = "transCreateDt")
    private String transCreateDt;
    @Schema(description = "Дата та час виконання", pattern = "YYYY-MM-DDThh:mm:ss", example = "2021-12-08T13:43:01")
    @JsonProperty(value = "transExecDt")
    private String transExecDt;
    @Schema(description = "Ідентифікатор системи лояльності.")
    @JsonProperty(value = "loyaltySystemId")
    private String loyaltySystemId;
    @Schema(description = "Назва системи лояльності")
    @JsonProperty(value = "loyaltySystemName")
    private String loyaltySystemName;
    @Schema(description = "Ідентифікатор картки лояльності")
    @JsonProperty(value = "loyaltyUserId")
    private String loyaltyUserId;
    @Schema(description = "Сума, яка списується")
    @JsonProperty(value = "sendAmount")
    private String sendAmount;
    @Schema(description = "Тип валюти списання. fc - фіат, sc - системна, vc - бали лояльності")
    @JsonProperty(value = "sendCurrencyType")
    private CurrencyType sendCurrencyType;
    @Schema(description = "Ідентифікатор валюти списання")
    @JsonProperty(value = "sendCurrencyAlias")
    private String sendCurrencyAlias;
    @Schema(description = "Сума, яка зараховується")
    @JsonProperty(value = "rcptAmount")
    private String rcptAmount;
    @Schema(description = "Тип валюти зарахування. fc - фіат, sc - системна, vc - бали лояльності")
    @JsonProperty(value = "rcptCurrencyType")
    private CurrencyType rcptCurrencyType;
    @Schema(description = "Ідентифікатор валюти зарахування")
    @JsonProperty(value = "rcptCurrencyAlias")
    private String rcptCurrencyAlias;
    @Schema(description = "Призначення тразакції")
    @JsonProperty(value = "transPurpose")
    private String transPurpose;
    @Schema(description = "Додаткові атрибути тразакції")
    @JsonProperty(value = "details")
    private AttrValue[] details;
}
