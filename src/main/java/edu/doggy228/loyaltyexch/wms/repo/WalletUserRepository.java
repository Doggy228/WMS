package edu.doggy228.loyaltyexch.wms.repo;

import edu.doggy228.loyaltyexch.wms.modeldb.WalletUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalletUserRepository extends MongoRepository<WalletUser, String> {
}
