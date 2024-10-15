package com.example.ex2_gestioncompte.configuration;

import com.example.ex2_gestioncompte.Entities.UserEntity;
import com.example.ex2_gestioncompte.Service.UserService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final RsaConfig rsaConfig;
    private final UserService userService;

    public SecurityConfig(PasswordEncoder passwordEncoder, RsaConfig rsaConfig, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.rsaConfig = rsaConfig;
        this.userService = userService;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsManager userDetailsManager) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsManager);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public UserDetailsManager userDetailsManager(UserService userService) {
        return new UserDetailsManager() {
            @Override
            public void createUser(UserDetails user) {
                UserEntity userEntity = new UserEntity();
                userEntity.setUsername(user.getUsername());
                userEntity.setPassword(user.getPassword());
                userEntity.setAuthorities(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
                userService.save(userEntity);
            }

            @Override
            public void updateUser(UserDetails user) {

            }

            @Override
            public void deleteUser(String username) {

            }

            @Override
            public void changePassword(String oldPassword, String newPassword) {

            }

            @Override
            public boolean userExists(String username) {
                return false;
            }

            @Override
            public UserDetails loadUserByUsername(String username) {
                UserEntity user = userService.findByUsername(username);
                if (user == null) {
                    throw new UsernameNotFoundException("Utilisateur non trouvé");
                }
                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
            }
        };
    }


            @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/RefreshToken").permitAll()
                        .requestMatchers("/api/comptes/ajouter", "/api/comptes/modifier/**", "/api/comptes/supprimer/**", "/api/comptes/all").hasRole("ADMIN")
                        .requestMatchers("/api/comptes/{id}").authenticated()
                        .requestMatchers("/api/comptes/crediter/**", "/api/comptes/debiter/**").authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    //un convertisseur pour gérer les rôles et les autorisations à partir d'un token JWT(pour gérer l'accès aux ressources en fonction des rôles d'utilisateur)
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> authorities = jwt.getClaimAsStringList("authorities");
            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
    }


    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaConfig.publicKey()).privateKey(rsaConfig.rsaPrivateKey()).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaConfig.publicKey()).build();
    }
}
