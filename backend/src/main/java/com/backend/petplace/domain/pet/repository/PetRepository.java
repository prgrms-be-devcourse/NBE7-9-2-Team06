package com.backend.petplace.domain.pet.repository;

import com.backend.petplace.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

}
