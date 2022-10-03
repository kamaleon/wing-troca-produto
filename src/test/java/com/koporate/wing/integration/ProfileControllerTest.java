package com.koporate.wing.integration;

import com.koporate.wing.WingApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ProfileControllerTest extends WingApplicationTests
{
    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    @Value("${api.path}")
    private String baseURL;

//    @After
//    public void tearDown() {
//        JdbcTestUtils.deleteFromTables(jdbcTemplate, "t_profile_role");
//    }

    @Test
    public void testGETListarProfiles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/user/profile").header("Authorization", authToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
}
