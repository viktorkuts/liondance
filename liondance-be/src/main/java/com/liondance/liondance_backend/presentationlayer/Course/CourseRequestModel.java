package com.liondance.liondance_backend.presentationlayer.Course;

import com.liondance.liondance_backend.datalayer.Course.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequestModel {
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private String instructorId;

    public static Course toEntity(CourseRequestModel courseRequestModel) {
        Course newCourse = new Course();
        BeanUtils.copyProperties(courseRequestModel, newCourse);
        return newCourse;
    }
}
