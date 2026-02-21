package payment_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import payment_service.model.RequestLogs;
import payment_service.repository.RequestLoggingRepository;

@Service
public class RequestLoggingService {

    @Autowired
    RequestLoggingRepository requestLoggingRepository;

    @Async("loggingExecutor")
    public void saveRequestLog(RequestLogs requestLogs) {
        requestLoggingRepository.save(requestLogs);
    }


}