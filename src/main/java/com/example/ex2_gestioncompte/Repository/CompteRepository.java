package com.example.ex2_gestioncompte.Repository;


import com.example.ex2_gestioncompte.Entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte,Long> {
}
