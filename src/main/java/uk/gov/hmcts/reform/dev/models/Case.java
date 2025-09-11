package uk.gov.hmcts.reform.dev.models;

import java.time.LocalDateTime;
import jakarta.persistence.*; // Use jakarta instead of javax
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cases")
public class Case {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String caseNumber;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdDate;
}