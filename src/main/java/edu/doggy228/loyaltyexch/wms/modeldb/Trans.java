package edu.doggy228.loyaltyexch.wms.modeldb;

import edu.doggy228.loyaltyexch.wms.modeljson.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;

@Getter
@Setter
public class Trans {
    private String id;
    private String operId;
    private String walletUserId;
    private TransType transType;
    private OperStateType state;
    private String stateMsg;
    private LocalDate transCreateDt;
    private LocalDate transExecDt;
    private String loyaltySystemId;
    private String loyaltyUserId;
    private String sendAmount;
    private CurrencyType sendCurrencyType;
    private String sendCurrencyAlias;
    private String rcptAmount;
    private CurrencyType rcptCurrencyType;
    private String rcptCurrencyAlias;
    private String transPurpose;
    private AttrValue[] details;
}
