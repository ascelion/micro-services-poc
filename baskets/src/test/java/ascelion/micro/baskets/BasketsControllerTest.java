package ascelion.micro.baskets;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.reservations.ReservationRequest;
import ascelion.micro.reservations.ReservationsApi;
import ascelion.micro.tests.TestsResourceServerConfig;
import ascelion.micro.tests.WithRoleAdmin;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.util.FieldUtils.setProtectedFieldValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@WebMvcTest(BasketsController.class)
@Import(TestsResourceServerConfig.class)
@ActiveProfiles("test")
public class BasketsControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@Autowired
	private BeanToBeanMapper bbm;
	@MockBean(answer = Answers.CALLS_REAL_METHODS)
	private BasketsRepository repo;
	@MockBean
	private ReservationsApi resApi;
	@MockBean
	private DataSource ds;

	private final Map<UUID, Basket> baskets = new HashMap<>();

	@Before
	public void setUp() {
		when(this.resApi.reserve(any()))
				.then(ivc -> {
					final Object[] arg = ivc.getArguments();
					final ReservationRequest[] ret = new ReservationRequest[arg.length];

					System.arraycopy(arg, 0, ret, 0, ret.length);

					return ret;
				});
		when(this.repo.findAll())
				.then(ivc -> {
					return this.baskets.values()
							.stream()
							.map(e -> this.bbm.create(Basket.class, e))
							.collect(toList());
				});
		when(this.repo.findByItemId(any()))
				.then(ivc -> {
					return this.baskets.values().stream()
							.filter(b -> {
								return b.getItems().stream().anyMatch(i -> i.getId().equals(ivc.getArgument(0)));
							})
							.findAny();
				});
		when(this.repo.findById(any()))
				.then(ivc -> {
					return ofNullable(this.baskets.get(ivc.getArgument(0)))
							.map(e -> this.bbm.create(Basket.class, e));
				});
		when(this.repo.save(any()))
				.then(ivc -> {
					final Basket o = ivc.getArgument(0);

					if (o.getId() == null) {
						setProtectedFieldValue("id", o, randomUUID());
						setProtectedFieldValue("createdAt", o, LocalDateTime.now());
						setProtectedFieldValue("updatedAt", o, LocalDateTime.now());
					}

					this.baskets.put(o.getId(), o);

					return o;
				});
	}

	@Test
	@WithRoleAdmin
	public void createBasket() throws Exception {
		final BasketRequest dto = new BasketRequest(randomUUID());
		final MockHttpServletRequestBuilder req = post("/baskets")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.id", notNullValue()));
	}

	@Test
	@WithRoleAdmin
	public void addItems() throws Exception {
		final BasketItemRequest i1 = new BasketItemRequest(randomUUID(), randomDecimal(10, 20));
		final BasketItemRequest i2 = new BasketItemRequest(randomUUID(), randomDecimal(10, 20));
		final Basket ent = Basket.builder().customerId(randomUUID()).build();

		this.repo.save(ent);

		final MockHttpServletRequestBuilder req = post("/baskets/{id}", ent.getId())
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(asList(i1, i2)));

		this.mvc.perform(req)
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.items", hasSize(2)));
	}
}
