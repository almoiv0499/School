package ua.com.foxminded.universitytest;

import static org.junit.Assert.assertEquals;  
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import ua.com.foxminded.university.dao.GroupDAO;
import ua.com.foxminded.university.entities.Group;

class TestGroupsDAO {

    @Rule
    Connect connect;
    GroupDAO groupsDAO;
    private IDatabaseTester databaseTester;
    
    public TestGroupsDAO() throws IOException, SQLException {
        this.connect = new Connect();
        this.groupsDAO = new GroupDAO(connect.getConnection());
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
    void findGroupsAndCompareTheirWithCurrent() throws SQLException, IOException {
        List<Group> expectedGroups = new ArrayList<>();
        expectedGroups.add(new Group(1, "AA - 01"));
        expectedGroups.add(new Group(2, "BB - 02"));
        expectedGroups.add(new Group(3, "CC - 03"));
        expectedGroups.add(new Group(4, "DD - 04"));

        assertEquals(expectedGroups, groupsDAO.findAll());
    }

    @Test
    void getIdsAndNamesOfGroups() throws SQLException, IOException {
        assertEquals(groupsDAO.getGroupById(1).get().getGroupId(), 1);
        assertEquals(groupsDAO.getGroupById(2).get().getGroupName(), "BB - 02");
        assertEquals(groupsDAO.getGroupById(3).get().getGroupId(), 3);
        assertEquals(groupsDAO.getGroupById(4).get().getGroupName(), "DD - 04");
    }

    @Test
    void createNewGroupAndCheckHisByGroupName() throws DataSetException, Exception {
        Group expectedGroups = new Group(5, "EE - 05");
        groupsDAO.create(expectedGroups);

        assertEquals(groupsDAO.getGroupById(5).get().getGroupName(), "EE - 05");
    }

    @Test
    void updateCoursGroupAndCheckHisByGroupName() throws SQLException, IOException {
        Group expectedGroups = new Group(1, "AA - 1");
        groupsDAO.update(expectedGroups);

        assertEquals(groupsDAO.getGroupById(1).get().getGroupName(), "AA - 1");
    }

    @Test
    void deleteGroupAndCheckHisByMethodIsEmpty() throws DataSetException, Exception {
        Group expectedGroups = new Group(4, "DD - 04");
        groupsDAO.delete(expectedGroups);

        assertTrue(groupsDAO.getGroupById(4).isEmpty());
    }

    @Test
    void checkCountOfStudentsInGroups() throws SQLException, IOException {
        List<Group> expectedGroups = new ArrayList<>();
        expectedGroups.add(new Group(1, "AA - 01"));
        expectedGroups.add(new Group(2, "BB - 02"));
        expectedGroups.add(new Group(3, "CC - 03"));

        assertEquals(expectedGroups, groupsDAO.getStudentCount(1));
    }

    @Test
    void checkWhatInGroupFourNotFoundStudents() throws SQLException, IOException {
        List<Group> expectedGroups = new ArrayList<>();
        expectedGroups.add(new Group(4, "DD - 04"));

        assertNotEquals(expectedGroups, groupsDAO.getStudentCount(1));
    }

}

