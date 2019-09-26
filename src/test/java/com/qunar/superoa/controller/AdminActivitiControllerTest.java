package com.qunar.superoa.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.*;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/17_上午12:00
 * @Despriction:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminActivitiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deploy() {
    }

    @Test
    public void processList() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/admin/processList"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateState() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void readResource() {
    }

    @Test
    public void findProcessInstanceList() {
    }

    @Test
    public void loadByDeployment() {
    }
}