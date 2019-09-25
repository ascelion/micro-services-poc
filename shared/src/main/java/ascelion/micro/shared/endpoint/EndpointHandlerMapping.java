package ascelion.micro.shared.endpoint;

import java.lang.reflect.Method;

import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import org.slf4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class EndpointHandlerMapping extends RequestMappingHandlerMapping {
	static private final Logger LOG = loggerForThisClass();

	public EndpointHandlerMapping() {
		// Make sure user-supplied mappings take precedence by default (except the resource mapping)
		setOrder(Ordered.LOWEST_PRECEDENCE - 2);
	}

	@Override
	protected boolean isHandler(Class<?> beanType) {
		return findAnnotation(beanType, Endpoint.class) != null;
	}

	@Override
	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		final RequestMappingInfo mapping = super.getMappingForMethod(method, handlerType);

		if (mapping == null) {
			return null;
		}

		final Endpoint annotation = findAnnotation(handlerType, Endpoint.class);
		final PatternsRequestCondition patterns = new PatternsRequestCondition(annotation.value(),
				getUrlPathHelper(),
				getPathMatcher(),
				useSuffixPatternMatch(),
				useTrailingSlashMatch(),
				getFileExtensions())
						.combine(mapping.getPatternsCondition());

		if (LOG.isTraceEnabled()) {
			LOG.trace("Remapped to {}: {}", patterns, mapping);
		}

		return new RequestMappingInfo(mapping.getName(),
				patterns,
				mapping.getMethodsCondition(),
				mapping.getParamsCondition(),
				mapping.getHeadersCondition(),
				mapping.getConsumesCondition(),
				mapping.getProducesCondition(),
				mapping.getCustomCondition());
	}

}
