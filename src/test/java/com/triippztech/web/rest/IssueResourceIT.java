package com.triippztech.web.rest;

import com.triippztech.IssueTrackerApp;
import com.triippztech.domain.Issue;
import com.triippztech.domain.Comment;
import com.triippztech.domain.User;
import com.triippztech.repository.IssueRepository;
import com.triippztech.service.IssueService;
import com.triippztech.web.rest.errors.ExceptionTranslator;
import com.triippztech.service.dto.IssueCriteria;
import com.triippztech.service.IssueQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.triippztech.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.triippztech.domain.enumeration.Priority;
import com.triippztech.domain.enumeration.Resolution;
/**
 * Integration tests for the {@Link IssueResource} REST controller.
 */
@SpringBootTest(classes = IssueTrackerApp.class)
public class IssueResourceIT {

    private static final Integer DEFAULT_NUMBER = 1;
    private static final Integer UPDATED_NUMBER = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Priority DEFAULT_PRIORITY = Priority.SHOWSTOPPER;
    private static final Priority UPDATED_PRIORITY = Priority.EMERGENCY;

    private static final Resolution DEFAULT_RESOLUTION = Resolution.NEW;
    private static final Resolution UPDATED_RESOLUTION = Resolution.RELEASED;

    private static final Long DEFAULT_REVIEWER_ID = 1L;
    private static final Long UPDATED_REVIEWER_ID = 2L;

    private static final Instant DEFAULT_REPORTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REPORTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueService issueService;

    @Autowired
    private IssueQueryService issueQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restIssueMockMvc;

    private Issue issue;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IssueResource issueResource = new IssueResource(issueService, issueQueryService);
        this.restIssueMockMvc = MockMvcBuilders.standaloneSetup(issueResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createEntity(EntityManager em) {
        Issue issue = new Issue()
            .number(DEFAULT_NUMBER)
            .description(DEFAULT_DESCRIPTION)
            .priority(DEFAULT_PRIORITY)
            .resolution(DEFAULT_RESOLUTION)
            .reviewerId(DEFAULT_REVIEWER_ID)
            .reportedDate(DEFAULT_REPORTED_DATE);
        return issue;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createUpdatedEntity(EntityManager em) {
        Issue issue = new Issue()
            .number(UPDATED_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .resolution(UPDATED_RESOLUTION)
            .reviewerId(UPDATED_REVIEWER_ID)
            .reportedDate(UPDATED_REPORTED_DATE);
        return issue;
    }

    @BeforeEach
    public void initTest() {
        issue = createEntity(em);
    }

    @Test
    @Transactional
    public void createIssue() throws Exception {
        int databaseSizeBeforeCreate = issueRepository.findAll().size();

        // Create the Issue
        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isCreated());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate + 1);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testIssue.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testIssue.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testIssue.getResolution()).isEqualTo(DEFAULT_RESOLUTION);
        assertThat(testIssue.getReviewerId()).isEqualTo(DEFAULT_REVIEWER_ID);
        assertThat(testIssue.getReportedDate()).isEqualTo(DEFAULT_REPORTED_DATE);
    }

    @Test
    @Transactional
    public void createIssueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = issueRepository.findAll().size();

        // Create the Issue with an existing ID
        issue.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().size();
        // set the field null
        issue.setNumber(null);

        // Create the Issue, which fails.

        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllIssues() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList
        restIssueMockMvc.perform(get("/api/issues?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(issue.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].resolution").value(hasItem(DEFAULT_RESOLUTION.toString())))
            .andExpect(jsonPath("$.[*].reviewerId").value(hasItem(DEFAULT_REVIEWER_ID.intValue())))
            .andExpect(jsonPath("$.[*].reportedDate").value(hasItem(DEFAULT_REPORTED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getIssue() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get the issue
        restIssueMockMvc.perform(get("/api/issues/{id}", issue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(issue.getId().intValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()))
            .andExpect(jsonPath("$.resolution").value(DEFAULT_RESOLUTION.toString()))
            .andExpect(jsonPath("$.reviewerId").value(DEFAULT_REVIEWER_ID.intValue()))
            .andExpect(jsonPath("$.reportedDate").value(DEFAULT_REPORTED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllIssuesByNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where number equals to DEFAULT_NUMBER
        defaultIssueShouldBeFound("number.equals=" + DEFAULT_NUMBER);

        // Get all the issueList where number equals to UPDATED_NUMBER
        defaultIssueShouldNotBeFound("number.equals=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    public void getAllIssuesByNumberIsInShouldWork() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where number in DEFAULT_NUMBER or UPDATED_NUMBER
        defaultIssueShouldBeFound("number.in=" + DEFAULT_NUMBER + "," + UPDATED_NUMBER);

        // Get all the issueList where number equals to UPDATED_NUMBER
        defaultIssueShouldNotBeFound("number.in=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    public void getAllIssuesByNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where number is not null
        defaultIssueShouldBeFound("number.specified=true");

        // Get all the issueList where number is null
        defaultIssueShouldNotBeFound("number.specified=false");
    }

    @Test
    @Transactional
    public void getAllIssuesByNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where number greater than or equals to DEFAULT_NUMBER
        defaultIssueShouldBeFound("number.greaterOrEqualThan=" + DEFAULT_NUMBER);

        // Get all the issueList where number greater than or equals to UPDATED_NUMBER
        defaultIssueShouldNotBeFound("number.greaterOrEqualThan=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    public void getAllIssuesByNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where number less than or equals to DEFAULT_NUMBER
        defaultIssueShouldNotBeFound("number.lessThan=" + DEFAULT_NUMBER);

        // Get all the issueList where number less than or equals to UPDATED_NUMBER
        defaultIssueShouldBeFound("number.lessThan=" + UPDATED_NUMBER);
    }


    @Test
    @Transactional
    public void getAllIssuesByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where priority equals to DEFAULT_PRIORITY
        defaultIssueShouldBeFound("priority.equals=" + DEFAULT_PRIORITY);

        // Get all the issueList where priority equals to UPDATED_PRIORITY
        defaultIssueShouldNotBeFound("priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllIssuesByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where priority in DEFAULT_PRIORITY or UPDATED_PRIORITY
        defaultIssueShouldBeFound("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY);

        // Get all the issueList where priority equals to UPDATED_PRIORITY
        defaultIssueShouldNotBeFound("priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllIssuesByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where priority is not null
        defaultIssueShouldBeFound("priority.specified=true");

        // Get all the issueList where priority is null
        defaultIssueShouldNotBeFound("priority.specified=false");
    }

    @Test
    @Transactional
    public void getAllIssuesByResolutionIsEqualToSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where resolution equals to DEFAULT_RESOLUTION
        defaultIssueShouldBeFound("resolution.equals=" + DEFAULT_RESOLUTION);

        // Get all the issueList where resolution equals to UPDATED_RESOLUTION
        defaultIssueShouldNotBeFound("resolution.equals=" + UPDATED_RESOLUTION);
    }

    @Test
    @Transactional
    public void getAllIssuesByResolutionIsInShouldWork() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where resolution in DEFAULT_RESOLUTION or UPDATED_RESOLUTION
        defaultIssueShouldBeFound("resolution.in=" + DEFAULT_RESOLUTION + "," + UPDATED_RESOLUTION);

        // Get all the issueList where resolution equals to UPDATED_RESOLUTION
        defaultIssueShouldNotBeFound("resolution.in=" + UPDATED_RESOLUTION);
    }

    @Test
    @Transactional
    public void getAllIssuesByResolutionIsNullOrNotNull() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where resolution is not null
        defaultIssueShouldBeFound("resolution.specified=true");

        // Get all the issueList where resolution is null
        defaultIssueShouldNotBeFound("resolution.specified=false");
    }

    @Test
    @Transactional
    public void getAllIssuesByReviewerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reviewerId equals to DEFAULT_REVIEWER_ID
        defaultIssueShouldBeFound("reviewerId.equals=" + DEFAULT_REVIEWER_ID);

        // Get all the issueList where reviewerId equals to UPDATED_REVIEWER_ID
        defaultIssueShouldNotBeFound("reviewerId.equals=" + UPDATED_REVIEWER_ID);
    }

    @Test
    @Transactional
    public void getAllIssuesByReviewerIdIsInShouldWork() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reviewerId in DEFAULT_REVIEWER_ID or UPDATED_REVIEWER_ID
        defaultIssueShouldBeFound("reviewerId.in=" + DEFAULT_REVIEWER_ID + "," + UPDATED_REVIEWER_ID);

        // Get all the issueList where reviewerId equals to UPDATED_REVIEWER_ID
        defaultIssueShouldNotBeFound("reviewerId.in=" + UPDATED_REVIEWER_ID);
    }

    @Test
    @Transactional
    public void getAllIssuesByReviewerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reviewerId is not null
        defaultIssueShouldBeFound("reviewerId.specified=true");

        // Get all the issueList where reviewerId is null
        defaultIssueShouldNotBeFound("reviewerId.specified=false");
    }

    @Test
    @Transactional
    public void getAllIssuesByReviewerIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reviewerId greater than or equals to DEFAULT_REVIEWER_ID
        defaultIssueShouldBeFound("reviewerId.greaterOrEqualThan=" + DEFAULT_REVIEWER_ID);

        // Get all the issueList where reviewerId greater than or equals to UPDATED_REVIEWER_ID
        defaultIssueShouldNotBeFound("reviewerId.greaterOrEqualThan=" + UPDATED_REVIEWER_ID);
    }

    @Test
    @Transactional
    public void getAllIssuesByReviewerIdIsLessThanSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reviewerId less than or equals to DEFAULT_REVIEWER_ID
        defaultIssueShouldNotBeFound("reviewerId.lessThan=" + DEFAULT_REVIEWER_ID);

        // Get all the issueList where reviewerId less than or equals to UPDATED_REVIEWER_ID
        defaultIssueShouldBeFound("reviewerId.lessThan=" + UPDATED_REVIEWER_ID);
    }


    @Test
    @Transactional
    public void getAllIssuesByReportedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reportedDate equals to DEFAULT_REPORTED_DATE
        defaultIssueShouldBeFound("reportedDate.equals=" + DEFAULT_REPORTED_DATE);

        // Get all the issueList where reportedDate equals to UPDATED_REPORTED_DATE
        defaultIssueShouldNotBeFound("reportedDate.equals=" + UPDATED_REPORTED_DATE);
    }

    @Test
    @Transactional
    public void getAllIssuesByReportedDateIsInShouldWork() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reportedDate in DEFAULT_REPORTED_DATE or UPDATED_REPORTED_DATE
        defaultIssueShouldBeFound("reportedDate.in=" + DEFAULT_REPORTED_DATE + "," + UPDATED_REPORTED_DATE);

        // Get all the issueList where reportedDate equals to UPDATED_REPORTED_DATE
        defaultIssueShouldNotBeFound("reportedDate.in=" + UPDATED_REPORTED_DATE);
    }

    @Test
    @Transactional
    public void getAllIssuesByReportedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList where reportedDate is not null
        defaultIssueShouldBeFound("reportedDate.specified=true");

        // Get all the issueList where reportedDate is null
        defaultIssueShouldNotBeFound("reportedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllIssuesByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        Comment comment = CommentResourceIT.createEntity(em);
        em.persist(comment);
        em.flush();
        issue.addComment(comment);
        issueRepository.saveAndFlush(issue);
        Long commentId = comment.getId();

        // Get all the issueList where comment equals to commentId
        defaultIssueShouldBeFound("commentId.equals=" + commentId);

        // Get all the issueList where comment equals to commentId + 1
        defaultIssueShouldNotBeFound("commentId.equals=" + (commentId + 1));
    }


    @Test
    @Transactional
    public void getAllIssuesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        issue.setUser(user);
        issueRepository.saveAndFlush(issue);
        Long userId = user.getId();

        // Get all the issueList where user equals to userId
        defaultIssueShouldBeFound("userId.equals=" + userId);

        // Get all the issueList where user equals to userId + 1
        defaultIssueShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIssueShouldBeFound(String filter) throws Exception {
        restIssueMockMvc.perform(get("/api/issues?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(issue.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].resolution").value(hasItem(DEFAULT_RESOLUTION.toString())))
            .andExpect(jsonPath("$.[*].reviewerId").value(hasItem(DEFAULT_REVIEWER_ID.intValue())))
            .andExpect(jsonPath("$.[*].reportedDate").value(hasItem(DEFAULT_REPORTED_DATE.toString())));

        // Check, that the count call also returns 1
        restIssueMockMvc.perform(get("/api/issues/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIssueShouldNotBeFound(String filter) throws Exception {
        restIssueMockMvc.perform(get("/api/issues?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIssueMockMvc.perform(get("/api/issues/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingIssue() throws Exception {
        // Get the issue
        restIssueMockMvc.perform(get("/api/issues/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIssue() throws Exception {
        // Initialize the database
        issueService.save(issue);

        int databaseSizeBeforeUpdate = issueRepository.findAll().size();

        // Update the issue
        Issue updatedIssue = issueRepository.findById(issue.getId()).get();
        // Disconnect from session so that the updates on updatedIssue are not directly saved in db
        em.detach(updatedIssue);
        updatedIssue
            .number(UPDATED_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .resolution(UPDATED_RESOLUTION)
            .reviewerId(UPDATED_REVIEWER_ID)
            .reportedDate(UPDATED_REPORTED_DATE);

        restIssueMockMvc.perform(put("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedIssue)))
            .andExpect(status().isOk());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testIssue.getResolution()).isEqualTo(UPDATED_RESOLUTION);
        assertThat(testIssue.getReviewerId()).isEqualTo(UPDATED_REVIEWER_ID);
        assertThat(testIssue.getReportedDate()).isEqualTo(UPDATED_REPORTED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();

        // Create the Issue

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIssueMockMvc.perform(put("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteIssue() throws Exception {
        // Initialize the database
        issueService.save(issue);

        int databaseSizeBeforeDelete = issueRepository.findAll().size();

        // Delete the issue
        restIssueMockMvc.perform(delete("/api/issues/{id}", issue.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Issue.class);
        Issue issue1 = new Issue();
        issue1.setId(1L);
        Issue issue2 = new Issue();
        issue2.setId(issue1.getId());
        assertThat(issue1).isEqualTo(issue2);
        issue2.setId(2L);
        assertThat(issue1).isNotEqualTo(issue2);
        issue1.setId(null);
        assertThat(issue1).isNotEqualTo(issue2);
    }
}
