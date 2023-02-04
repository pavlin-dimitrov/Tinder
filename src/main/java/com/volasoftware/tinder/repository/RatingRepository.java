package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

}
