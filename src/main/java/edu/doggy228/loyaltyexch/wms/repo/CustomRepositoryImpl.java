package edu.doggy228.loyaltyexch.wms.repo;

import edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser;
import edu.doggy228.loyaltyexch.wms.modeldb.Oper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomRepositoryImpl implements CustomRepository {
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<LoyaltyUser> loyaltyUserFindByWalletUser(String walletUserId) {
        final Query query = new Query()
                .addCriteria(Criteria.where("walletUserId").is(walletUserId));
        return mongoTemplate.find(query, LoyaltyUser.class);
    }

    @Override
    public LoyaltyUser loyaltyUserFindByWalletUserAndLoyaltySystem(String walletUserId, String loyaltySystemId){
        final Query query = new Query()
                .addCriteria(new Criteria().andOperator(
                        Criteria.where("walletUserId").is(walletUserId),
                        Criteria.where("loyaltySystemId").is(loyaltySystemId)
                        ));
        List<LoyaltyUser> list = mongoTemplate.find(query, LoyaltyUser.class);
        if(list==null || list.isEmpty()) return null;
        return list.get(0);
    }

    @Override
    public List<Oper> operFindByWalletUserLast100(String walletUserId) {
        final Pageable pageableRequest = PageRequest.of(0, 100);
        final Query query = new Query()
                .addCriteria(Criteria.where("walletUserId").is(walletUserId))
                .with(Sort.by(Sort.Direction.DESC, "operCreateDt"))
                .with(pageableRequest);
        return mongoTemplate.find(query, Oper.class);
    }

    @Override
    public List<Oper> operFindByLoyaltyUserLast100(String loyaltyUserId) {
        final Pageable pageableRequest = PageRequest.of(0, 100);
        final Query query = new Query()
                .addCriteria(Criteria.where("refLoyaltyUser").regex(";"+loyaltyUserId+";"))
                .with(Sort.by(Sort.Direction.DESC, "operCreateDt"))
                .with(pageableRequest);
        return mongoTemplate.find(query, Oper.class);
    }

    @Override
    public List<Oper> operFindByLoyaltySystemLast100(String loyaltySystemId) {
        final Pageable pageableRequest = PageRequest.of(0, 100);
        final Query query = new Query()
                .addCriteria(Criteria.where("refLoyaltySystem").regex(";"+loyaltySystemId+";"))
                .with(Sort.by(Sort.Direction.DESC, "operCreateDt"))
                .with(pageableRequest);
        return mongoTemplate.find(query, Oper.class);
    }
}
