package ua.com.foxminded.universitytest;

import static org.junit.Assert.assertEquals;  
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.university.dao.Connect;
import ua.com.foxminded.university.dao.StudentDAO;
import ua.com.foxminded.university.entities.Group;
import ua.com.foxminded.university.entities.Student;

class TestStudentsDAO {
    
    @Rule
    Connect connect;
    StudentDAO studentsDAO;
    private IDatabaseTester databaseTester;
    
    public TestStudentsDAO() throws IOException, SQLException {
        this.connect = new Connect();
        this.studentsDAO = new StudentDAO(connect.getConnection());
        
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
    void findStudentsAndCompareTheirWithCurrent() throws SQLException, IOException {
        List<Student> expectedStudents = new ArrayList<>();
        expectedStudents.add(new Student(1, 1, "Tony", "Stark"));
        expectedStudents.add(new Student(2, 2, "Bruce", "Wayne"));
        expectedStudents.add(new Student(3, 3, "Lex", "Luthor"));

        assertEquals(expectedStudents, studentsDAO.findAll());
    }

    @Test
    void getIdsFirstAndLastNamesOfStudents() throws SQLException, IOException {
        assertEquals(studentsDAO.getStudentById(1).get().getFirstName(), "Tony");
        assertEquals(studentsDAO.getStudentById(2).get().getLastName(), "Wayne");
    }

    @Test
    void createNewStudentAndCheckHisByLastName() throws SQLException, IOException {
        Student expectedStudents = new Student(4, 1, "Bi", "Han");
        studentsDAO.create(expectedStudents);

        assertEquals(studentsDAO.getStudentById(4).get().getLastName(), "Han");
    }

    @Test
    void updateStudentsAndCheckHisByLastName() throws SQLException, IOException {
        studentsDAO.update(new Student(2, 1, "Bruce", "Stark"));

        assertEquals(studentsDAO.getStudentById(2).get().getLastName(), "Stark");
    }

    @Test
    void deleteStudentAndCheckHisByMethodIsEmpty() throws SQLException, IOException {
        studentsDAO.delete(2);

        assertTrue(studentsDAO.getStudentById(2).isEmpty());
    }

    @Test
    void checkAddingStudentToGroupAndCompareWithCurrent() throws SQLException, IOException {
        List<Student> expectedStudents = new ArrayList<>();
        expectedStudents.add(new Student(1, 1, "Tony", "Stark"));

        assertEquals(expectedStudents, studentsDAO.addStudentToGroup(new Group(1, "AA - 01")));
    }

    @Test
    void checkAddingStudentToCourseAndCompareWithCurrent() throws SQLException, IOException {
        List<Student> expectedStudents = new ArrayList<>();
        expectedStudents.add(new Student(1, 1, "Tony", "Stark"));
        expectedStudents.add(new Student(2, 2, "Bruce", "Wayne"));

        assertEquals(expectedStudents, studentsDAO.addStudentToCourse("Software Developer"));
    }

}

