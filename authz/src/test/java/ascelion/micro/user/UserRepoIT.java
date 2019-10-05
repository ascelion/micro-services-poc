package ascelion.micro.user;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import ascelion.micro.config.OauthProperties;
import ascelion.micro.config.SafePasswordEncoder;
import ascelion.micro.tests.JpaEntityIT;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@JpaEntityIT
@Import({
		SafePasswordEncoder.class,
})
@EnableConfigurationProperties(OauthProperties.class)
public class UserRepoIT {
	@Autowired
	private EntityManager tem;

	@Autowired
	private UserRepo repo;

	@Autowired
	private PasswordEncoder pwe;

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_table_mappings() {
		final var u1 = new User("username", "password1");

		this.tem.setFlushMode(FlushModeType.AUTO);

		this.tem.persist(u1);
		this.tem.flush();
		this.tem.detach(u1);

		assertThat(u1.getId(), notNullValue());

		final var u1o = this.repo.findById(u1.getId());

		assertThat(u1o.isPresent(), is(true));

		User u21 = u1o.get();

		assertThat(u21, not(sameInstance(u1)));

		assertThat(u21.getId(), notNullValue());
		assertThat(u21.getCreatedAt(), notNullValue());
		assertThat(u21.getUpdatedAt(), notNullValue());
		assertThat(u21.getUsername(), equalTo(u1.getUsername()));
		assertThat(u21.getPassword(), equalTo(u1.getPassword()));

		assertThat(this.pwe.matches("password1", u21.getPassword()), is(true));

		u21.setPassword("password2");

		u21 = this.tem.merge(u21);
		this.tem.flush();
		this.tem.detach(u21);

		var u22 = this.repo.findById(u1.getId()).get();

		assertThat(this.pwe.matches("password2", u22.getPassword()), is(true));

		u22 = this.tem.merge(u22);
		this.tem.flush();
		this.tem.detach(u22);

		assertThat(this.pwe.matches("password2", u22.getPassword()), is(true));
	}
}
