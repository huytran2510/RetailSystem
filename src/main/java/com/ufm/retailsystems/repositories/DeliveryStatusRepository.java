package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus,Long> {
}
