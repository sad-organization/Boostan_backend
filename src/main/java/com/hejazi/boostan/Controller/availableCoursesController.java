package com.hejazi.boostan.Controller;

import com.hejazi.boostan.database.mysql.*;
import com.hejazi.boostan.database.redis.userSession;
import com.hejazi.boostan.database.redis.userSessionRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class availableCoursesController {
    @Autowired
    private courseRepository courseRepository;

    @Autowired
    private availableCoursesRepository availableCoursesRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private userSessionRepository userSessionRepository;

    @Autowired
    private studentRepository studentRepository;

    @RequestMapping("/availableCourses")
    public String availableCourses(@RequestParam(name = "token", required = true) String token) {
        if (!tokenValid(token))
            return null;
        JSONArray array = new JSONArray();
        for (availableCourses availableCourses:
                availableCoursesRepository.findAll()) {
            JSONObject element = new JSONObject();
            element.put("courseName",courseRepository.findById((long) availableCourses.getCourseId()).get().getName());
            element.put("teacherName",studentRepository.findById((long) availableCourses.getTeacherId()).get().getLastName());
            element.put("courseId",availableCourses.getCourseId());
            element.put("classCapacity", availableCourses.getClassCapacity());
            element.put("presentedTime", availableCourses.getPresentedTime());
            element.put("value", courseRepository.findById((long) availableCourses.getCourseId()).get().getValue());
            ArrayList<Integer> pishniazIdList = new ArrayList();
            String pishniaz = courseRepository.findById((long) availableCourses.getCourseId()).get().getPishniazCouses();
            String [] strings = pishniaz.split("-");
            for (String s:
                 strings) {
                pishniazIdList.add(Integer.parseInt(s));
            }
            JSONArray pishniazJSONArray = new JSONArray();
            for (int pishniazCourse:
                 pishniazIdList) {
                JSONObject pishniazElement = new JSONObject();
                pishniazElement.put("courseName",courseRepository.findById((long) pishniazCourse).get().getName());
                pishniazJSONArray.put(pishniazElement);
            }
            element.put("pishniazCourses",pishniazJSONArray);
            array.put(element);
        }
        return new JSONObject().put("courses",array).toString();
    }

    private boolean tokenValid(String token) {
        return userSessionRepository.findById(token).isPresent();
    }

    private Student retreiveStudentFromCache(String token){
        userSession userSession = userSessionRepository.findById(token).get();
        return studentRepository.findByUsername(userSession.getUsername()).get();
    }
}
