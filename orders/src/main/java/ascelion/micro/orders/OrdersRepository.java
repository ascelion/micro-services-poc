package ascelion.micro.orders;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Order, UUID> {
}
