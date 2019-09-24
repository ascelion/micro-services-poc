package ascelion.micro.products;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import ascelion.micro.model.AbstractEntity;
import ascelion.micro.tests.ResourceServerTestConfiguration;
import ascelion.micro.tests.WithAdminsRole;
import ascelion.micro.tests.WithUsersRole;
import ascelion.micro.utils.Mappings;

import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
@Import(ResourceServerTestConfiguration.class)
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

	private final Map<UUID, Product> products = Products.generate(7);

	@Before
	public void setUp() {
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
						final Product newP = Products.generateOne(0);

						Mappings.copyProperties(p, newP, false);

						this.products.put(newP.getId(), newP);

						return newP;
					} else {
						this.products.put(p.getId(), p);

						return p;
					}
				});
	}

	@Test
	@WithUsersRole
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
	@WithUsersRole
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
	@WithUsersRole
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
	@WithUsersRole
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
	@WithAdminsRole
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
	@WithAdminsRole
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
	@WithUsersRole
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
	@WithAdminsRole
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
	@WithAdminsRole
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
	@WithAdminsRole
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
	@WithUsersRole
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
	@WithAdminsRole
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
				.andExpect(jsonPath("$.price", equalTo(ent.getPrice().intValue())));
	}

	@Test
	@WithAdminsRole
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
	@WithAdminsRole
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
	@WithUsersRole
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
