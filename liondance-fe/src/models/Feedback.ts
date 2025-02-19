export enum Visibility {
  PUBLIC = "PUBLIC",
  PRIVATE = "PRIVATE",
  UNLISTED = "UNLISTED",
}

export interface Feedback {
  feedbackId?: string;
  timestamp: Date;
  feedback: string;
  rating: number;
  eventId: string;
  visibility: Visibility;
}
