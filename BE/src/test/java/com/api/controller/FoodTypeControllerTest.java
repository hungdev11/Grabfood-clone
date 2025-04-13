package com.api.controller;

import com.api.service.FoodTypeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class FoodTypeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FoodTypeService foodTypeService;


    private String request;
    private long response;

//    @BeforeEach
//    public void init() {
//        request = "Rice";
//        response = 1L;
//    }

    @Test
    // comment
    void addNewFoodType_validRequest_success() throws Exception {
        //given
//        ObjectMapper mapper = new ObjectMapper();
//        String content = mapper.writeValueAsString(request);
        Mockito.when(foodTypeService.addNewFoodType(ArgumentMatchers.any()))
                .thenReturn(response);
        //when - then
//        mvc.perform(MockMvcRequestBuilders
//                .post("/food-types")
//                .content(content)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("code")
//                        .value("200")
//        );
//        mvc.perform(MockMvcRequestBuilders
//                        .post("/grab/food-types")
//                        .param("name", "Rau")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("code").value(200));
        mvc.perform(post("/grab/food-types")
                        .param("name", "Pizza"))
                .andExpect(status().isOk());
    }
}
