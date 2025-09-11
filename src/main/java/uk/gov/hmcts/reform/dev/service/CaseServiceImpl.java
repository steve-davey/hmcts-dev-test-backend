package uk.gov.hmcts.reform.dev.service;

import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.models.PagedResponse;
import uk.gov.hmcts.reform.dev.repository.CaseRepository;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// Annotation
@Service
// Class implementing CaseService class
public class CaseServiceImpl
        implements CaseService {

    @Autowired
    private CaseRepository myCaseRepository;

    // Save operation
    @Override
    public Case createCase(Case myCase) {
        return myCaseRepository.save(myCase);
    }

    // Read operation
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

    // Update operation
    @Override
    public Case updateCase(Case myCase, String caseId) {
        int id = Integer.parseInt(caseId);
        Case depDB = myCaseRepository.findById(id).orElse(null);
        if (depDB == null)
            return null;

        // Use the actual field names from your Case model:
        if (Objects.nonNull(myCase.getTitle()) && !"".equalsIgnoreCase(myCase.getTitle())) {
            depDB.setTitle(myCase.getTitle());
        }

        if (Objects.nonNull(myCase.getDescription()) && !"".equalsIgnoreCase(myCase.getDescription())) {
            depDB.setDescription(myCase.getDescription());
        }

        if (Objects.nonNull(myCase.getStatus()) && !"".equalsIgnoreCase(myCase.getStatus())) {
            depDB.setStatus(myCase.getStatus());
        }

        if (Objects.nonNull(myCase.getCaseNumber()) && !"".equalsIgnoreCase(myCase.getCaseNumber())) {
            depDB.setCaseNumber(myCase.getCaseNumber());
        }

        return myCaseRepository.save(depDB);
    }

    // Delete operation
    @Override
    public void deleteCaseById(String caseId) {
        int id = Integer.parseInt(caseId);
        myCaseRepository.deleteById(id);
    }
}