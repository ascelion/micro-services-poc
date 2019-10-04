package ascelion.micro.payment;

import ascelion.micro.shared.model.EntityRepo;

import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends EntityRepo<Payment> {
}
