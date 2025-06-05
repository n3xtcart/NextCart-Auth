package it.nextre.corsojava.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.service.UserAdminService.UserAdminServiceJdbc;
import it.nextre.corsojava.service.UserAdminService.UserAdminServiceMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserAdminServiceProducer {

    @ConfigProperty(name = "source.Mem")
    private String serviceType;

    @Inject
    private UserAdminServiceJdbc serviceJdbc;
    @Inject
    private UserAdminServiceMemory service;

    @Produces
    @Default
    public UserAdminService getService() {

        return switch (serviceType) {
            case "db" -> serviceJdbc;
            case "mem" -> service;
            default -> throw new IllegalStateException("Unexpected value: " + serviceType);
        };
    }
}
