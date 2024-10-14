package com.example.ex2_gestioncompte;

import com.example.ex2_gestioncompte.configuration.RsaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties(RsaConfig.class)
public class Ex2GestionCompteApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ex2GestionCompteApplication.class, args);
    }
    @Bean
        //Methode pour crypter les mots de passes
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

