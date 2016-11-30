package com.librest.books;

import java.util.Arrays;
import java.util.List;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.json.JSONObject;



public class TokenFactory {
	RsaJsonWebKey rsaJsonWebKey;
	
	TokenFactory(){
		 // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
	    try {
			 rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
		} catch (JoseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String tokenProduce(int memberID, String memberRole) {
		
		
		String jwt="";
	    rsaJsonWebKey.setKeyId("k1");

	    JwtClaims claims = new JwtClaims();
	    claims.setIssuer("Issuer");  // who creates the token and signs it
	    claims.setAudience("Audience"); // to whom the token is intended to be sent
	    claims.setExpirationTimeMinutesInTheFuture(5); // time when the token will expire (5 minutes from now)
	    claims.setGeneratedJwtId(); // a unique identifier for the token
	    claims.setIssuedAtToNow();  // when the token was issued/created (now)
	    claims.setNotBeforeMinutesInThePast(0); // time before which the token is not yet valid (2 minutes ago)
	    claims.setSubject("subject"); // the subject/principal is whom the token is about
	    claims.setClaim("ID",memberID+""); 
	    claims.setClaim("Role",memberRole); 
	    List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
	    claims.setStringListClaim("groups", groups); // 
	    JsonWebSignature jws = new JsonWebSignature();

	    jws.setPayload(claims.toJson());
	    // The JWT is signed 
	    jws.setKey(rsaJsonWebKey.getPrivateKey());
	    // Set the Key ID (kid).
	    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
	    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
	    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

	    
		try {
			jwt = jws.getCompactSerialization();
		} catch (JoseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		return jwt;
	    

	   
	}
	
	public JSONObject tokenConsume(String jwtSent){

		JSONObject jsonObject= new JSONObject();
		int memberID=-1;
		String memberRole="Visitor";
	    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	            .setRequireExpirationTime() // the JWT must have an expiration time
	            .setMaxFutureValidityInMinutes(300)
	            .setAllowedClockSkewInSeconds(600) 
	            .setRequireSubject() // the JWT must have a subject claim
	            .setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
	            .setExpectedAudience("Audience") // to whom the JWT is intended for
	            .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
	            .build(); // create the JwtConsumer instance

	    try
	    {
	        //  Validate the JWT and process it to the Claims
	    	
	        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtSent);
	        memberID= Integer.parseInt((String)jwtClaims.getClaimValue("ID"));
	        memberRole= (String) jwtClaims.getClaimValue("Role");
	        jsonObject.put("ID", memberID);
	        jsonObject.put("Role", memberRole);
	        System.out.println("JWT validation succeeded! " + jwtClaims);
	    }
	    catch (InvalidJwtException e)
	    {
	        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
	        
	        System.out.println("Invalid JWT! " + e);
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

		return jsonObject;
	}

}
