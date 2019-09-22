package ascelion.micro.tests;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithRole("USERS")
public @interface WithUsersRole {
}
