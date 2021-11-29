package ua.com.foxminded.university.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.university.entities.Course;
import ua.com.foxminded.university.entities.Student;
import ua.com.foxminded.university.exception.UniversityDAOException;

public class CourseDAO {

    private static final int START_VALUE_COURSE_ID = 1;

    private static final String SELECT_COURSE = "SELECT course_id, course_name, course_description FROM COURSES ORDER BY course_id;";
    private static final String SELECT_COURSE_BY_ID = "SELECT course_id, course_name, course_description FROM COURSES WHERE course_id = ?;";
    private static final String INSERT_COURSE = "INSERT INTO COURSES (course_name, course_description) VALUES (?, ?);";
    private static final String UPDATE_COURSE = "UPDATE COURSES SET course_name = ?, course_description = ? WHERE course_id = ?;";
    private static final String DELETE_COURSE = "DELETE FROM COURSES WHERE course_id = ?;";
    private static final String ADD_STUDENT_TO_COURSE = "INSERT INTO STUDENTS_COURSES (student_id, course_id) VALUES (?, ?);";
    private static final String REMOVE_STUDENT_FROM_COURSE = "DELETE FROM STUDENTS_COURSES WHERE student_id = ? AND course_id = ?;";
    private static final String SELECT_BY_STUDENT_ID = "SELECT course_id, course_name, course_description FROM COURSES c "
            + "WHERE c.course_id IN (SELECT course_id FROM STUDENTS_COURSES sc WHERE student_id = ?);";

    private Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Course course) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COURSE,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, course.getCourseName());
            preparedStatement.setString(2, course.getCourseDescription());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                course.setCourseId(generatedKeys.getInt(START_VALUE_COURSE_ID));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public void update(Course course) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COURSE)) {
            preparedStatement.setString(1, course.getCourseName());
            preparedStatement.setString(2, course.getCourseDescription());
            preparedStatement.setInt(3, course.getCourseId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public void delete(Course course) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COURSE)) {
            preparedStatement.setInt(1, course.getCourseId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public List<Course> findAll() throws SQLException {
        List<Course> course = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COURSE)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                course.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return course;
    }

    public Optional<Course> getCourseById(Integer coursesId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COURSE_BY_ID)) {
            preparedStatement.setInt(1, coursesId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return Optional.empty();
    }

    public List<Course> getStudentById(Integer studentId) throws SQLException {
        List<Course> course = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_STUDENT_ID);) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                course.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return course;
    }

    public void addStudentToCourse(Student student, Course course) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_STUDENT_TO_COURSE)) {
            preparedStatement.setInt(1, student.getStudentId());
            preparedStatement.setInt(2, course.getCourseId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public void removeStudentFromCourse(Integer studentId, Integer courseId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_STUDENT_FROM_COURSE)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    private Course parseResultSet(ResultSet resultSet) throws SQLException {
        return new Course(resultSet.getInt("course_id"), resultSet.getString("course_name"),
                resultSet.getString("course_description"));
    }

}
