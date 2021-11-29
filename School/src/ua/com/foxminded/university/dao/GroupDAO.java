package ua.com.foxminded.university.dao;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.university.entities.Group;
import ua.com.foxminded.university.exception.UniversityDAOException;

public class GroupDAO {

    private static final int START_VALUE_GROUP_ID = 1;

    private static final String SELECT_GROUP = "SELECT group_id, group_name FROM GROUPS ORDER BY group_id;";
    private static final String SELECT_GROUP_BY_ID = "SELECT group_id, group_name FROM GROUPS WHERE group_id = ?;";
    private static final String INSERT_GROUP = "INSERT INTO GROUPS (group_name) VALUES (?);";
    private static final String UPDATE_GROUP = "UPDATE GROUPS SET group_name = ? WHERE group_id = ?;";
    private static final String DELETE_GROUP = "DELETE FROM GROUPS WHERE group_id = ?;";
    private static final String SELECT_GROUP_BY_STUDENTS_ID = "SELECT g.group_id, g.group_name FROM GROUPS g "
            + "INNER JOIN STUDENTS s ON g.group_id = s.group_id GROUP BY g.group_id, g.group_name HAVING COUNT(*) <= ? ORDER BY g.group_id;";
    
    private Connection connection;
    
    public GroupDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Group group) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GROUP,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                group.setGroupId(generatedKeys.getInt(START_VALUE_GROUP_ID));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public void update(Group group) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GROUP)) {
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.setInt(2, group.getGroupId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public void delete(Group groupId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GROUP)) {
            preparedStatement.setInt(1, groupId.getGroupId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
    }

    public List<Group> findAll() throws SQLException {
        List<Group> group = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUP)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                group.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return group;
    }

    public Optional<Group> getGroupById(Integer groupId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUP_BY_ID)) {
            preparedStatement.setInt(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return Optional.empty();
    }

    public List<Group> getStudentCount(int studentsCount) throws SQLException {
        List<Group> group = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUP_BY_STUDENTS_ID)) {
            preparedStatement.setInt(1, studentsCount);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                group.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new UniversityDAOException("Exception during saving course :", exception);
        }
        return group;
    }

    private Group parseResultSet(ResultSet resultSet) throws SQLException {
        return new Group(resultSet.getInt("group_id"), resultSet.getString("group_name"));
    }

}
