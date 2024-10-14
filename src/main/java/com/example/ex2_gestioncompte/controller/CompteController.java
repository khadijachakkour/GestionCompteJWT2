package com.example.ex2_gestioncompte.controller;


import com.example.ex2_gestioncompte.Entities.Compte;
import com.example.ex2_gestioncompte.Service.CompteService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comptes")


@OpenAPIDefinition(info = @Info(
        title = "API de Gestion des Comptes Bancaires",
        version = "1.0",
        description = "Cette API permet de gérer les comptes bancaires, de consulter le solde, d'effectuer des crédits et des débits."
),

        servers = @Server(
                url = "http://localhost:8080/"
        ))

public class CompteController {

    @Autowired
    CompteService compteService;


    @PostMapping("/ajouter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ajouter Un compte")
    public ResponseEntity<Compte> createCompte(@RequestBody Compte compte) {
        return ResponseEntity.ok(compteService.CreateCompte(compte));
    }

    @Operation(summary = "Récupérer liste des comptes")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Compte>> getAll() {
        List<Compte> compteList = compteService.GetAllCompte();
        return ResponseEntity.ok(compteList);

    }


    @Operation(summary = "recuperer un compte par son Id")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Compte> GetbyId(@PathVariable Long id) {
        Compte compte = compteService.GetCompteById(id);
        return compte.equals(null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(compte);
    }

    @DeleteMapping("/supprimer/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity Delete(@PathVariable Long id) {
        compteService.DeleteCompte(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modifier/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Compte> Update(@PathVariable Long id, @RequestBody Compte c) {
        Compte compte = compteService.UpdateCompte(id, c);
        return ResponseEntity.ok(compte);
    }


    @PutMapping("/crediter/{id}/{m}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Compte> crediter(@PathVariable Long id, @PathVariable float m) {
        Compte compte = compteService.Crediter(id, m);
        return ResponseEntity.ok(compte);
    }

    @PutMapping("/debiter/{id}/{m}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Compte> debiter(@PathVariable Long id, @PathVariable float m) {
        Compte compte = compteService.Debiter(id, m);
        return ResponseEntity.ok(compte);
    }


}
