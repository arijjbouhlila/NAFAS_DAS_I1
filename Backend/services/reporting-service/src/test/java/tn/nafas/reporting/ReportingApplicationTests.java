package tn.nafas.reporting;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"nafas.upload-dir=target/test-uploads", "nafas.seed-demo-data=false"})
@AutoConfigureMockMvc
class ReportingApplicationTests {
    @Autowired MockMvc mvc;

    private static final String JSON = "{\"title\":\"Smoke\",\"description\":\"Fumée noire\",\"category\":\"AIR\","
            + "\"latitude\":33.88,\"longitude\":10.1,\"reporterName\":\"Ali\"}";

    private int createReport() throws Exception {
        String body = mvc.perform(post("/api/reports").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    @Test void helloWorks() throws Exception {
        mvc.perform(get("/hello")).andExpect(status().isOk())
           .andExpect(jsonPath("$.service").value("reporting-service"));
    }

    @Test void swaggerDocsAvailable() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
           .andExpect(jsonPath("$.info.title").value("NAFAS - Reporting Service API"));
    }

    @Test void homePageServed() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test void fullWorkflow() throws Exception {
        int id = createReport();
        mvc.perform(multipart("/api/reports/" + id + "/photo").file(new MockMultipartFile("file", "a.png", "image/png", new byte[]{1, 2})))
           .andExpect(status().isOk()).andExpect(jsonPath("$.photoUrl").value("/api/reports/" + id + "/photo"));
        mvc.perform(multipart("/api/reports/" + id + "/photo").file(new MockMultipartFile("file", "a.txt", "text/plain", new byte[]{1})))
           .andExpect(status().isBadRequest());

        mvc.perform(patch("/api/reports/" + id + "/validate").header("X-User-Role", "CITIZEN")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/reports/" + id + "/validate").header("X-User-Role", "ADMIN"))
           .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("VALIDATED"));
        mvc.perform(patch("/api/reports/" + id + "/reject").header("X-User-Role", "ADMIN"))
           .andExpect(status().isConflict());
    }

    @Test void filtersStatsAndDelete() throws Exception {
        int id = createReport();
        mvc.perform(get("/api/reports").param("status", "SUBMITTED").param("category", "AIR"))
           .andExpect(status().isOk()).andExpect(jsonPath("$[0].category").value("AIR"));
        mvc.perform(get("/api/reports/stats")).andExpect(status().isOk()).andExpect(jsonPath("$.TOTAL").exists());

        mvc.perform(delete("/api/reports/" + id)).andExpect(status().isForbidden());
        mvc.perform(delete("/api/reports/" + id).header("X-User-Role", "ADMIN")).andExpect(status().isNoContent());
        mvc.perform(get("/api/reports/" + id)).andExpect(status().isNotFound());
    }

    @Test void invalidReportRejected() throws Exception {
        mvc.perform(post("/api/reports").contentType(MediaType.APPLICATION_JSON)
           .content("{\"title\":\"\",\"description\":\"\",\"category\":\"AIR\",\"latitude\":0,\"longitude\":0}"))
           .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields.title").exists());
        mvc.perform(post("/api/reports").contentType(MediaType.APPLICATION_JSON)
           .content("{\"title\":\"x\",\"description\":\"y\",\"category\":\"NOPE\",\"latitude\":0,\"longitude\":0}"))
           .andExpect(status().isBadRequest());
    }
}
