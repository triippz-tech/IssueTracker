import { TextFormat } from 'react-jhipster';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import React from 'react';
import { Card, CardBody, CardFooter, Container, CardTitle, Row, Col } from 'reactstrap';

export const IssueTR = ({ issue }) => (
  <React.Fragment>
    <tr>
      <td>{issue.number}</td>
      <td>
        <TextFormat value={issue.reportedDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
      </td>
      <td>{issue.priority}</td>
      <td>{issue.resolution}</td>
      <td>{issue.description}</td>
    </tr>
  </React.Fragment>
);
