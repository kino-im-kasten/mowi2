package kik.overview.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import kik.overview.data.OverviewMonth;
import kik.overview.management.OverviewManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class OverviewGetControllerTest {
    @Autowired
    MockMvc mvc;

    private OverviewManagement overviewManagement;

    @Autowired
    OverviewGetControllerTest(OverviewManagement overviewManagement) {
        this.overviewManagement = overviewManagement;
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void uMOverviewGetControllerGetOverviewP1() throws Exception {

        MvcResult response = mvc.perform(get("/overview")).andReturn();

        assertEquals(200, response.getResponse().getStatus());

        Object overviewList = response.getModelAndView().getModelMap().get("overviewList");

        assertTrue("The server response should be a list of overviews", overviewList instanceof ArrayList);

        for (Object o : ((ArrayList) overviewList)) {
            assertTrue("There should be only overviews in that list", o instanceof OverviewMonth);
        }

        assertEquals(this.overviewManagement.getOverviewList().size(), ((ArrayList) overviewList).size(),
                "The amount of elements should match the ones from the management");
    }
}
