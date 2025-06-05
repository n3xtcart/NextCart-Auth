package it.nextre.corsojava.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.nextre.aut.service.RoleService;
import it.nextre.corsojava.service.roleService.RoleServiceJdbc;
import it.nextre.corsojava.service.roleService.RoleServiceMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class RoleServiceProducer {

    @ConfigProperty(name = "source.Mem")
    private String serviceType;

    @Inject
    private RoleServiceJdbc serviceJdbc;
    @Inject
    private RoleServiceMemory service;

    @Produces
    @Default
    public RoleService getService() {

        return switch (serviceType) {
            case "db" -> serviceJdbc;
            case "mem" -> service;
            default -> throw new IllegalStateException("Unexpected value: " + serviceType);
        };
    }
}
