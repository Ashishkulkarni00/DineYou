package restaurant_service.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import restaurant_service.model.RequestLogs;
import restaurant_service.repository.RequestLoggingRepository;

@Service
public class RequestLoggingService {

    @Autowired
    RequestLoggingRepository requestLoggingRepository;

    @Async("loggingExecutor")
    public void saveRequestLog(RequestLogs requestLogs) {
        requestLoggingRepository.save(requestLogs);
    }

}
