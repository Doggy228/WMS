package edu.doggy228.loyaltyexch.wms.repo;

import edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoyaltyUserRepository extends MongoRepository<LoyaltyUser, String> {
}
