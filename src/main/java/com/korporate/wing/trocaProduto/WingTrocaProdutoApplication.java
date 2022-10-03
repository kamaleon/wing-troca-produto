package com.korporate.wing.trocaProduto;

import com.korporate.spring.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;


@SpringBootApplication(scanBasePackages = "com.korporate")
@EntityScan(basePackages = "com.korporate",
            basePackageClasses = {WingTrocaProdutoApplication.class, Jsr310JpaConverters.class})
@EnableJpaRepositories(basePackages = "com.korporate")
@EnableScheduling
@EnableFeignClients
public class WingTrocaProdutoApplication
{
    private static Logger logger = LoggerFactory.getLogger(WingTrocaProdutoApplication.class);

    public static void main(String[] args)
    {
        // IPv4
        System.setProperty("java.net.preferIPv4Stack", "true");
        // timezone
        TimeZone.setDefault(TimeZone.getTimeZone("America/Recife"));

        SpringApplication app = new SpringApplication(WingTrocaProdutoApplication.class);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env)
    {
        // protocol
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null)
            protocol = "https";

        // port
        String serverPort = env.getProperty("server.port");

        // path
        String contextPath = env.getProperty("server.servlet.context-path");
        if (!StringUtil.check(contextPath))
            contextPath = "/";

        // host
        String hostAddress = "localhost";
        try
        {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            logger.warn("Não foi possivel determinar o nome do host, usando 'localhost' como tratamento de falho");
        }

        logger.info("\n----------------------------------------------------------\n\t" +
                        "A aplicação '{}' está rodando! URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "Externa: \t{}://{}:{}{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                env.getActiveProfiles());
    }

}
