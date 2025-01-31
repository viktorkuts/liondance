export enum CourseStatus {
    ACTIVE = "ACTIVE",
    CANCELLED = "CANCELLED",
}

export interface Course {
    courseId?: string;
    name: string;
    startTime: string;
    endTime: string;
    dayOfWeek: string;
    userIds: string[];
    instructorId: string;
    cancelledDates: string[];
    status: CourseStatus;
}