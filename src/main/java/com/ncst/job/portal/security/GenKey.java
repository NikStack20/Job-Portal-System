package com.ncst.job.portal.security;

import java.security.Key;
import java.util.Base64;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class GenKey {
	  public static void main(String[] args) {
	    Key k = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	    System.out.println(Base64.getEncoder().encodeToString(k.getEncoded()));
	  }
	}
