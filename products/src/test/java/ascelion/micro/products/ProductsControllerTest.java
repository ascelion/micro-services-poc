package ascelion.micro.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.utils.BeanToBeanMapper;
import ascelion.micro.tests.TestsResourceServerConfig;
import ascelion.micro.tests.WithRoleAdmin;
import ascelion.micro.tests.WithRoleUser;

import static ascelion.micro.tests.RandomUtils.randomAscii;
import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.util.FieldUtils.setProtectedFieldValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
@WebMvcTest(ProductsController.class)
@Import(TestsResourceServerConfig.class)
@ActiveProfiles("test")
public class ProductsControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@MockBean
	private ProductsRepository repo;
	@MockBean
	private DataSource ds;

	private final Map<UUID, Product> products = new HashMap<>();
	private final BeanToBeanMapper bbm = new BeanToBeanMapper();

	@Before
	public void setUp() {
		for (int k = 0; k < 10; k++) {
			final LocalDateTime t = LocalDateTime.now();
			final Product p = Product.builder()
					.name(randomAscii(10, 20))
					.description(randomAscii(10, 20))
					.price(randomDecimal(0, 100))
					.stock(randomDecimal(0, 100))
					.build();

			setProtectedFieldValue("id", p, randomUUID());
			setProtectedFieldValue("createdAt", p, t);
			setProtectedFieldValue("updatedAt", p, t);

			this.products.put(p.getId(), p);
		}

		when(this.repo.findAll())
				.then(ivc -> {
					return new ArrayList<>(this.products.values());
				});
		when(this.repo.findById(any()))
				.then(ivc -> {
					return ofNullable(this.products.get(ivc.getArgument(0)));
				});
		when(this.repo.save(any()))
				.then(ivc -> {
					final Product p = ivc.getArgument(0);

					if (p.getId() == null) {
						final Product newP = this.bbm.copy(p, Product.class, false);

						setProtectedFieldValue("id", newP, randomUUID());

						this.products.put(newP.getId(), newP);

						return newP;
					} else {
						this.products.put(p.getId(), p);

						return p;
					}
				});
	}

	@Test
	@WithRoleUser
	public void getEntities() throws Exception {
		final MockHttpServletRequestBuilder req = get("/products")
				.accept(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(this.products.size())))
				.andExpect(jsonPath("$[0].id", equalTo(this.products.values().iterator().next().getId().toString())));
	}

	@Test
	public void getEntitiesAnonymous() throws Exception {
		final MockHttpServletRequestBuilder req = get("/products")
				.accept(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithRoleUser
	public void getEntitiesInvalid() throws Exception {
		final MockHttpServletRequestBuilder req = get("/products")
				.param("page", "0")
				.param("size", "5")
				.accept(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.messages", hasSize(1)));
	}

	@Test
	@WithRoleUser
	public void getEntity() throws Exception {
		final UUID id = this.products.values().stream().skip(this.products.size() / 2).findFirst().map(AbstractEntity::getId).get();
		final MockHttpServletRequestBuilder req = get("/products/{id}", id)
				.accept(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.id", equalTo(id.toString())));
	}

	@Test
	@WithRoleUser
	public void getEntityNotFound() throws Exception {
		final UUID id = UUID.randomUUID();
		final MockHttpServletRequestBuilder req = get("/products/{id}", id)
				.accept(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.messages", hasSize(1)));
	}

	@Test
	@WithRoleAdmin
	public void createEntity() throws Exception {
		final ProductRequest dto = new ProductRequest("add name", "add description", BigDecimal.ONE);
		final MockHttpServletRequestBuilder req = post("/products")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.name", equalTo(dto.getName())))
				.andExpect(jsonPath("$.description", equalTo(dto.getDescription())))
				.andExpect(jsonPath("$.price", equalTo(dto.getPrice().intValue())));
	}

	@Test
	@WithRoleAdmin
	public void createEntityInvalid() throws Exception {
		final ProductRequest dto = new ProductRequest(null, null, null);
		final MockHttpServletRequestBuilder req = post("/products")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.messages", hasSize(3)));
	}

	@Test
	public void createEntityAsAnonymous() throws Exception {
		final MockHttpServletRequestBuilder req = post("/products")
				.contentType(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithRoleUser
	public void createEntityAsUser() throws Exception {
		final ProductRequest dto = new ProductRequest("add name", "add description", BigDecimal.ONE);
		final MockHttpServletRequestBuilder req = post("/products")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isForbidden());
	}

	@Test
	@WithRoleAdmin
	public void updateEntity() throws Exception {
		final ProductRequest dto = new ProductRequest("new name", "new description", BigDecimal.ONE);

		final UUID id = this.products.values().stream().skip(this.products.size() / 2).findFirst().map(AbstractEntity::getId).get();
		final MockHttpServletRequestBuilder req = put("/products/{id}", id)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.id", equalTo(id.toString())))
				.andExpect(jsonPath("$.name", equalTo(dto.getName())))
				.andExpect(jsonPath("$.description", equalTo(dto.getDescription())))
				.andExpect(jsonPath("$.price", equalTo(dto.getPrice().intValue())));
	}

	@Test
	@WithRoleAdmin
	public void updateEntityInvalid() throws Exception {
		final ProductRequest dto = new ProductRequest(null, null, null);

		final UUID id = this.products.values().stream().skip(this.products.size() / 2).findFirst().map(AbstractEntity::getId).get();
		final MockHttpServletRequestBuilder req = put("/products/{id}", id)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.messages", hasSize(3)));
	}

	@Test
	@WithRoleAdmin
	public void updateEntityNotFound() throws Exception {
		final ProductRequest dto = new ProductRequest("new name", "new description", BigDecimal.ONE);

		final UUID id = UUID.randomUUID();
		final MockHttpServletRequestBuilder req = put("/products/{id}", id)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.messages", hasSize(1)));
	}

	@Test
	public void updateEntityAsAnonymous() throws Exception {
		final UUID id = UUID.randomUUID();
		final MockHttpServletRequestBuilder req = put("/products/{id}", id)
				.contentType(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithRoleUser
	public void updateEntityAsUser() throws Exception {
		final ProductRequest dto = new ProductRequest("new name", "new description", BigDecimal.ONE);

		final UUID id = this.products.values().stream().skip(this.products.size() / 2).findFirst().map(AbstractEntity::getId).get();
		final MockHttpServletRequestBuilder req = put("/products/{id}", id)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isForbidden());
	}

	@Test
	@WithRoleAdmin
	public void patchEntity() throws Exception {
		final Product ent = this.products.values().stream().skip(this.products.size() / 2).findFirst().get();
		final ProductRequest dto = new ProductRequest(null, "new description", null);

		final MockHttpServletRequestBuilder req = patch("/products/{id}", ent.getId())
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.id", equalTo(ent.getId().toString())))
				.andExpect(jsonPath("$.name", equalTo(ent.getName())))
				.andExpect(jsonPath("$.description", equalTo(dto.getDescription())))
				.andExpect(jsonPath("$.price", equalTo(ent.getPrice().doubleValue())));
	}

	@Test
	@WithRoleAdmin
	public void patchEntityEmpty() throws Exception {
		final ProductRequest dto = new ProductRequest(null, null, null);

		final UUID id = this.products.values().stream().skip(this.products.size() / 2).findFirst().map(AbstractEntity::getId).get();
		final MockHttpServletRequestBuilder req = patch("/products/{id}", id)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.messages", hasSize(1)));
	}

	@Test
	@WithRoleAdmin
	public void patchEntityNotFound() throws Exception {
		final ProductRequest dto = new ProductRequest("new name", "new description", BigDecimal.ONE);

		final UUID id = UUID.randomUUID();
		final MockHttpServletRequestBuilder req = patch("/products/{id}", id)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.messages", hasSize(1)));
	}

	@Test
	public void patchEntityAsAnonymous() throws Exception {
		final UUID id = UUID.randomUUID();
		final MockHttpServletRequestBuilder req = patch("/products/{id}", id)
				.contentType(APPLICATION_JSON);

		this.mvc.perform(req)
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithRoleUser
	public void patchEntityAsUser() throws Exception {
		final ProductRequest dto = new ProductRequest("new name", "new description", BigDecimal.ONE);

		final UUID id = this.products.values().stream().skip(this.products.size() / 2).findFirst().map(AbstractEntity::getId).get();
		final MockHttpServletRequestBuilder req = patch("/products/{id}", id)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(this.om.writeValueAsString(dto));

		this.mvc.perform(req)
				.andExpect(status().isForbidden());
	}
}
