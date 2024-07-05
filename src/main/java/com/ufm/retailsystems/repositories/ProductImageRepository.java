package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImages , Long> {

}
