package edu.doggy228.loyaltyexch.wms.modeldb;

import edu.doggy228.loyaltyexch.wms.modeljson.AttrValue;
import edu.doggy228.loyaltyexch.wms.modeljson.CurrencyType;
import edu.doggy228.loyaltyexch.wms.modeljson.OperStateType;
import edu.doggy228.loyaltyexch.wms.modeljson.OperType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;

@Getter
@Setter
public class Oper {
    @Id
    private String id;
    @Indexed(unique = false)
    private String walletUserId;
    private OperType operType;
    private OperStateType state;
    private String stateMsg;
    private LocalDate operCreateDt;
    private LocalDate operExecDt;
    private String loyaltySystemId;
    private String loyaltyUserId;
    private String operAmount;
    private CurrencyType operCurrencyType;
    private String operCurrencyAlias;
    private String operPurpose;
    private AttrValue[] details;
    private Trans[] listTrans;
    private String refLoyaltySystems;
    private String refLoyaltyUsers;
}
