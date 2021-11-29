package ua.com.foxminded.universitytest;

import static org.junit.jupiter.api.Assertions.*;  

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.university.dao.Connect;
import ua.com.foxminded.university.dao.CourseDAO;
import ua.com.foxminded.university.entities.Course;
import ua.com.foxminded.university.entities.Student;

class TestCoursesDAO {

    @Rule
    Connect connect;
    CourseDAO coursesDAO;
    private IDatabaseTester databaseTester;
    
    public TestCoursesDAO() throws IOException, SQLException {
        this.connect = new Connect();
        this.coursesDAO = new CourseDAO(connect.getConnection());
    }
  
    @BeforeAll
    static void getSQLTables() throws IOException, SQLException {
        Connect connect = new Connect();
        ScriptRunner scriptRunner = new ScriptRunner(connect.getConnection());
        scriptRunner.runScript(Resources.getResourceAsReader("resources/schema.sql"));
    }

    @BeforeEach
    void tuneDatabaseTester() throws Exception {
        Connect connect = new Connect();
        databaseTester = new JdbcDatabaseTester(org.h2.Driver.class.getName(),
                connect.getConnection().getMetaData().getURL());
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(new FlatXmlDataSetBuilder()
                .build(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/data.xml")));
        databaseTester.onSetup();
    }

    @Test
    void findCoursesAndCompareTheirWithCurrent() throws SQLException, IOException {
        List<Course> expectedCourse = new ArrayList<>();
        expectedCourse.add(new Course(1, "Software Developer",
                "Is the process of designing, programming, testing, and bug fixing."));
        expectedCourse.add(new Course(2, "Music", "Is explore music from some different perspectives in these courses."));

        assertEquals(expectedCourse, coursesDAO.findAll());
    }

    @Test
    void getIdsNamesDescriptionsOfCourses() throws SQLException, IOException {
        assertEquals(coursesDAO.getCourseById(1).get().getCourseName(), "Software Developer");
        assertEquals(coursesDAO.getCourseById(2).get().getCourseDescription(),
                "Is explore music from some different perspectives in these courses.");
        assertEquals(coursesDAO.getCourseById(2).get().getCourseId(), 2);
    }

    @Test
    void findCoursesByStudentIds() throws SQLException, IOException {
        List<Course> expectedCourse = new ArrayList<>();
        expectedCourse.add(new Course(1, "Software Developer",
                "Is the process of designing, programming, testing, and bug fixing."));
        expectedCourse.add(new Course(2, "Music", "Is explore music from some different perspectives in these courses."));

        assertEquals(expectedCourse, coursesDAO.getStudentById(1));
        assertEquals(expectedCourse, coursesDAO.getStudentById(2));
    }

    @Test
    void createNewCourseAndCheckHisByCourseName() throws DataSetException, Exception {
        Course expectedCourse = new Course(3, "Foxminded",
                "A minimum of theory, a maximum of real experience and tasks. Learning a programming language, tools, teamwork under the guidance of a mentor.");
        coursesDAO.create(expectedCourse);

        assertEquals(coursesDAO.getCourseById(3).get().getCourseName(), "Foxminded");
    }

    @Test
    void updateCourseAndCheckHisByCourseName() throws DataSetException, Exception {
        Course expectedCourse = new Course(1, "Java Spring",
                "The Spring Framework provides a comprehensive programming and configuration model for modern Java-based enterprise applications - on any kind of deployment platform.");
        coursesDAO.update(expectedCourse);

        assertEquals(coursesDAO.getCourseById(1).get().getCourseName(), "Java Spring");
    }

    @Test
    void deleteCourseAndCheckHisByMethodIsEmpty() throws DataSetException, Exception {
        Course expectedCourse = new Course(1, "Software Developer",
                "Is the process of designing, programming, testing, and bug fixing.");
        coursesDAO.delete(expectedCourse);

        assertTrue(coursesDAO.getCourseById(1).isEmpty());
    }

    @Test
    void addStudentToCourseAndCheckAttitudeToCourse() throws DataSetException, Exception {
        coursesDAO.addStudentToCourse(new Student(3, 3, "Daria", "Moroz"), new Course(1,
                "Software Developer", "Is the process of designing, programming, testing, and bug fixing."));
        int actualCourse = databaseTester.getConnection().createQueryTable("STUDENTS_COURSES",
                "SELECT * FROM STUDENTS_COURSES WHERE student_id = 3 and course_id = 1;").getRowCount();

        assertEquals(1, actualCourse);
    }

    @Test
    void deleteStudentFromCourseAndCheckAttitudeToCourse() throws DataSetException, Exception {
        coursesDAO.removeStudentFromCourse(1, 2);
        int actualCourse = databaseTester.getConnection().createQueryTable("STUDENT_COURSES",
                "SELECT * FROM STUDENTS_COURSES WHERE student_id = 1 and course_id = 2;").getRowCount();

        assertEquals(0, actualCourse);
    }

}

