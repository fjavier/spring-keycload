package org.banking.demo;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@Slf4j
public class CustomEventListenerFactory implements EventListenerProviderFactory {
    public static final String PROVIDER_ID = "custom-event-logger";

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new CustomEventListener();
    }

    @Override
    public void init(Config.Scope scope) {
       log.info("[KC-SPI] Initializing custom logging SPI");
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
