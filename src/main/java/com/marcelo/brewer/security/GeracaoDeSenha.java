package com.marcelo.brewer.security;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeracaoDeSenha {

//	@Autowired
//	private BCryptPasswordEncoder encoder;
	
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println("Senha nova >>>>> " + encoder.encode("admin"));
	}
	
}
