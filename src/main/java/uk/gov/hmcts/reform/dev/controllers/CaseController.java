package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Case Management", description = "APIs for managing cases")
public class CaseController {

    @Autowired private CaseService caseService;

    @Operation(summary = "Get example case", 
               description = "Returns a sample case for testing purposes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Successful operation",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ExampleCase.class)))
    })

    @GetMapping(value = "/get-example-case", produces = "application/json")
    public ResponseEntity<ExampleCase> getExampleCase() {
        return ok(new ExampleCase(1, "ABC12345", "Case Title",
                "Case Description", "Case Status", LocalDateTime.now()));
    }

    @Operation(summary = "Get all cases", 
               description = "Retrieve all cases with optional pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "List of cases retrieved successfully",
                    content = @Content(mediaType = "application/json"))
    })

    @GetMapping(value = "/cases")
    public ResponseEntity<PagedResponse<Case>> getCases(
        @Parameter(description = "Page number for pagination", example = "0")
        @RequestParam(defaultValue = "0") int page) {
        return caseService.fetchCaseList();
    }

    @Operation(summary = "Get case by ID", 
               description = "Retrieve a specific case using its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Case found and returned successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Case.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Case not found",
                    content = @Content())
    })

    @GetMapping(value = "/cases/{id}")
    public ResponseEntity<Case> getCase(
        @Parameter(description = "Unique identifier of the case", required = true, example = "1")
        @PathVariable("id") String id) {
        Case myCase = caseService.getCaseById(id);
        return myCase != null ? ResponseEntity.ok(myCase) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new case", 
               description = "Create a new case in the system with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Case created successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Case.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input provided",
                    content = @Content())
    })

    @PostMapping(value = "/cases")
    public ResponseEntity<Case> createCase(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Case object that needs to be created", 
                required = true,
                content = @Content(schema = @Schema(implementation = Case.class)))
        @RequestBody Case myCase) {
    Instant instant = Instant.now();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    myCase.setCreatedDate(localDateTime);
    Case createdCase = caseService.createCase(myCase);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCase);
	}

    @Operation(summary = "Update an existing case", 
               description = "Update case details using the case ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Case updated successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Case.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Case not found",
                    content = @Content()),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input provided",
                    content = @Content())
    })

    @PutMapping(value = "/cases/{id}")
    public ResponseEntity<Case> updateCase(
        @Parameter(description = "Unique identifier of the case to update", required = true, example = "1")
        @PathVariable("id") String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated case object", 
                required = true,
                content = @Content(schema = @Schema(implementation = Case.class)))
        @RequestBody Case myCase) { 
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
        // Still need to implement this in service layer
        return ResponseEntity.ok().build(); // Placeholder
    }
}
