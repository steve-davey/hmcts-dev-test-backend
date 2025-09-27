package uk.gov.hmcts.reform.dev.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// OpenAPI/Swagger imports
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Case entity representing a legal case in the system")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cases", indexes = {
                @Index(name = "idx_case_number", columnList = "caseNumber"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_created_date", columnList = "createdDate")
})
public class Case {

        @Schema(description = "Unique identifier for the case", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Schema(description = "Unique case number identifier", example = "ABC12345", required = true, maxLength = 50)
        @Column(name = "case_number", unique = true, nullable = false)
        @NotBlank(message = "Case number is required")
        @Size(min = 3, max = 20, message = "Case number must be between 3 and 20 characters")
        @Pattern(regexp = "^[A-Z0-9]+$", message = "Case number must contain only uppercase letters and numbers")
        private String caseNumber;

        @Schema(description = "Title or name of the case", example = "Contract Dispute Resolution", required = true, maxLength = 255)
        @Column(nullable = false)
        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
        private String title;

        @Schema(description = "Detailed description of the case", example = "This case involves a contract dispute between two parties regarding the terms of service delivery.", maxLength = 2000)
        @Column(columnDefinition = "TEXT")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        @Schema(description = "Current status of the case", example = "OPEN", required = true, allowableValues = {
                        "OPEN", "IN_PROGRESS", "CLOSED", "CANCELLED" })
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private CaseStatus status;

        @Schema(description = "Date and time when the case is due for completion", example = "2024-12-31T17:00:00", required = true)
        @Column(name = "due_date", nullable = false)
        @NotNull(message = "Due date is required")
        @Future(message = "Due date must be in the future")
        private LocalDateTime dueDate;

        @Schema(description = "Date and time when the case was created", example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
        @Column(name = "created_date", nullable = false, updatable = false)
        private LocalDateTime createdDate;

        @Schema(description = "Date and time when the case was last updated", example = "2024-01-16T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
        @Column(name = "updated_date")
        private LocalDateTime updatedDate;

        @PrePersist
        protected void onCreate() {
                createdDate = LocalDateTime.now();
                updatedDate = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
                updatedDate = LocalDateTime.now();
        }
}