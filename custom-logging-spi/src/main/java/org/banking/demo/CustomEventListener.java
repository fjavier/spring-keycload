package org.banking.demo;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

@Slf4j
public class CustomEventListener implements EventListenerProvider {
    @Override
    public void onEvent(Event event) {
        log.info("Evento de usuario: type={} userId={} ip={} details={}",
                event.getType(),
                event.getUserId(),
                event.getIpAddress(),
                event.getDetails());
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        log.info("Evento de administración: op={}, resource={}, realm={}",
                adminEvent.getOperationType(),
                adminEvent.getResourcePath(),
                adminEvent.getRealmId());
    }

    @Override
    public void close() {
        log.debug("Cerrar CustomEventListener");
    }
}
