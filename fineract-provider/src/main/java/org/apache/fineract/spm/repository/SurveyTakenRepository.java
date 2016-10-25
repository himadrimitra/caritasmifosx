package org.apache.fineract.spm.repository;

import java.util.List;

import org.apache.fineract.spm.domain.SurveyTaken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyTakenRepository extends JpaRepository<SurveyTaken, Long> {

    List<SurveyTaken> findByEntityTypeAndEntityId(final Integer entityType, final Long entityId);
}