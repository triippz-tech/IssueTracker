import { Moment } from 'moment';
import { IComment } from 'app/shared/model/comment.model';
import { IUser } from 'app/shared/model/user.model';

export const enum Priority {
  SHOWSTOPPER = 'SHOWSTOPPER',
  EMERGENCY = 'EMERGENCY',
  HIGH = 'HIGH',
  LOW = 'LOW',
  ROUTINE = 'ROUTINE',
  DEFER = 'DEFER'
}

export const enum Resolution {
  NEW = 'NEW',
  RELEASED = 'RELEASED',
  DUPLICATE = 'DUPLICATE',
  TESTED = 'TESTED',
  FIXED = 'FIXED',
  PARTIALLY_FIXED = 'PARTIALLY_FIXED',
  REVIEWED = 'REVIEWED',
  CANNOT_REPRODUCE = 'CANNOT_REPRODUCE'
}

export interface IIssue {
  id?: number;
  number?: number;
  description?: any;
  priority?: Priority;
  resolution?: Resolution;
  reviewerId?: number;
  reportedDate?: Moment;
  comments?: IComment[];
  user?: IUser;
}

export const defaultValue: Readonly<IIssue> = {};
