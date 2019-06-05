package com.triippztech.service;

import com.triippztech.domain.Issue;
import com.triippztech.domain.enumeration.Resolution;
import com.triippztech.repository.IssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Issue}.
 */
@Service
@Transactional
public class IssueService {

    private final Logger log = LoggerFactory.getLogger(IssueService.class);

    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    /**
     * Save a issue.
     *
     * @param issue the entity to save.
     * @return the persisted entity.
     */
    public Issue save(Issue issue) {
        log.debug("Request to save Issue : {}", issue);
        return issueRepository.save(issue);
    }

    /**
     * Get all the issues.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Issue> findAll() {
        log.debug("Request to get all Issues");
        return issueRepository.findAll();
    }


    /**
     * Get one issue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Issue> findOne(Long id) {
        log.debug("Request to get Issue : {}", id);
        return issueRepository.findById(id);
    }

    /**
     * Delete the issue by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Issue : {}", id);
        issueRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Issue> findAllOpenIssues()
    {
        log.debug("Request to find all open Issues");
        return issueRepository.findAllByResolution(Resolution.NEW);
    }

    @Transactional(readOnly = true)
    public List<Issue> findAllReviewedIssues()
    {
        log.debug("Request to find all reviewed Issues");
        return issueRepository.findAllByResolution(Resolution.REVIEWED);
    }

}
