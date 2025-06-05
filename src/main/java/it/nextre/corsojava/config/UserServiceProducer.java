package it.nextre.corsojava.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.nextre.aut.service.UserService;
import it.nextre.corsojava.service.UserService.UserServiceJdbc;
import it.nextre.corsojava.service.UserService.UserServiceMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserServiceProducer {

    @ConfigProperty(name = "source.Mem")
    private String serviceType;

    @Inject
    private UserServiceJdbc serviceJdbc;
    @Inject
    private UserServiceMemory service;

    @Produces
    @Default
    public UserService getService() {

        return switch (serviceType) {
            case "db" -> serviceJdbc;
            case "mem" -> service;
            default -> throw new IllegalStateException("Unexpected value: " + serviceType);
        };
    }
}
