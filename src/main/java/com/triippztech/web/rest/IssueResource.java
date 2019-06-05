package com.triippztech.web.rest;

import com.triippztech.domain.Issue;
import com.triippztech.service.IssueService;
import com.triippztech.web.rest.errors.BadRequestAlertException;
import com.triippztech.service.dto.IssueCriteria;
import com.triippztech.service.IssueQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.triippztech.domain.Issue}.
 */
@RestController
@RequestMapping("/api")
public class IssueResource {

    private final Logger log = LoggerFactory.getLogger(IssueResource.class);

    private static final String ENTITY_NAME = "issue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IssueService issueService;

    private final IssueQueryService issueQueryService;

    public IssueResource(IssueService issueService, IssueQueryService issueQueryService) {
        this.issueService = issueService;
        this.issueQueryService = issueQueryService;
    }

    /**
     * {@code POST  /issues} : Create a new issue.
     *
     * @param issue the issue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new issue, or with status {@code 400 (Bad Request)} if the issue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/issues")
    public ResponseEntity<Issue> createIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        log.debug("REST request to save Issue : {}", issue);
        if (issue.getId() != null) {
            throw new BadRequestAlertException("A new issue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Issue result = issueService.save(issue);
        return ResponseEntity.created(new URI("/api/issues/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /issues} : Updates an existing issue.
     *
     * @param issue the issue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated issue,
     * or with status {@code 400 (Bad Request)} if the issue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the issue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/issues")
    public ResponseEntity<Issue> updateIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        log.debug("REST request to update Issue : {}", issue);
        if (issue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Issue result = issueService.save(issue);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, issue.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /issues} : get all the issues.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of issues in body.
     */
    @GetMapping("/issues")
    public ResponseEntity<List<Issue>> getAllIssues(IssueCriteria criteria) {
        log.debug("REST request to get Issues by criteria: {}", criteria);
        List<Issue> entityList = issueQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /openissues} : get all the open issues.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of open issues in body.
     */
    @GetMapping("/openissues")
    public ResponseEntity<List<Issue>> getOpenIssues() {
        log.debug("REST request to get all open Issues");
        List<Issue> entityList = issueService.findAllOpenIssues();
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /openissues} : get all the reviewed issues.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reviewed issues in body.
     */
    @GetMapping("/reviewedissues")
    public ResponseEntity<List<Issue>> getReviewedIssues() {
        log.debug("REST request to get all open Issues");
        List<Issue> entityList = issueService.findAllReviewedIssues();
        return ResponseEntity.ok().body(entityList);
    }

    /**
    * {@code GET  /issues/count} : count all the issues.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/issues/count")
    public ResponseEntity<Long> countIssues(IssueCriteria criteria) {
        log.debug("REST request to count Issues by criteria: {}", criteria);
        return ResponseEntity.ok().body(issueQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /issues/:id} : get the "id" issue.
     *
     * @param id the id of the issue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the issue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/issues/{id}")
    public ResponseEntity<Issue> getIssue(@PathVariable Long id) {
        log.debug("REST request to get Issue : {}", id);
        Optional<Issue> issue = issueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(issue);
    }

    /**
     * {@code DELETE  /issues/:id} : delete the "id" issue.
     *
     * @param id the id of the issue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/issues/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        log.debug("REST request to delete Issue : {}", id);
        issueService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
