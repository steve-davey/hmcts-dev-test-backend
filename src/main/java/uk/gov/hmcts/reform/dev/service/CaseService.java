package uk.gov.hmcts.reform.dev.service;

import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.models.PagedResponse;

import org.springframework.http.ResponseEntity;

// Class
public interface CaseService {
    Case createCase(Case myCase);
    Case getCaseById(String caseId);
    ResponseEntity<PagedResponse<Case>> fetchCaseList();
    Case updateCase(Case myCase, String caseId);
    void deleteCaseById(String caseId);
}