package com.koporate.wing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WingApplicationTests
{

	/**
	 * COMANDO PARA EXECUTAR OS TESTES GERANDO RELATORIO DE COVERAGE
	 * mvn test jacoco:report -Dspring.profiles.active=test
	 */

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

	@Autowired
	protected MockMvc mockMvc;

	@Value("${api.path}")
	protected String baseURL;

	protected static final String authToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjMzMzM0MTUsInVzZXJJRCI6LTk5LCJ0ZW5hbnQiOiJwdWJsaWMiLCJ2ZXJzaW9uIjowfQ.P3Y4gKb3BO67dqOA-J3MuzcFYpsK0gODnz3AullGVTQ";

	@Test
	public void contextLoads() {
	}

//	protected String getAuthorizedUserToken() throws Exception {
//		HttpHeaders headerss = new HttpHeaders();
//		headerss.add("X-Emissor", "public");
//
//		Map<String, String> loginParams = new HashMap<>();
//		loginParams.put("login", "07722963404");
//		loginParams.put("password", "CDC@k4m4");
//
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//		String requestJson = ow.writeValueAsString(loginParams);
//
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(baseURL + "/kamaleon/login")
//				.contentType(APPLICATION_JSON_UTF8)
//				.headers(headerss)
//				.content(requestJson)
//		).andReturn();
//
//		Map<String, String> responseBody = mapper.readValue(result.getResponse().getContentAsString(), Map.class);
//
//		return responseBody.get("token");
//	}

}
