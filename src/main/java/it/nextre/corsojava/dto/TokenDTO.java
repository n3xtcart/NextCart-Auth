package it.nextre.corsojava.dto;

import it.nextre.corsojava.entity.Token;

public class TokenDTO {
    private String value;
    private UserDTO userDTO;

  
    public TokenDTO() {
    	
    }

    public TokenDTO(Token tokenByValue) {

    			this.value = tokenByValue.getValue();
		if (tokenByValue.getUser() != null) {
			this.userDTO = new UserDTO(tokenByValue.getUser());
		} else {
			this.userDTO = null;
		}
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
