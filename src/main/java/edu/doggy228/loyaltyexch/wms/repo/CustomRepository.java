package edu.doggy228.loyaltyexch.wms.repo;

import edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser;
import edu.doggy228.loyaltyexch.wms.modeldb.Oper;

import java.util.List;

public interface CustomRepository {
    public List<LoyaltyUser> loyaltyUserFindByWalletUser(String walletUserId);
    public List<Oper> operFindByWalletUserLast100(String walletUserId);
    public List<Oper> operFindByLoyaltyUserLast100(String loyaltyUserId);
    public List<Oper> operFindByLoyaltySystemLast100(String loyaltySystemId);
}
