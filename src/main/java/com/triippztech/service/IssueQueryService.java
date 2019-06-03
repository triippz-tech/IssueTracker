package com.triippztech.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.triippztech.domain.Issue;
import com.triippztech.domain.*; // for static metamodels
import com.triippztech.repository.IssueRepository;
import com.triippztech.service.dto.IssueCriteria;

/**
 * Service for executing complex queries for {@link Issue} entities in the database.
 * The main input is a {@link IssueCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Issue} or a {@link Page} of {@link Issue} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IssueQueryService extends QueryService<Issue> {

    private final Logger log = LoggerFactory.getLogger(IssueQueryService.class);

    private final IssueRepository issueRepository;

    public IssueQueryService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    /**
     * Return a {@link List} of {@link Issue} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Issue> findByCriteria(IssueCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Issue> specification = createSpecification(criteria);
        return issueRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Issue} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Issue> findByCriteria(IssueCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Issue> specification = createSpecification(criteria);
        return issueRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IssueCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Issue> specification = createSpecification(criteria);
        return issueRepository.count(specification);
    }

    /**
     * Function to convert IssueCriteria to a {@link Specification}.
     */
    private Specification<Issue> createSpecification(IssueCriteria criteria) {
        Specification<Issue> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Issue_.id));
            }
            if (criteria.getNumber() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumber(), Issue_.number));
            }
            if (criteria.getPriority() != null) {
                specification = specification.and(buildSpecification(criteria.getPriority(), Issue_.priority));
            }
            if (criteria.getResolution() != null) {
                specification = specification.and(buildSpecification(criteria.getResolution(), Issue_.resolution));
            }
            if (criteria.getReviewerId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReviewerId(), Issue_.reviewerId));
            }
            if (criteria.getReportedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReportedDate(), Issue_.reportedDate));
            }
            if (criteria.getCommentId() != null) {
                specification = specification.and(buildSpecification(criteria.getCommentId(),
                    root -> root.join(Issue_.comments, JoinType.LEFT).get(Comment_.id)));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(Issue_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
