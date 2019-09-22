//package ascelion.micro.config;
//
//import javax.sql.DataSource;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//
//@Configuration
//@EnableAuthorizationServer
//@RequiredArgsConstructor
//public class AuthzServerConfig extends AuthorizationServerConfigurerAdapter {
//	private final DataSource ds;
//	private final PasswordEncoder passwordEncoder;
////	private final AuthenticationManager authenticationManager;
////	private final AccessTokenConverter accessTokenConverter;
////	private final UserDetailsService userDetailsService;
////	private final TokenStore tokenStore;
//
//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//		security
////				.accessDeniedHandler(this::forbidden)
////				.authenticationEntryPoint(this::unauthorized)
//				.tokenKeyAccess("permitAll()")
//				.checkTokenAccess("isAuthenticated()");
//	}
//
//	@Override
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		clients
//				.jdbc(this.ds)
//				.passwordEncoder(this.passwordEncoder);
//	}
//
////	@Override
////	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
////		endpoints
////				.authenticationManager(this.authenticationManager)
////				.accessTokenConverter(this.accessTokenConverter)
////				.userDetailsService(this.userDetailsService)
////				.tokenStore(this.tokenStore);
////	}
//
////	private void unauthorized(HttpServletRequest req, HttpServletResponse rsp, AuthenticationException ex) throws IOException, ServletException {
////		rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
////	}
//
////	private void forbidden(HttpServletRequest req, HttpServletResponse rsp, AccessDeniedException ex) throws IOException, ServletException {
////		rsp.sendError(HttpServletResponse.SC_FORBIDDEN);
////	}
//}
