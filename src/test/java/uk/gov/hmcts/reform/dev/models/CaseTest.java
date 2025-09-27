package uk.gov.hmcts.reform.dev.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.controllers.CaseController;
import uk.gov.hmcts.reform.dev.exception.GlobalExceptionHandler;
import uk.gov.hmcts.reform.dev.repository.CaseRepository;
import uk.gov.hmcts.reform.dev.service.CaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = CaseController.class)
@Import({CaseTest.TestConfig.class, GlobalExceptionHandler.class})
public class CaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseRepository caseRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CaseService caseService() {
            return mock(CaseService.class);
        }

        @Bean
        public CaseRepository caseRepository() {
            return mock(CaseRepository.class);
        }
    }

    @BeforeEach
    void setup() {
        when(caseService.createCase(any(Case.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate case number"));
    }

    @Test
    void createCaseWithInvalidData_ShouldReturnBadRequest() throws Exception {
        String invalidJson = """
                {
                    "caseNumber": "AB",
                    "title": "Hi",
                    "status": "INVALID_STATUS"
                }
                """;

        mockMvc.perform(post("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void createDuplicateCaseNumber_ShouldReturnConflict() throws Exception {
        Case case1 = Case.builder()
                .caseNumber("DUPLICATE123")
                .title("First Case")
                .status(CaseStatus.OPEN)
                .build();

        caseRepository.save(case1);

        Case case2 = Case.builder()
                .caseNumber("DUPLICATE123")
                .title("Second Case")
                .status(CaseStatus.OPEN)
                .build();

        mockMvc.perform(post("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(case2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Data Integrity Violation"));
    }
}
