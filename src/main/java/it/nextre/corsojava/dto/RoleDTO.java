package it.nextre.corsojava.dto;

import it.nextre.corsojava.entity.Role;

public class RoleDTO {
	private String descrizione;
	private Boolean admin;
	private Long priority;
	private Long id;

	public RoleDTO(Role role) {
		this.id = role.getId();
		this.descrizione = role.getDescrizione();
		this.admin = role.isAdmin();
		this.priority = role.getPriority();

	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
