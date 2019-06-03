package com.triippztech.repository;

import com.triippztech.domain.Issue;
import com.triippztech.domain.enumeration.Resolution;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Issue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    @Query("select issue from Issue issue where issue.user.login = ?#{principal.username}")
    List<Issue> findByUserIsCurrentUser();

    List<Issue> findAllByResolution(Resolution resolution);

}
