package com.taskService;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.taskService.interceptor.RequestProcessingInterceptor;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@ComponentScan({ "com.taskService" })
@PropertySource({ "classpath:persistence-mongo.properties" })
@Configuration
public class TaskServiceApplication extends WebMvcConfigurerAdapter{

	public static void main(String[] args) {
		SpringApplication.run(TaskServiceApplication.class, args);
	}
	
	@Autowired
	private Environment env;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any())
				.build().directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class);
	}

	@Bean(name = "dataSourceClient")
	public MongoDatabase dataSource() {

		String host = env.getProperty("mongo.url").toString();

		Object portOb = env.getProperty("mongo.port");
		Integer port = Integer.parseInt(portOb.toString());

		String db = env.getProperty("mongo.dataBase").toString();

		 MongoClient client = new MongoClient(env.getProperty("mongo.url"),
				Integer.valueOf(env.getProperty("mongo.port")));
		 
		 return client.getDatabase(env
					.getProperty("mongo.dataBase"));
		 

	}
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	RedisTemplate<String, Object> redisTemplate() {
		final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericToStringSerializer<Object>(
				Object.class));
		template.setValueSerializer(new GenericToStringSerializer<Object>(
				Object.class));
		return template;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RequestProcessingInterceptor()).excludePathPatterns("/api/taskapp/user/**");
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}
