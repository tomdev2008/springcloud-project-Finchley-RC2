package com.efe.ms.zuulgateway.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * 
 * <p>
 * swagger文档聚合(聚合各个微服务的api):
 * </p>
 * 
 * @author Liu TianLong
 * @date 2018年9月25日 下午4:00:48
 */
@Component
@Primary
public class AggregationSwaggerResourcesProvider implements
		SwaggerResourcesProvider {

	private static final String REPLACE_PATTERN = "\\*\\*";
	private static final String SWAGGER_DOCS_PATH = "v2/api-docs";
	private static final String SWAGGER_VERSION = "2.0";
	private static final Pattern IGNORE_PATH_PATTERN = Pattern
			.compile("config-server|\\w*(-route)$");

	private final RouteLocator routeLocator;

	public AggregationSwaggerResourcesProvider(RouteLocator routeLocator) {
		this.routeLocator = routeLocator;
	}

	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<SwaggerResource>();
		routeLocator.getRoutes().forEach(route -> {
			if (!IGNORE_PATH_PATTERN.matcher(route.getId()).matches()) { // 过滤不需要聚合的API信息
					resources.add(swaggerResources(
							route.getId(),
							route.getFullPath().replaceAll(REPLACE_PATTERN,
									SWAGGER_DOCS_PATH), SWAGGER_VERSION));
				}
			});
		return resources;
	}

	private SwaggerResource swaggerResources(String name, String location,
			String version) {
		SwaggerResource resource = new SwaggerResource();
		resource.setName(name);
		resource.setLocation(location);
		resource.setSwaggerVersion(version);
		return resource;
	}

}
