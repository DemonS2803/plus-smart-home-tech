package ru.yandex.practicum.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.inventory.entity.InventoryRecord;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryRecord, Long> {

    Optional<InventoryRecord> findByProductId(Long productId);

    boolean existsByProductId(Long productId);
}
