package com.example.ex2_gestioncompte.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;


@RestController
@RequestMapping("api/users")
public class Oauth2_Controller {

    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;

    private UserDetailsService userDetailsService;

    public Oauth2_Controller(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    Map<String,String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Instant instant = Instant.now();
        String authorities = authenticate.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtClaimsSet jwtClaimsSet_AccessToken = JwtClaimsSet.builder()
                .issuer("MS_sec")
                .subject(authenticate.getName())
                .issuedAt(instant)
                .expiresAt(instant.plus(15, ChronoUnit.MINUTES))
                .claim("name", authenticate.getName())
                // Ajout des autorités
                .claim("authorities", authorities)
                .build();

        String AccessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_AccessToken)).getTokenValue();

        // Création du Refresh Token
        JwtClaimsSet jwtClaimsSet_RefreshToken = JwtClaimsSet.builder()
                .issuer("MS_sec")
                .subject(authenticate.getName())
                .issuedAt(instant)
                .expiresAt(instant.plus(15, ChronoUnit.MINUTES))
                .claim("name", authenticate.getName())
                .build();

        String RefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_RefreshToken)).getTokenValue();

        Map<String, String> ID_Token = new HashMap<>();
        ID_Token.put("Access_Token", AccessToken);
        ID_Token.put("Refresh_Token", RefreshToken);
        return ID_Token;
    }


    @PostMapping("/RefreshToken")
    public Map<String,String>fr_t(String RefreshToken){
        if(RefreshToken==null){
            return Map.of("Message error","Refresh_Token est necessaire");
        }
        // verifier la signature
        Jwt decoded = jwtDecoder.decode(RefreshToken);
        String username = decoded.getSubject();
        UserDetails userDetails=userDetailsService.loadUserByUsername(username);


        //Renouveller l'access toekn

        Instant instant = Instant.now();
        //recuperer Scopes dans chaine scopes et separe avec des espaces
        String scopes = userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet_AccessToken = JwtClaimsSet.builder()
                .issuer("MS_sec")
                .subject(userDetails.getUsername())
                .issuedAt(instant)
                .expiresAt(instant.plus(2, ChronoUnit.MINUTES))
                .claim("name", userDetails.getUsername())
                //Roles d'utilisateur
                .claim("SCOPE", scopes)
                .build();

        //signe token
        //Recupere Access Token
        String AccessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_AccessToken)).getTokenValue();

        Map<String, String> ID_Token = new HashMap<>();
        ID_Token.put("Access_Token", AccessToken);
        ID_Token.put("Refresh_Token", RefreshToken);
        return ID_Token;
    }
    }