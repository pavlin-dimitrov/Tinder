package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
