package edu.neu.ecommerce.admin.notifier;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@SuppressWarnings("all")
public class EmailNotifier extends AbstractEventNotifier {

    protected EmailNotifier(InstanceRepository repository) {
        super(repository);
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(()-> {
            if(event instanceof InstanceStatusChangedEvent){
                log.info("Instance Status Change: [{}], [{}], [{}]",
                        instance.getRegistration().getName(),
                        event.getInstance(),
                        ((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
            }else{
                log.info("Instance info [{}], [{}], [{}]",
                        instance.getRegistration().getName(),
                        event.getInstance(),
                        event.getType());
            }
        });

    }
}
