package ua.com.foxminded.university;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import ua.com.foxminded.university.dao.Connect;
import ua.com.foxminded.university.entities.Student;

public class Main {

    public static void main(String[] args) throws SQLException, IOException, URISyntaxException {
        Connect connect = new Connect();
        DataGenerator dataGenerator = new DataGenerator(connect);
        Menu menu = new Menu(connect);
        dataGenerator.getSQLTables();

        List<Student> students = dataGenerator.getRandomStudents(200);
        dataGenerator.addStudentsToGroups(students, dataGenerator.getRandomGroups(10));
        dataGenerator.addStudentsToCourse(students, dataGenerator.getRandomCourses(), 3);
        menu.menu();
    }

}
