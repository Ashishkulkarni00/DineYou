package payment_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import payment_service.model.BusinessEventLog;
import payment_service.repository.BusinessEventRepository;

@Service
public class BusinessEventLoggingService {

    @Autowired
    BusinessEventRepository businessEventRepository;

    @Async("loggingExecutor")
    public void saveBusinessEvent(BusinessEventLog businessEventLog){
        businessEventRepository.save(businessEventLog);
    }

}
