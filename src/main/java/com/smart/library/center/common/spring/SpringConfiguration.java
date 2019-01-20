package com.smart.library.center.common.spring;


import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.mongodb.MongoClient;
import com.smart.library.center.common.config.Config;
import com.smart.library.center.common.mongodb.MongoFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 */
@Slf4j
//@Configuration
@ComponentScan("com.smart.library")
public class SpringConfiguration {

    /*@Bean
    public Module elementsJacksonModule() {
        return new CloudJacksonModule();
    }*/

    @Bean
    public HttpMessageConverters customConverters(ObjectProvider<MappingJackson2HttpMessageConverter> jackson) {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        jackson.ifAvailable(converters::add);

        BufferedImageHttpMessageConverter bihm = new BufferedImageHttpMessageConverter();
        bihm.setDefaultContentType(MediaType.IMAGE_JPEG);
        converters.add(bihm);

        return new HttpMessageConverters(false, converters);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /*@Lazy
    @Bean
    public BusinessLogger businessLogger(AmqpTemplate amqpTemplate) {
        return new BusinessLogger(amqpTemplate);
    }*/

    /*@Lazy
    @Bean
    public ResourceMessageSender resourceMessageSender(AmqpTemplate amqpTemplate) {
        return new ResourceMessageSender(amqpTemplate);
    }*/


    @Bean
    public UriTemplateHandler rootUriTemplateHandler(Config config) {
        String uri = config.getHttpServerUri();
        return new RootUriTemplateHandler(uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri);
    }

    @Bean
    @Lazy
    @Primary
    public RestTemplate restTemplate(HttpMessageConverters converters, UriTemplateHandler handler) {
        RestTemplate template = new RestTemplate(converters.getConverters());
        template.setUriTemplateHandler(handler);
        return template;

    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Lazy
    @Bean
    public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
                                                 OAuth2ProtectedResourceDetails details,
                                                 UriTemplateHandler handler,
                                                 HttpMessageConverters converters) {
        OAuth2RestTemplate template = new OAuth2RestTemplate(details, oauth2ClientContext);
        template.setMessageConverters(converters.getConverters());
        template.setUriTemplateHandler(handler);
        return template;
    }


    @SuppressWarnings("deprecation")
   /* @Lazy
    @Bean
    public TokenClient tokenClient(Config config) {
        return new TokenClient(config);
    }*/

    /*@Lazy
    @Bean
    public TaskClient taskClient(Config config, OAuth2RestTemplate template, ObjectMapper objectMapper) {
        String apiServerUrl = config.getPublic().getApiServerUrl();
        return new DefaultTaskClientImpl(apiServerUrl, template, objectMapper);
    }*/

    @Lazy
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        return taskScheduler;
    }

    @Lazy
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        return executor;
    }

   /* @Bean
    public ConversionServiceFactoryBean conversionServiceFactoryBean() {
        ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
        Set<Converter<?, ?>> converters = Sets.newHashSet(new StringToDateConverter(), new LongToDateConverter());
        factoryBean.setConverters(converters);
        return factoryBean;
    }*/

    @Bean
    public MongoFactory mongoFactory(MongoClient mongo) {
        return new MongoFactory(mongo);
    }

    /*@Bean
    @Lazy
    public DeviceService globalDeviceService(MongoFactory factory) {
        return new DeviceService(factory);
    }*/
}