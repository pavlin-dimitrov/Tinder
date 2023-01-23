package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.service.contract.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

  private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
  private static final long  accessTokenTwentyFourMinutes = 1000 * 60 * 2;
  //1000 * 60 * 24
  private static final long refreshTokenOneWeek = 1000 * 60 * 60 * 24 * 7;

  @Override
  public String extractUsername(String token) {
    log.info("Extract username, e.g. email");
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    log.info("Extracting Claim");
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  @Override
  public String generateAccessToken(UserDetails userDetails) {
    log.info("Generated access token");
    return generateAccessToken(new HashMap<>(), userDetails);
  }

  @Override
  public  String generateRefreshToken(UserDetails userDetails){
    log.info("Generated refresh token");
    return generateRefreshToken(new HashMap<>(), userDetails);
  }

  @Override
  public String generateAccessToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  ) {
    log.info("Generated access token");
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenTwentyFourMinutes))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String generateRefreshToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails
  ) {
    log.info("Generated refresh token");
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + refreshTokenOneWeek))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    log.info("Checking if the token is valid");
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    log.info("Checking if the token is expired");
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    log.info("Extracting the expiration of the token");
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    log.info("Extracting all Claims");
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    log.info("Getting signature key");
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
