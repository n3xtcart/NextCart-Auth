package it.nextre.corsojava.service.UserAdminService;

import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.utils.EntityConverter;

public class UserAdminServiceMemory extends UserAdminServiceJdbc implements UserAdminService {

	public UserAdminServiceMemory(EntityConverter entityConverter) {
		super(entityConverter);
	}



}
