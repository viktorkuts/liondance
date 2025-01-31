package com.liondance.liondance_backend.presentationlayer.Course;

import com.liondance.liondance_backend.datalayer.Course.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseModel {
    private String courseId;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private List<String> userIds;
    private String instructorId;
    private String instructorFirstName;
    private String instructorMiddleName;
    private String instructorLastName;
    private List<LocalDate> cancelledDates;


    public static CourseResponseModel from(Course course) {
        CourseResponseModel responseModel = new CourseResponseModel();
        BeanUtils.copyProperties(course, responseModel);
        return responseModel;
    }
}
