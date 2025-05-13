package it.nextre.corsojava.dto;

import it.nextre.corsojava.entity.User;

public class UserDTO {
	private Long id;
	private String nome;
	private String cognome;
	private String email;
	private String password;
	private GroupDTO groupDTO;

	public UserDTO(User user) {
		this.id = user.getId();
		this.nome = user.getNome();
		this.cognome = user.getCognome();
		this.email = user.getEmail();
		this.password=user.getPassword();
		if (user.getGroup() != null) {
			this.groupDTO = new GroupDTO(user.getGroup());
		}

	}

	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public GroupDTO getGroupDTO() {
		return groupDTO;
	}

	public void setGroupDTO(GroupDTO groupDTO) {
		this.groupDTO = groupDTO;
	}
}
