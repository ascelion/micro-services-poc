package ascelion.micro.products;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithMockUser;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "admin", roles = "ADMIN")
public @interface WithAdmin {
}
