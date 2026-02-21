package order_service.config.MDC_Decorator;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {

        // Capture current request thread MDC
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap); // set MDC to async thread
                }
                runnable.run();
            } finally {
                MDC.clear(); // prevent memory leak
            }
        };
    }
}

