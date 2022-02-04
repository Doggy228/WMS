package edu.doggy228.loyaltyexch.wms.repo;

import edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoyaltySystemRepository extends MongoRepository<LoyaltySystem, String> {
}
