package ascelion.micro.account;

import ascelion.micro.shared.model.EntityRepo;

import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends EntityRepo<Account> {
}
