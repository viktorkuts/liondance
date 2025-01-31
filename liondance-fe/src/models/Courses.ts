import { DayOfWeek } from "@mantine/dates";

export interface Course {
    courseId?: string;
    name: string;
    startTime: string;
    endTime: string;
    dayOfWeek: DayOfWeek; //look at
    userIds: string[];
    instructorId: string;
    cancelledDates: Date[];
}