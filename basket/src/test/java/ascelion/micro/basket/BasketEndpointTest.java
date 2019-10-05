package ascelion.micro.basket;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.reservation.api.ReservationRequest;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.reservation.api.ReservationsApi;
import ascelion.micro.tests.MockUtils;
import ascelion.micro.tests.TestsResourceServerConfig;
import ascelion.micro.tests.WithRoleAdmin;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
@WebMvcTest(BasketEndpoint.class)
@Import(TestsResourceServerConfig.class)
@ActiveProfiles("test")
public class BasketEndpointTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@Autowired
	private BeanToBeanMapper bbm;
	@MockBean(answer = Answers.CALLS_REAL_METHODS)
	private BasketRepo repo;
	@MockBean(answer = Answers.CALLS_REAL_METHODS)
	private BasketItemRepo itmRepo;
	@MockBean
	private ReservationsApi resApi;
	@MockBean
	private DataSource ds;

	private final Map<UUID, Basket> baskets = new HashMap<>();
	private ReservationResponse[] reservations;

	@Before
	public void setUp() {
		MockUtils.mockRepository(this.bbm, this.repo, this.baskets,
				() -> Basket.builder()
						.customerId(randomUUID())
						.build());

		when(this.itmRepo.findById(any()))
				.then(ivc -> {
					return this.baskets.values().stream()
							.flatMap(b -> b.getItems().stream())
							.filter(i -> i.getId().equals(ivc.getArgument(0)))
							.findAny();
				});

		when(this.resApi.reserve(any()))
				.then(ivc -> {
					return this.reservations = stream(ivc.getArguments())
							.map(ReservationRequest.class::cast)
							.map(req -> new ReservationResponse(req.getQuantity(), randomDecimal(20, 30)))
							.toArray(ReservationResponse[]::new);
				});
	}

	@Test
	@WithRoleAdmin
	public void createBasket() throws Exception {
		final var dto = new BasketRequest(randomUUID());
		final var req = post("/baskets")
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
		final var i1 = new BasketItemRequest(randomUUID(), randomDecimal(10, 20));
		final var i2 = new BasketItemRequest(randomUUID(), randomDecimal(10, 20));
		final var ent = Basket.builder().customerId(randomUUID()).build();

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

		assertThat(this.reservations, notNullValue());
		assertThat(asList(this.reservations), hasSize(2));

		assertThat(this.reservations[0], notNullValue());
		assertThat(this.reservations[0].getQuantity(), equalTo(i1.getQuantity()));
		assertThat(this.reservations[0].getPrice(), greaterThan(BigDecimal.TEN));

		assertThat(this.reservations[1], notNullValue());
		assertThat(this.reservations[1].getQuantity(), equalTo(i2.getQuantity()));
		assertThat(this.reservations[1].getPrice(), greaterThan(BigDecimal.TEN));
	}
}
