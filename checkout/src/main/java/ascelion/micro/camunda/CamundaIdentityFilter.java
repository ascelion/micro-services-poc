package ascelion.micro.camunda;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.stream.Collectors.toList;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(102)
public class CamundaIdentityFilter extends OncePerRequestFilter {

	@Autowired
	private ProcessEngine engine;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final var auth = SecurityContextHolder.getContext().getAuthentication();
		final var name = auth.getName();

		if (StringUtils.isNotEmpty(name)) {
			final var ids = this.engine.getIdentityService();

			ids.setAuthentication(name, groups(auth.getAuthorities()));

			try {
				chain.doFilter(request, response);
			} finally {
				ids.clearAuthentication();
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private List<String> groups(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.map(a -> a.replaceFirst("^ROLE_", ""))
				.collect(toList());
	}

}
