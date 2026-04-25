package Aspects;

import MyAnnotations.Loggable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class LoggableAspect {
    private final Logger logForMethods = LoggerFactory.getLogger("LFM");

    @Around("@annotation(loggable)")
    public Object around(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        String msg = loggable.value();
        logByLevel(loggable.level(), msg);
        return joinPoint.proceed();
    }

    private void logByLevel(LogLevel level, String message) {
        switch (level) {
            case TRACE -> logForMethods.trace(message);
            case DEBUG -> logForMethods.debug(message);
            case INFO -> logForMethods.info(message);
            case WARN -> logForMethods.warn(message);
            case ERROR, FATAL -> logForMethods.error(message);
            case OFF -> {

            }
        }
    }
}