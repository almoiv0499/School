package ua.com.foxminded.university;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.h2.tools.RunScript;

import ua.com.foxminded.university.dao.Connect;
import ua.com.foxminded.university.dao.CourseDAO;
import ua.com.foxminded.university.dao.GroupDAO;
import ua.com.foxminded.university.dao.StudentDAO;
import ua.com.foxminded.university.entities.Course;
import ua.com.foxminded.university.entities.Group;
import ua.com.foxminded.university.entities.Student;

public class DataGenerator {

    private static final String RANDOM_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String RANDOM_NUMBERS = "0123456789";
    private static final String COURSES_NAMES = "resources/courses.txt";
    private static final String SPLIT = "_";
    private static final String GROUP_FORMAT = "%s - %s";
    private static final String STUDENT_FORMAT = "%s";
    private static final String FIRST_NAMES_STUDENTS = "resources/firstName.txt";
    private static final String LAST_NAMES_STUDENTS = "resources/lastName.txt";
    private static final int START_VALUE_RANDOM_GROUP = 0;
    private static final int START_VALUE = 1;
    private static final int START_VALUE_RANDOM_COURSE = 1;
    private static final int MIN_COUNT_STUDENTS_IN_GROUP = 10;
    private static final int MAX_COUNT_STUDENTS_IN_GROUP = 30;
    private static final int VALUE_ZERO = 0;
    private static final int DECREASE_BY_ONE = 1;
    private static final int ADD_BY_ONE = 1;
    private static final int MAX_LENGTH_ARRAY_WITH_COURSES = 2;
    private static final int NAME_OF_COURSE = 0;
    private static final int DESCRIPTION_OF_COURSE = 1;
    private static final int MAX_LENGTH_LETTERS = 2;
    private static final int MAX_LENGTH_NUMBERS = 2;
    
    private Connection connection;
    private CourseDAO courseDAO;
    private GroupDAO groupDAO;
    private StudentDAO studentDAO;
    
    public DataGenerator(Connect connect) throws SQLException, IOException {
        this.connection = connect.getConnection();
        courseDAO = new CourseDAO(connection);
        groupDAO = new GroupDAO(connection);
        studentDAO = new StudentDAO(connection);
    }
    
    public void getSQLTables() throws FileNotFoundException, SQLException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("resources/schema.sql");
        File file = new File(url.toURI());
        RunScript.execute(connection, new FileReader(file));
    }
    
    public void addStudentsToGroups(List<Student> students, List<Group> groups)
            throws SQLException {
        for (Student student : students) {
            int randomGroupId = groups.get(getRandomNumber(START_VALUE_RANDOM_GROUP, groups.size() - DECREASE_BY_ONE))
                    .getGroupId();
            student.setGroupId(randomGroupId);
        }

        Map<Integer, Long> sizeGroups = students.stream()
                .collect(Collectors.groupingBy(Student::getGroupId, Collectors.counting()));

        for (Student student : students) {
            Long studentsInGroup = sizeGroups.get(student.getGroupId());
            if (studentsInGroup >= MIN_COUNT_STUDENTS_IN_GROUP && studentsInGroup <= MAX_COUNT_STUDENTS_IN_GROUP) {
                studentDAO.update(student);
            } else {
                student.setGroupId(VALUE_ZERO);
            }
        }
    }

    public void addStudentsToCourse(List<Student> students, List<Course> courses, int courseCount)
            throws SQLException {
        for (Student student : students) {
            int randomCourses = getRandomNumber(START_VALUE_RANDOM_COURSE, courseCount);
            Collections.shuffle(courses);
            for (Course course : courses.subList(START_VALUE, randomCourses)) {
                courseDAO.addStudentToCourse(student, course);
            }
        }
    }

    public List<Course> getRandomCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        for (String dataFromFile : fileReader(COURSES_NAMES)) {
            String[] partsCourses = dataFromFile.split(SPLIT);
            if (partsCourses.length == MAX_LENGTH_ARRAY_WITH_COURSES) {
                Course course = new Course(partsCourses[NAME_OF_COURSE], partsCourses[DESCRIPTION_OF_COURSE]);
                courses.add(course);
                courseDAO.create(course);
            }
        }
        return courses;
    }

    public List<Group> getRandomGroups(int groupsCount) throws SQLException {
        List<Group> groups = new ArrayList<>();
        while (groups.size() < groupsCount) {
            Group group = new Group(String.format(GROUP_FORMAT, getRandomString(MAX_LENGTH_LETTERS, RANDOM_LETTERS),
                    getRandomString(MAX_LENGTH_NUMBERS, RANDOM_NUMBERS)));
            groups.add(group);
            groupDAO.create(group);
        }
        return groups;
    }

    public List<Student> getRandomStudents(int studentsCount)
            throws SQLException {
        Random random = new Random();
        List<String> firstName = fileReader(FIRST_NAMES_STUDENTS);
        List<String> lastName = fileReader(LAST_NAMES_STUDENTS);
        List<Student> students = new ArrayList<>();

        while (students.size() < studentsCount) {
            Student student = new Student(
                    String.format(STUDENT_FORMAT, firstName.get(random.nextInt(firstName.size()))),
                    String.format(STUDENT_FORMAT, lastName.get(random.nextInt(lastName.size()))));
            students.add(student);
            studentDAO.create(student);
        }
        return students;
    }

    private String getRandomString(int elementsCount, String str) {
        StringBuilder stringBuilder = new StringBuilder(elementsCount);
        for (int i = 0; i < elementsCount; i++) {
            int randomIndexChar = ((int) (Math.random() * str.length()));
            char randomChar = str.charAt(randomIndexChar);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min + ADD_BY_ONE)) + min);
    }

    private List<String> fileReader(String fileName) {
        List<String> result = new ArrayList<>();
        File data = new File(
                Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(fileName)).getFile());

        try (Stream<String> lineStream = Files.newBufferedReader(Paths.get(data.getPath())).lines()) {
            lineStream.forEach(result::add);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }

}
