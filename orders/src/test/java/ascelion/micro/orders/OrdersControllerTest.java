package ascelion.micro.orders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import ascelion.micro.shared.utils.BeanToBeanMapper;
import ascelion.micro.tests.TestsResourceServerConfig;
import ascelion.micro.tests.WithRoleAdmin;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@WebMvcTest(OrdersController.class)
@Import(TestsResourceServerConfig.class)
@ActiveProfiles("test")
public class OrdersControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@MockBean
	private OrdersRepository repo;
	@MockBean
	private DataSource ds;

	private final Map<UUID, Order> orders = new HashMap<>();
	private final BeanToBeanMapper bbm = new BeanToBeanMapper();

	@Before
	public void setUp() {
		when(this.repo.findAll())
				.then(ivc -> {
					return new ArrayList<>(this.orders.values());
				});
		when(this.repo.findById(any()))
				.then(ivc -> {
					return ofNullable(this.orders.get(ivc.getArgument(0)));
				});
		when(this.repo.save(any()))
				.then(ivc -> {
					final Order o = ivc.getArgument(0);

					if (o.getId() == null) {
						final Order newO = this.bbm.copy(o, Order.class, false);

						setProtectedFieldValue("id", newO, randomUUID());

						this.orders.put(newO.getId(), newO);

						return newO;
					} else {
						this.orders.put(o.getId(), o);

						return o;
					}
				});
	}

	@Test
	@WithRoleAdmin
	public void createEntity() throws Exception {
		final OrderItemRequest i1 = new OrderItemRequest(randomUUID(), randomDecimal(10, 20));
		final OrderItemRequest i2 = new OrderItemRequest(randomUUID(), randomDecimal(10, 20));
		final OrderRequest dto = new OrderRequest(randomUUID(), randomUUID(), randomUUID(), asList(i1, i2));
		final MockHttpServletRequestBuilder req = post("/orders")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.items", hasSize(dto.getItems().size())));
	}
}
