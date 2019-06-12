import React from 'react';
import { connect } from 'react-redux';
import { Button, Row, Col, CardGroup, Card, Container, CardDeck, CardBody, CardTitle } from 'reactstrap';

import { IRootState } from 'app/shared/reducers';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import { getOpenIssues, getReviewedIssues } from 'app/entities/issue/issue.reducer';
import { IssueTR } from 'app/components/IssueTR';

export interface IMonitorViewProps extends StateProps, DispatchProps {}

export interface IMonitorViewState {
  seconds: number;
}

export class MonitorView extends React.Component<IMonitorViewProps, IMonitorViewState> {
  private interval: number;
  constructor(props) {
    super(props);
    this.state = {
      seconds: 0
    };
  }
  componentDidMount() {
    this.props.getSession();
    this.props.getOpenIssues();
    this.props.getReviewedIssues();
    this.interval = window.setInterval(() => this.tick(), 10000);
  }
  componentWillUnmount() {
    clearInterval(this.interval);
  }
  tick() {
    this.setState(prevState => ({
      seconds: prevState.seconds + 1
    }));
    this.props.getOpenIssues();
    this.props.getReviewedIssues();
  }
  render() {
    const { openIssues, reviewedIssues } = this.props;
    return (
      <>
        <div className="container-fluid">
          <Row>
            <Col>
              <Row>
                <h5>
                  <strong>Open Issues</strong>
                </h5>
                <table className="table table-borderless table-sm">
                  <thead>
                    <tr>
                      <th>Issue #</th>
                      <th>Date</th>
                      <th>Priority</th>
                      <th>Resolution</th>
                      <th>Description</th>
                    </tr>
                  </thead>
                  <tbody>
                    {openIssues === undefined ? (
                      <Card className="card-stats mb-4 mb-xl-0">
                        <Container>
                          <h6>It looks like there are no open issues at this time!</h6>
                        </Container>
                      </Card>
                    ) : (
                      openIssues.map(issue => (
                        <React.Fragment key={issue.id}>
                          <IssueTR issue={issue} />
                        </React.Fragment>
                      ))
                    )}
                  </tbody>
                </table>
              </Row>
              <hr />
              <Row>
                <h5>
                  <strong>Reviewed Issues</strong>
                </h5>
                <table className="table table-borderless table-sm">
                  <thead>
                    <tr>
                      <th>Issue #</th>
                      <th>Date</th>
                      <th>Priority</th>
                      <th>Resolution</th>
                      <th>Description</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reviewedIssues === undefined ? (
                      <Card className="card-stats mb-4 mb-xl-0">
                        <Container>
                          <h6>It looks like there are no reviewed issues at this time!</h6>
                        </Container>
                      </Card>
                    ) : (
                      reviewedIssues.map(issue => (
                        <React.Fragment key={issue.id}>
                          <IssueTR issue={issue} />
                        </React.Fragment>
                      ))
                    )}
                  </tbody>
                </table>
              </Row>
            </Col>
          </Row>
        </div>
      </>
    );
  }
}

const mapStateToProps = ({ authentication, applicationProfile, issue }: IRootState) => ({
  isAuthenticated: authentication.isAuthenticated,
  isAdmin: hasAnyAuthority(authentication.account.authorities, [AUTHORITIES.ADMIN]),
  ribbonEnv: applicationProfile.ribbonEnv,
  isInProduction: applicationProfile.inProduction,
  isSwaggerEnabled: applicationProfile.isSwaggerEnabled,
  openIssues: issue.openIssues,
  reviewedIssues: issue.reviewedIssues
});

const mapDispatchToProps = {
  getSession,
  getProfile,
  getOpenIssues,
  getReviewedIssues
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MonitorView);
