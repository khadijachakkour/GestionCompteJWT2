package com.example.ex2_gestioncompte.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nom" , length = 100)
    private String nom;
    @Column(name = "tel", length = 100)
    private String tel;

    @Column(name = "montant", length = 100)
    private float montant;


}
