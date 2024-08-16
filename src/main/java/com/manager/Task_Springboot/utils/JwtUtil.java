package com.manager.Task_Springboot.utils;

import com.manager.Task_Springboot.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.manager.Task_Springboot.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.builder;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserRepository userRepository;
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
//        to call token method from api
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails details){
        return Jwts.builder().setClaims(extraClaims).setSubject(details.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256).compact();
    }
    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode("413F4428472B4B6250655368566D5970337336763979244226452948404D6351");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final  String userName=extractUserName(token);
        return (userName.equals(userDetails.getUsername()))&& !isTokenExpired(token);
//    to check the validity of token
    }

    public  String extractUserName(String token){
        return  extractClaims(token, Claims::getSubject);
//    to extract the username from the token
    }

    private  boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
//        to check wheather the token is expired or not
    }
    private Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
//    to extarct the expiration of the token
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimsResolvers){
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
//        to resolve all the specified claims from function extractAllClaims
    }

    private Claims extractAllClaims(String token){
        return  Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
//      to check  validity n expiration of token
    }

    public User getLoggedInUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.isAuthenticated()){
            User user=(User)  authentication.getPrincipal();
            Optional<User> optionalUser=userRepository.findById(user.getId());
            return optionalUser.orElse(null);
        }
        return null;
    }


}
