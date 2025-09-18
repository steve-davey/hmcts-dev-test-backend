package uk.gov.hmcts.reform.dev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.models.CaseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Integer> {
    
    // Find by case number
    Optional<Case> findByCaseNumber(String caseNumber);
    
    // Find by status
    List<Case> findByStatus(CaseStatus status);
    Page<Case> findByStatus(CaseStatus status, Pageable pageable);
    
    // Find by title containing (case insensitive search)
    @Query("SELECT c FROM Case c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Case> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    // Find cases created between dates
    List<Case> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count cases by status
    long countByStatus(CaseStatus status);
    
    // Custom query to find recent cases
    @Query("SELECT c FROM Case c WHERE c.createdDate >= :date ORDER BY c.createdDate DESC")
    List<Case> findRecentCases(@Param("date") LocalDateTime date);
    
    // Find cases by multiple statuses
    List<Case> findByStatusIn(List<CaseStatus> statuses);
    
    // Search across multiple fields
    @Query("SELECT c FROM Case c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.caseNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Case> searchCases(@Param("searchTerm") String searchTerm, Pageable pageable);
}