package it.nextre.corsojava.dto;

import it.nextre.corsojava.entity.Token;

public class TokenDTO {
    private String value;
    private UserDTO userDTO;

    public TokenDTO(Token token) {
		this.value = token.getValue();
		this.userDTO = new UserDTO(token.getUser());
	
	}
    public TokenDTO() {
    	
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public String getValue() {
        return value;
    }

    public void setToken(String value) {
        this.value = value;
    }
}
