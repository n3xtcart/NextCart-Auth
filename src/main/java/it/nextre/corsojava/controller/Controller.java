package it.nextre.corsojava.controller;


import org.jboss.logging.Logger;

import it.nextre.corsojava.config.ServiceProducer;
import it.nextre.corsojava.service.UserServiceInterface;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public abstract class Controller {
	
	@Inject
	ServiceProducer serviceProducer;
	protected UserServiceInterface service;
    protected static final Logger LOGGER = Logger.getLogger(Controller.class);
	
	  @PostConstruct
	    void init() {
	        service = serviceProducer.getService();
	    }


}
