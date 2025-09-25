package uk.gov.hmcts.reform.dev.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available status values for a case")
public enum CaseStatus {
    
    @Schema(description = "Case is newly created and open for processing")
    OPEN("Open"),
    
    @Schema(description = "Case is currently being worked on")
    IN_PROGRESS("In Progress"),
    
    @Schema(description = "Case has been completed and closed")
    CLOSED("Closed"),
    
    @Schema(description = "Case has been cancelled and will not be processed further")
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    CaseStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}