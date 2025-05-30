package it.nextre.corsojava.config;

import it.nextre.corsojava.service.UserService;
import it.nextre.corsojava.service.UserServiceInterface;
import it.nextre.corsojava.service.UserServiceJdbc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ServiceProducer {

    @ConfigProperty(name = "source.Mem")
    private String serviceType;

    @Inject
    private UserServiceJdbc serviceJdbc;
    @Inject
    private UserService service;

    @Produces
    @Default
    public UserServiceInterface getService() {

        return switch (serviceType) {
            case "db" -> serviceJdbc;
            case "mem" -> service;
            default -> throw new IllegalStateException("Unexpected value: " + serviceType);
        };
    }
}
