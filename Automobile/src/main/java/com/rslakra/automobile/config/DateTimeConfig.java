package com.rslakra.automobile.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

/**
 * @author Rohtash Lakra
 * @created 4/26/23 4:58 PM
 */
@Configuration
public class DateTimeConfig implements WebMvcConfigurer {

    public static final String DATE_PATTERN = "MM-dd-yyyy";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Register date/time formatters
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar dateTimeRegistrar = new DateTimeFormatterRegistrar();
        dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ofPattern(DATE_PATTERN));
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        dateTimeRegistrar.registerFormatters(registry);

        DateFormatterRegistrar dateRegistrar = new DateFormatterRegistrar();
        dateRegistrar.setFormatter(new DateFormatter(DATE_PATTERN));
        dateRegistrar.registerFormatters(registry);
    }
}

