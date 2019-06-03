import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './issue.reducer';
import { IIssue } from 'app/shared/model/issue.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IIssueUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IIssueUpdateState {
  isNew: boolean;
  userId: string;
}

export class IssueUpdate extends React.Component<IIssueUpdateProps, IIssueUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      userId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getUsers();
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    values.reportedDate = convertDateTimeToServer(values.reportedDate);

    if (errors.length === 0) {
      const { issueEntity } = this.props;
      const entity = {
        ...issueEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/issue');
  };

  render() {
    const { issueEntity, users, loading, updating } = this.props;
    const { isNew } = this.state;

    const { description } = issueEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="issueTrackerApp.issue.home.createOrEditLabel">Create or edit a Issue</h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : issueEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="issue-id">ID</Label>
                    <AvInput id="issue-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="numberLabel" for="issue-number">
                    Number
                  </Label>
                  <AvField
                    id="issue-number"
                    type="string"
                    className="form-control"
                    name="number"
                    validate={{
                      required: { value: true, errorMessage: 'This field is required.' },
                      number: { value: true, errorMessage: 'This field should be a number.' }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="descriptionLabel" for="issue-description">
                    Description
                  </Label>
                  <AvInput id="issue-description" type="textarea" name="description" />
                </AvGroup>
                <AvGroup>
                  <Label id="priorityLabel" for="issue-priority">
                    Priority
                  </Label>
                  <AvInput
                    id="issue-priority"
                    type="select"
                    className="form-control"
                    name="priority"
                    value={(!isNew && issueEntity.priority) || 'SHOWSTOPPER'}
                  >
                    <option value="SHOWSTOPPER">SHOWSTOPPER</option>
                    <option value="EMERGENCY">EMERGENCY</option>
                    <option value="HIGH">HIGH</option>
                    <option value="LOW">LOW</option>
                    <option value="ROUTINE">ROUTINE</option>
                    <option value="DEFER">DEFER</option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="resolutionLabel" for="issue-resolution">
                    Resolution
                  </Label>
                  <AvInput
                    id="issue-resolution"
                    type="select"
                    className="form-control"
                    name="resolution"
                    value={(!isNew && issueEntity.resolution) || 'NEW'}
                  >
                    <option value="NEW">NEW</option>
                    <option value="RELEASED">RELEASED</option>
                    <option value="DUPLICATE">DUPLICATE</option>
                    <option value="TESTED">TESTED</option>
                    <option value="FIXED">FIXED</option>
                    <option value="PARTIALLY_FIXED">PARTIALLY_FIXED</option>
                    <option value="REVIEWED">REVIEWED</option>
                    <option value="CANNOT_REPRODUCE">CANNOT_REPRODUCE</option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="reviewerIdLabel" for="issue-reviewerId">
                    Reviewer Id
                  </Label>
                  <AvField id="issue-reviewerId" type="string" className="form-control" name="reviewerId" />
                </AvGroup>
                <AvGroup>
                  <Label id="reportedDateLabel" for="issue-reportedDate">
                    Reported Date
                  </Label>
                  <AvInput
                    id="issue-reportedDate"
                    type="datetime-local"
                    className="form-control"
                    name="reportedDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.issueEntity.reportedDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="issue-user">User</Label>
                  <AvInput id="issue-user" type="select" className="form-control" name="user.id">
                    <option value="" key="0" />
                    {users
                      ? users.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.login}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/issue" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">Back</span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp; Save
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  issueEntity: storeState.issue.entity,
  loading: storeState.issue.loading,
  updating: storeState.issue.updating,
  updateSuccess: storeState.issue.updateSuccess
});

const mapDispatchToProps = {
  getUsers,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(IssueUpdate);
