package order_service.service;

import order_service.model.BusinessEventLog;
import order_service.repository.BusinessEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BusinessEventLoggingService {

    @Autowired
    BusinessEventRepository businessEventRepository;

    @Async("loggingExecutor")
    public void saveBusinessEvent(BusinessEventLog businessEventLog){
        businessEventRepository.save(businessEventLog);
    }

}
