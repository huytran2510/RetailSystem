package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper,Long> {
}
