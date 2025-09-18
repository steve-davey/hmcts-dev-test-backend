package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.dev.models.ExampleCase;
import uk.gov.hmcts.reform.dev.models.PagedResponse;
import uk.gov.hmcts.reform.dev.service.CaseService;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.models.CaseStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class CaseController {

    @Autowired private CaseService caseService;

    @GetMapping(value = "/get-example-case", produces = "application/json")
    public ResponseEntity<ExampleCase> getExampleCase() {
        return ok(new ExampleCase(1, "ABC12345", "Case Title",
                "Case Description", "Case Status", LocalDateTime.now()));
    }

    @GetMapping(value = "/cases")
    public ResponseEntity<PagedResponse<Case>> getCases(@RequestParam(defaultValue = "0") int page) {
        return caseService.fetchCaseList();
    }

    @GetMapping(value = "/cases/{id}")
    public ResponseEntity<Case> getCase(@PathVariable("id") String id) {
        Case myCase = caseService.getCaseById(id);
        return myCase != null ? ResponseEntity.ok(myCase) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/cases")
    public ResponseEntity<Case> createCase(@RequestBody Case myCase) {
    Instant instant = Instant.now();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    myCase.setCreatedDate(localDateTime);
    Case createdCase = caseService.createCase(myCase);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCase);
	}

    @PutMapping(value = "/cases/{id}")
    public ResponseEntity<Case> updateCase(@PathVariable("id") String id, @RequestBody Case myCase) { 
    Case updatedCase = caseService.updateCase(myCase, id);
    return ResponseEntity.ok(updatedCase);
    }

    @DeleteMapping(value = "/cases/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable("id") String id) {
        caseService.deleteCaseById(id);
        return ResponseEntity.noContent().build();
    }

        // Additional endpoint to get available case statuses
    @GetMapping(value = "/case-statuses")
    public ResponseEntity<List<CaseStatus>> getCaseStatuses() {
        return ResponseEntity.ok(Arrays.asList(CaseStatus.values()));
    }

    // Additional endpoint to get cases by status
    @GetMapping(value = "/cases/status/{status}")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable("status") CaseStatus status) {
        // You'll need to implement this in your service layer
        return ResponseEntity.ok().build(); // Placeholder
    }
}
