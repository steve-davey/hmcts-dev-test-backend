package uk.gov.hmcts.reform.dev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.models.CaseStatus;
import uk.gov.hmcts.reform.dev.models.PagedResponse;
import uk.gov.hmcts.reform.dev.repository.CaseRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaseServiceTest {
    
    @Mock
    private CaseRepository caseRepository;

    @InjectMocks
    private CaseServiceImpl caseService;
    
    private Case testCase;
    
    @BeforeEach
    void setUp() {
        testCase = Case.builder()
            .id(1)
            .caseNumber("CASE-001")
            .title("Test Case")
            .description("Test Description")
            .status(CaseStatus.OPEN)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();
    }
    
    @Test
    void shouldCreateCaseSuccessfully() {
        // Given
        when(caseRepository.save(any(Case.class))).thenReturn(testCase);
        
        // When
        Case result = caseService.createCase(testCase);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCaseNumber()).isEqualTo("CASE-001");
        assertThat(result.getTitle()).isEqualTo("Test Case");
        assertThat(result.getStatus()).isEqualTo(CaseStatus.OPEN);
        
        verify(caseRepository, times(1)).save(testCase);
    }
    
    @Test
    void shouldGetCaseByIdSuccessfully() {
        // Given
        when(caseRepository.findById(1)).thenReturn(Optional.of(testCase));
        
        // When
        Case result = caseService.getCaseById("1");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCaseNumber()).isEqualTo("CASE-001");
        
        verify(caseRepository, times(1)).findById(1);
    }
    
    @Test
    void shouldReturnNullWhenCaseNotFound() {
        // Given
        when(caseRepository.findById(999)).thenReturn(Optional.empty());
        
        // When
        Case result = caseService.getCaseById("999");
        
        // Then
        assertThat(result).isNull();
        
        verify(caseRepository, times(1)).findById(999);
    }
    
    @Test
    void shouldFetchCaseListSuccessfully() {
        // Given
        List<Case> caseList = Arrays.asList(testCase, 
            Case.builder()
                .id(2)
                .caseNumber("CASE-002")
                .title("Test Case 2")
                .status(CaseStatus.IN_PROGRESS)
                .build());
        
        when(caseRepository.findAll()).thenReturn(caseList);
        
        // When
        ResponseEntity<PagedResponse<Case>> result = caseService.fetchCaseList();
        
        // Then
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).hasSize(2);
        assertThat(result.getBody().getTotalElements()).isEqualTo(2);
        
        verify(caseRepository, times(1)).findAll();
    }
    
    @Test
    void shouldUpdateCaseSuccessfully() {
        // Given
        Case updatedCaseData = Case.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(CaseStatus.IN_PROGRESS)
            .build();
        
        when(caseRepository.findById(1)).thenReturn(Optional.of(testCase));
        when(caseRepository.save(any(Case.class))).thenReturn(testCase);
        
        // When
        Case result = caseService.updateCase(updatedCaseData, "1");
        
        // Then
        assertThat(result).isNotNull();
        verify(caseRepository, times(1)).findById(1);
        verify(caseRepository, times(1)).save(testCase);
    }
    
    @Test
    void shouldReturnNullWhenUpdatingNonExistentCase() {
        // Given
        Case updatedCaseData = Case.builder()
            .title("Updated Title")
            .build();
        
        when(caseRepository.findById(999)).thenReturn(Optional.empty());
        
        // When
        Case result = caseService.updateCase(updatedCaseData, "999");
        
        // Then
        assertThat(result).isNull();
        verify(caseRepository, times(1)).findById(999);
        verify(caseRepository, never()).save(any(Case.class));
    }
    
    @Test
    void shouldDeleteCaseById() {
        // Given
        doNothing().when(caseRepository).deleteById(1);
        
        // When
        caseService.deleteCaseById("1");
        
        // Then
        verify(caseRepository, times(1)).deleteById(1);
    }
    
    @Test
    void shouldGetCasesByStatus() {
        // Given
        List<Case> openCases = Arrays.asList(testCase);
        when(caseRepository.findByStatus(CaseStatus.OPEN)).thenReturn(openCases);
        
        // When
        List<Case> result = caseService.getCasesByStatus(CaseStatus.OPEN);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(CaseStatus.OPEN);
        
        verify(caseRepository, times(1)).findByStatus(CaseStatus.OPEN);
    }
    
    @Test
    void shouldCountCasesByStatus() {
        // Given
        when(caseRepository.countByStatus(CaseStatus.OPEN)).thenReturn(5L);
        
        // When
        long result = caseService.countCasesByStatus(CaseStatus.OPEN);
        
        // Then
        assertThat(result).isEqualTo(5L);
        
        verify(caseRepository, times(1)).countByStatus(CaseStatus.OPEN);
    }
}