package com.triippztech.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.triippztech.domain.enumeration.Priority;
import com.triippztech.domain.enumeration.Resolution;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.triippztech.domain.Issue} entity. This class is used
 * in {@link com.triippztech.web.rest.IssueResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /issues?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class IssueCriteria implements Serializable, Criteria {
    /**
     * Class for filtering Priority
     */
    public static class PriorityFilter extends Filter<Priority> {

        public PriorityFilter() {
        }

        public PriorityFilter(PriorityFilter filter) {
            super(filter);
        }

        @Override
        public PriorityFilter copy() {
            return new PriorityFilter(this);
        }

    }
    /**
     * Class for filtering Resolution
     */
    public static class ResolutionFilter extends Filter<Resolution> {

        public ResolutionFilter() {
        }

        public ResolutionFilter(ResolutionFilter filter) {
            super(filter);
        }

        @Override
        public ResolutionFilter copy() {
            return new ResolutionFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter number;

    private PriorityFilter priority;

    private ResolutionFilter resolution;

    private LongFilter reviewerId;

    private InstantFilter reportedDate;

    private LongFilter commentId;

    private LongFilter userId;

    public IssueCriteria(){
    }

    public IssueCriteria(IssueCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.number = other.number == null ? null : other.number.copy();
        this.priority = other.priority == null ? null : other.priority.copy();
        this.resolution = other.resolution == null ? null : other.resolution.copy();
        this.reviewerId = other.reviewerId == null ? null : other.reviewerId.copy();
        this.reportedDate = other.reportedDate == null ? null : other.reportedDate.copy();
        this.commentId = other.commentId == null ? null : other.commentId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public IssueCriteria copy() {
        return new IssueCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getNumber() {
        return number;
    }

    public void setNumber(IntegerFilter number) {
        this.number = number;
    }

    public PriorityFilter getPriority() {
        return priority;
    }

    public void setPriority(PriorityFilter priority) {
        this.priority = priority;
    }

    public ResolutionFilter getResolution() {
        return resolution;
    }

    public void setResolution(ResolutionFilter resolution) {
        this.resolution = resolution;
    }

    public LongFilter getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(LongFilter reviewerId) {
        this.reviewerId = reviewerId;
    }

    public InstantFilter getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(InstantFilter reportedDate) {
        this.reportedDate = reportedDate;
    }

    public LongFilter getCommentId() {
        return commentId;
    }

    public void setCommentId(LongFilter commentId) {
        this.commentId = commentId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IssueCriteria that = (IssueCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(number, that.number) &&
            Objects.equals(priority, that.priority) &&
            Objects.equals(resolution, that.resolution) &&
            Objects.equals(reviewerId, that.reviewerId) &&
            Objects.equals(reportedDate, that.reportedDate) &&
            Objects.equals(commentId, that.commentId) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        number,
        priority,
        resolution,
        reviewerId,
        reportedDate,
        commentId,
        userId
        );
    }

    @Override
    public String toString() {
        return "IssueCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (number != null ? "number=" + number + ", " : "") +
                (priority != null ? "priority=" + priority + ", " : "") +
                (resolution != null ? "resolution=" + resolution + ", " : "") +
                (reviewerId != null ? "reviewerId=" + reviewerId + ", " : "") +
                (reportedDate != null ? "reportedDate=" + reportedDate + ", " : "") +
                (commentId != null ? "commentId=" + commentId + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
