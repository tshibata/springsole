package com.x7th.sole;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

@Configuration
public class SoleConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/versioned/**")
		.addResourceLocations("classpath:/static/")
		.setCachePeriod(24 * 60 * 60)
		.resourceChain(true)
		.addResolver((new VersionResourceResolver()).addContentVersionStrategy("/**"));
	}
}

