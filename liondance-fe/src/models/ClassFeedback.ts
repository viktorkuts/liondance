export interface ClassFeedbackRequestModel {
    classDate: string; 
    score: number;
    comment?: string;
  }
  
  export interface ClassFeedbackResponseModel {
    score: number;
    comment?: string;
  }
  