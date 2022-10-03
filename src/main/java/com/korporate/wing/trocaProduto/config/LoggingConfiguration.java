package com.korporate.wing.trocaProduto.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.boolex.OnMarkerEvaluator;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.Iterator;

@Configuration
public class LoggingConfiguration {

    private static final String LOGSTASH_APPENDER_NAME = "WING";

    private static final String ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH";

    private static final String ASYNC_CONSOLE_APPENDER_NAME = "CONSOLE";

    private final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    private LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    private final String appName;

    private final String serverPort;

    private final Boolean logstashEnabled;

    private final String elkHost;

    private final Integer elkPort;

    private final Integer elkQueueSize;

    public LoggingConfiguration(@Value("${spring.application.name}") String appName,
                                @Value("${server.port}") String serverPort,
                                @Value("${logstash.enabled}") Boolean logstashEnabled,
                                @Value("${logstash.host}") String elkHost,
                                @Value("${logstash.port}") Integer elkPort,
                                @Value("${logstash.queue-size}") Integer elkQueueSize) {
        this.appName = appName;
        this.serverPort = serverPort;
        this.logstashEnabled = logstashEnabled;
        this.elkHost = elkHost;
        this.elkPort = elkPort;
        this.elkQueueSize = elkQueueSize;
        if (logstashEnabled) {
            addLogstashAppender(context);
            addContextListener(context);
            setMetricsMarkerLogbackFilter(context);
        }
        addConsoleAppender(context);
        addFileAppender(context, "/tmp/wing-troca-produto-error.log");
    }

    private void addFileAppender(LoggerContext context, String file) {
        log.info("Initializing logging file");
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(context);
        ple.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(context);
        fileAppender.start();
    }

    private void addConsoleAppender(LoggerContext context) {
        log.info("Initializing logging");
        // Wrap the appender in an Async appender for performance
        AsyncAppender appender = new AsyncAppender();
        appender.setContext(context);
        appender.setName(ASYNC_CONSOLE_APPENDER_NAME);
        appender.start();

    }

    private void addContextListener(LoggerContext context) {
        LogbackLoggerContextListener loggerContextListener = new LogbackLoggerContextListener();
        loggerContextListener.setContext(context);
        context.addListener(loggerContextListener);
    }

    private void addLogstashAppender(LoggerContext context) {
        log.info("Initializing Logstash logging");

        LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
        logstashAppender.setName(LOGSTASH_APPENDER_NAME);
        logstashAppender.setContext(context);
        String customFields = "{\"app_name\":\"" + appName + "\",\"app_port\":\"" + serverPort + "\"}";

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        LogstashEncoder logstashEncoder = new LogstashEncoder();
        logstashEncoder.setCustomFields(customFields);
        logstashAppender.addDestinations(new InetSocketAddress(elkHost, elkPort));

        ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
        throwableConverter.setRootCauseFirst(true);
        logstashEncoder.setThrowableConverter(throwableConverter);
        logstashEncoder.setCustomFields(customFields);

        logstashAppender.setEncoder(logstashEncoder);
        logstashAppender.start();

        // Wrap the appender in an Async appender for performance
        AsyncAppender asyncLogstashAppender = new AsyncAppender();
        asyncLogstashAppender.setContext(context);
        asyncLogstashAppender.setName(ASYNC_LOGSTASH_APPENDER_NAME);
        asyncLogstashAppender.setQueueSize(elkQueueSize);
        asyncLogstashAppender.addAppender(logstashAppender);
        asyncLogstashAppender.start();

        context.getLogger("ROOT").addAppender(asyncLogstashAppender);
    }

    // Configure a log filter to remove "metrics" logs from all appenders except the "LOGSTASH" appender
    private void setMetricsMarkerLogbackFilter(LoggerContext context) {
        log.info("Filtering metrics logs from all appenders except the {} appender", LOGSTASH_APPENDER_NAME);
        OnMarkerEvaluator onMarkerMetricsEvaluator = new OnMarkerEvaluator();
        onMarkerMetricsEvaluator.setContext(context);
        onMarkerMetricsEvaluator.addMarker("metrics");
        onMarkerMetricsEvaluator.start();
        EvaluatorFilter<ILoggingEvent> metricsFilter = new EvaluatorFilter<>();
        metricsFilter.setContext(context);
        metricsFilter.setEvaluator(onMarkerMetricsEvaluator);
        metricsFilter.setOnMatch(FilterReply.DENY);
        metricsFilter.start();

        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders(); it.hasNext(); ) {
                Appender<ILoggingEvent> appender = it.next();
                if (!appender.getName().equals(ASYNC_LOGSTASH_APPENDER_NAME)) {
                    log.debug("Filter metrics logs from the {} appender", appender.getName());
                    appender.setContext(context);
                    appender.addFilter(metricsFilter);
                    appender.start();
                }
            }
        }
    }

    /**
     * Logback configuration is achieved by configuration file and API.
     * When configuration file change is detected, the configuration is reset.
     * This listener ensures that the programmatic configuration is also re-applied after reset.
     */
    class LogbackLoggerContextListener extends ContextAwareBase implements LoggerContextListener {

        @Override
        public boolean isResetResistant() {
            return true;
        }

        @Override
        public void onStart(LoggerContext context) {
            addLogstashAppender(context);
        }

        @Override
        public void onReset(LoggerContext context) {
            addLogstashAppender(context);
        }

        @Override
        public void onStop(LoggerContext context) {
            // Nothing to do.
        }

        @Override
        public void onLevelChange(ch.qos.logback.classic.Logger logger, Level level) {
            // Nothing to do.
        }
    }

}
