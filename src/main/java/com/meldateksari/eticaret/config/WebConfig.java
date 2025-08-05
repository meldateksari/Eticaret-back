package com.meldateksari.eticaret.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/users/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(0); // İsteğe bağlı: önbellek kapal
    }
}

