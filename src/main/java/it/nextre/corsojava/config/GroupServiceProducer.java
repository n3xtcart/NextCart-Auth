package it.nextre.corsojava.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.nextre.aut.service.GroupService;
import it.nextre.corsojava.service.groupService.GroupServiceJdbc;
import it.nextre.corsojava.service.groupService.GroupServiceMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class GroupServiceProducer {

    @ConfigProperty(name = "source.Mem")
    private String serviceType;

    @Inject
    private GroupServiceJdbc serviceJdbc;
    @Inject
    private GroupServiceMemory service;

    @Produces
    @Default
    public GroupService getService() {

        return switch (serviceType) {
            case "db" -> serviceJdbc;
            case "mem" -> service;
            default -> throw new IllegalStateException("Unexpected value: " + serviceType);
        };
    }
}
