package ascelion.micro.users;

import ascelion.micro.shared.model.EntityRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends EntityRepository<User> {
}
