package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.DeliveryStatus;
import com.ufm.retailsystems.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus,Long> {

    DeliveryStatus findByStatus(Status status);
}
