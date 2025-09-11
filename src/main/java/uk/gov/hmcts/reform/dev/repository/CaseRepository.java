package uk.gov.hmcts.reform.dev.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.dev.models.Case;

@Repository
public interface CaseRepository extends CrudRepository<Case, Integer> {
    // CrudRepository<EntityType, IdType>
    // You can add custom query methods here if needed
}