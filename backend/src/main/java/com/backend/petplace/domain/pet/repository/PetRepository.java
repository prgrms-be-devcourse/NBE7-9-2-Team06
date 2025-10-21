package com.backend.petplace.domain.pet.repository;

import com.backend.petplace.domain.pet.entity.Pet;
import com.backend.petplace.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {

  @Query("select p from Pet p where p.user = :user and p.activated = true")
  List<Pet> findByUserWithActivatedPet(@Param("user") User user);
}
