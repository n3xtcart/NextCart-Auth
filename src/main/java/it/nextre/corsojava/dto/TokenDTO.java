package it.nextre.corsojava.dto;

import it.nextre.corsojava.entity.Token;

public class TokenDTO {
    private String token;
    private UserDTO userDTO;

    public TokenDTO(Token token) {
		this.token = token.getValue();
		this.userDTO = new UserDTO(token.getUser());
	
	}

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
