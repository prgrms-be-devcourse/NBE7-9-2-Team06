package com.backend.petplace.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.petplace.domain.pet.entity.Pet;

public interface PetRepository  extends JpaRepository<Pet, Long> {

}
