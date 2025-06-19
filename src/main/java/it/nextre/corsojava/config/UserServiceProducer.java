package it.nextre.corsojava.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.nextre.aut.service.UserService;
import it.nextre.corsojava.service.user.UserServiceJdbc;
import it.nextre.corsojava.service.user.UserServiceMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class UserServiceProducer {

    @ConfigProperty(name = "source.Mem")
    private String serviceType;

    @Inject
    @Named("defaultJdbc")
    private UserServiceJdbc serviceJdbc;
    @Inject
    @Named("defaultMemory")
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
