export interface ClassFeedbackRequestModel {
    classDate: string; 
    score: number;
    comment?: string;
  }
  
  export interface ClassFeedbackResponseModel {
    score: number;
    comment?: string;
  }
  
  export interface ClassFeedbackReportResponseModel {
    reportId: string;
    classDate: string; 
    averageScore: number;
    feedbackDetails: Array<{ score: number; comment: string }>;
  }