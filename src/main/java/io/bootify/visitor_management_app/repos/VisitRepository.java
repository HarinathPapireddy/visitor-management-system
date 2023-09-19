package io.bootify.visitor_management_app.repos;

import io.bootify.visitor_management_app.domain.Visit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VisitRepository extends JpaRepository<Visit, Long> {

}
