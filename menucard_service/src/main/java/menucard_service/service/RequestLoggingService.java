package menucard_service.service;

import menucard_service.model.RequestLogs;
import menucard_service.repository.RequestLoggingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RequestLoggingService {

    @Autowired
    RequestLoggingRepository requestLoggingRepository;

    @Async("loggingExecutor")
    public void saveRequestLog(RequestLogs requestLogs) {
        requestLoggingRepository.save(requestLogs);
    }


}
