package com.backstage.system.service.outbox;

public interface OutboxEventPublisher {

    void publishEventById(Long eventId);
}
