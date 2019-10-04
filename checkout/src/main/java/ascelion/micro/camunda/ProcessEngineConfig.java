package ascelion.micro.camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.spring.components.aop.ActivitiStateAnnotationBeanPostProcessor;
import org.camunda.bpm.engine.spring.components.aop.ProcessStartAnnotationBeanPostProcessor;
import org.camunda.bpm.engine.spring.components.registry.ActivitiStateHandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class ProcessEngineConfig {

	@Bean
	public ActivitiStateAnnotationBeanPostProcessor stateAnnotationBeanPostProcessor() {
		final ActivitiStateAnnotationBeanPostProcessor post = new ActivitiStateAnnotationBeanPostProcessor();

		post.setRegistry(new ActivitiStateHandlerRegistry());

		return post;
	}

	@Bean
	public ProcessStartAnnotationBeanPostProcessor startAnnotationBeanPostProcessor(ProcessEngine pe) {
		final ProcessStartAnnotationBeanPostProcessor post = new ProcessStartAnnotationBeanPostProcessor();

		post.setProcessEngine(pe);

		return post;
	}
}
