package edu.doggy228.loyaltyexch.wms.repo;

import edu.doggy228.loyaltyexch.wms.modeldb.Oper;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OperRepository extends MongoRepository<Oper, String> {
}
