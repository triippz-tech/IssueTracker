import { Moment } from 'moment';
import { IIssue } from 'app/shared/model/issue.model';
import { IUser } from 'app/shared/model/user.model';

export interface IComment {
  id?: number;
  description?: any;
  date?: Moment;
  issue?: IIssue;
  user?: IUser;
}

export const defaultValue: Readonly<IComment> = {};
