package uk.gov.hmcts.reform.dev.service;

import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.models.CaseStatus;
import uk.gov.hmcts.reform.dev.models.PagedResponse;
import uk.gov.hmcts.reform.dev.repository.CaseRepository;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service

public class CaseServiceImpl
        implements CaseService {

    @Autowired
    private CaseRepository myCaseRepository;

    @Override
    public Case createCase(Case myCase) {
        return myCaseRepository.save(myCase);
    }

    @Override
    public Case getCaseById(String caseId) {
        int id = Integer.parseInt(caseId);
        return myCaseRepository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<PagedResponse<Case>> fetchCaseList() {
        List<Case> cases = (List<Case>) myCaseRepository.findAll();
        PagedResponse<Case> pagedResponse = new PagedResponse<>(cases, 0, cases.size(), cases.size());
    return ResponseEntity.ok(pagedResponse);
    }

    @Override
    public Case updateCase(Case myCase, String caseId) {
        int id = Integer.parseInt(caseId);
        Case depDB = myCaseRepository.findById(id).orElse(null);
        if (depDB == null)
            return null;

        if (Objects.nonNull(myCase.getTitle()) && !"".equalsIgnoreCase(myCase.getTitle())) {
            depDB.setTitle(myCase.getTitle());
        }

        if (Objects.nonNull(myCase.getDescription()) && !"".equalsIgnoreCase(myCase.getDescription())) {
            depDB.setDescription(myCase.getDescription());
        }

        if (Objects.nonNull(myCase.getStatus())) {
            depDB.setStatus(myCase.getStatus());
        }

        if (Objects.nonNull(myCase.getCaseNumber()) && !"".equalsIgnoreCase(myCase.getCaseNumber())) {
            depDB.setCaseNumber(myCase.getCaseNumber());
        }

        return myCaseRepository.save(depDB);
    }

    @Override
    public void deleteCaseById(String caseId) {
        int id = Integer.parseInt(caseId);
        myCaseRepository.deleteById(id);
    }

    public List<Case> getCasesByStatus(CaseStatus status) {
        return myCaseRepository.findByStatus(status);
    }

    public long countCasesByStatus(CaseStatus status) {
        return myCaseRepository.countByStatus(status);
    }
}