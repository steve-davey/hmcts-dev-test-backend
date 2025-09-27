package uk.gov.hmcts.reform.dev.service;

import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.models.CaseStatus;
import uk.gov.hmcts.reform.dev.models.PagedResponse;
import uk.gov.hmcts.reform.dev.repository.CaseRepository;
import uk.gov.hmcts.reform.dev.exception.CaseNotFoundException;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service

public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseRepository myCaseRepository;

    @Override
    public Case createCase(Case myCase) {
        return myCaseRepository.save(myCase);
    }

    @Override
    public Case getCaseById(String caseId) {
        try {
            int id = Integer.parseInt(caseId);
            return myCaseRepository.findById(id)
                    .orElseThrow(() -> new CaseNotFoundException("Case with ID " + caseId + " not found"));
        } catch (NumberFormatException e) {
            throw new CaseNotFoundException("Invalid case ID format: " + caseId);
        }
    }

    @Override
    public ResponseEntity<PagedResponse<Case>> fetchCaseList() {
        List<Case> cases = (List<Case>) myCaseRepository.findAll();
        PagedResponse<Case> pagedResponse = new PagedResponse<>(cases, 0, cases.size(), cases.size());
        return ResponseEntity.ok(pagedResponse);
    }

    @Override
    public Case updateCase(Case myCase, String caseId) {
        try {
            int id = Integer.parseInt(caseId);
            Case existingCase = myCaseRepository.findById(id)
                    .orElseThrow(() -> new CaseNotFoundException("Case with ID " + caseId + " not found"));

            if (Objects.nonNull(myCase.getTitle()) && !myCase.getTitle().trim().isEmpty()) {
                existingCase.setTitle(myCase.getTitle());
            }

            if (Objects.nonNull(myCase.getDescription())) {
                existingCase.setDescription(myCase.getDescription());
            }

            if (Objects.nonNull(myCase.getStatus())) {
                existingCase.setStatus(myCase.getStatus());
            }

            if (Objects.nonNull(myCase.getCaseNumber()) && !myCase.getCaseNumber().trim().isEmpty()) {
                existingCase.setCaseNumber(myCase.getCaseNumber());
            }

            return myCaseRepository.save(existingCase);
        } catch (NumberFormatException e) {
            throw new CaseNotFoundException("Invalid case ID format: " + caseId);
        }
    }

    @Override
    public void deleteCaseById(String caseId) {
        try {
            int id = Integer.parseInt(caseId);
            myCaseRepository.deleteById(id);
        } catch (NumberFormatException e) {
            throw new CaseNotFoundException("Invalid case ID format: " + caseId);
        }
    }

    public List<Case> getCasesByStatus(CaseStatus status) {
        return myCaseRepository.findByStatus(status);
    }

    public long countCasesByStatus(CaseStatus status) {
        return myCaseRepository.countByStatus(status);
    }
}