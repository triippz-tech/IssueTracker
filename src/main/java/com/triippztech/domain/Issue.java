package com.triippztech.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.triippztech.domain.enumeration.Priority;

import com.triippztech.domain.enumeration.Resolution;

/**
 * A Issue.
 */
@Entity
@Table(name = "issue")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Issue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jhi_number", nullable = false)
    private Integer number;

    @Lob
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution")
    private Resolution resolution;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "reported_date")
    private Instant reportedDate;

    @OneToMany(mappedBy = "issue")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("issues")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public Issue number(Integer number) {
        this.number = number;
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public Issue description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public Issue priority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public Issue resolution(Resolution resolution) {
        this.resolution = resolution;
        return this;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public Issue reviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
        return this;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Instant getReportedDate() {
        return reportedDate;
    }

    public Issue reportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
        return this;
    }

    public void setReportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Issue comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Issue addComment(Comment comment) {
        this.comments.add(comment);
        comment.setIssue(this);
        return this;
    }

    public Issue removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setIssue(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public Issue user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Issue)) {
            return false;
        }
        return id != null && id.equals(((Issue) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Issue{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", description='" + getDescription() + "'" +
            ", priority='" + getPriority() + "'" +
            ", resolution='" + getResolution() + "'" +
            ", reviewerId=" + getReviewerId() +
            ", reportedDate='" + getReportedDate() + "'" +
            "}";
    }
}
