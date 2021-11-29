package ua.com.foxminded.university.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Optional;

import ua.com.foxminded.university.entities.Group;
import ua.com.foxminded.university.entities.Student;
import ua.com.foxminded.university.exception.UniversityDAOException;

public class StudentDAO {

    private static final int START_VALUE_STUDENT_ID = 1;

    private static final String SELECT_STUDENT = "SELECT student_id, group_id, first_name, last_name FROM STUDENTS ORDER BY student_id;";
    private static final String SELECT_STUDENT_BY_ID = "SELECT student_id, group_id, first_name, last_name FROM STUDENTS WHERE student_id = ?;";
    private static final String ADD_STUDENT_TO_GROUP = "SELECT student_id, group_id, first_name, last_name FROM STUDENTS WHERE group_id = ?;";
    private static final String INSERT_STUDENT = "INSERT INTO STUDENTS (group_id, first_name, last_name) VALUES (?, ?, ?);";
    private static final String UPDATE_STUDENT = "UPDATE STUDENTS SET group_id = ?, first_name = ?, last_name = ? WHERE student_id = ?;";
    private static final String DELETE_STUDENT = "DELETE FROM STUDENTS WHERE student_id = ?;";
    private static final String ADD_STUDENT_TO_COURSE = "SELECT s.student_id, s.group_id, s.first_name, s.last_name FROM STUDENTS s "
            + "LEFT OUTER JOIN STUDENTS_COURSES sc ON s.student_id = sc.student_id "
            + "LEFT OUTER JOIN COURSES c ON c.course_id = sc.course_id WHERE c.course_name = ?;";
    
    private Connection connection;
    
    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public Student create(Student student) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, student.getGroupId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                student.setStudentId(generatedKeys.getInt(START_VALUE_STUDENT_ID));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return student;
    }

    public void update(Student student) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT)) {
            preparedStatement.setInt(1, student.getGroupId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.setInt(4, student.getStudentId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public void delete(Integer studentId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public List<Student> findAll() throws SQLException {
        List<Student> student = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                student.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return student;
    }

    public Optional<Student> getStudentById(Integer studentId)
            throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ID)) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return Optional.empty();
    }

    public List<Student> addStudentToGroup(Group group) throws SQLException {
        List<Student> student = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_STUDENT_TO_GROUP)) {
            preparedStatement.setInt(1, group.getGroupId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                student.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return student;
    }

    public List<Student> addStudentToCourse(String courseName)
            throws SQLException {
        List<Student> student = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_STUDENT_TO_COURSE)) {
            preparedStatement.setString(1, courseName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                student.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return student;
    }

    private Student parseResultSet(ResultSet resultSet) throws SQLException {
        return new Student(resultSet.getInt("student_id"), resultSet.getInt("group_id"),
                resultSet.getString("first_name"), resultSet.getString("last_name"));
    }

}
