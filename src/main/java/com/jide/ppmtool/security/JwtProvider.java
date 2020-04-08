package com.jide.ppmtool.security;

import com.jide.ppmtool.model.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jide.ppmtool.security.SecurityConstants.EXPIRATION_TIME;
import static com.jide.ppmtool.security.SecurityConstants.SECRET;

@Component
public class JwtProvider {
    //generate Token
    public String generateToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Date currentDate = new Date(System.currentTimeMillis());

        Date expirationDate = new Date(currentDate.getTime() + EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", (Long.toString(user.getId())));
        claims.put("username", (user.getUsername()));
        claims.put("fullName", user.getFullName());

        return Jwts.builder().setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

    }
    //validate Token

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid Jwt Signature");
        }catch (MalformedJwtException ex){
            System.out.println("Invalid Jwt Token");

        }catch (ExpiredJwtException ex){
            System.out.println("Expired Jwt token");
        }catch (UnsupportedJwtException ex){
            System.out.println("Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            System.out.println("Jwt set claims string is empty");
        }
        return  false;
    }
    //get userId from the token
    public Long getUserIdFromJwt(String token){
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();

        String id = (String) claims.get("id");
        return  Long.parseLong(id);
    }
}
