package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransStateType {
    @JsonProperty("create")
    CREATE,
    @JsonProperty("exec")
    EXEC,
    @JsonProperty("ok")
    OK,
    @JsonProperty("err")
    ERR,
    @JsonProperty("cancel")
    CANCEL
}
