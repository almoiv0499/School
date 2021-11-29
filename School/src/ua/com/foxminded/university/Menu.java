package ua.com.foxminded.university;

import java.io.IOException; 
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import ua.com.foxminded.university.dao.Connect;
import ua.com.foxminded.university.dao.CourseDAO;
import ua.com.foxminded.university.dao.GroupDAO;
import ua.com.foxminded.university.dao.StudentDAO;
import ua.com.foxminded.university.entities.Student;

public class Menu {

    private static final int MAX_COUNT_SEPARATOR = 55;
    private static final int MAX_COUNT_OF_GROUPS = 10;
    private static final int MIN_COUNT_OF_GROUPS = 1;
    private static final String SEPARATOR = "-".repeat(MAX_COUNT_SEPARATOR);
    private static final String NEW_LINE = "\n";
    private static final String MENU = "Main menu:\n" + SEPARATOR + "\n"
            + "a. Find all groups with less or equals student count.\n"
            + "b. Find all students related to course with given name.\n" + "c. Add new student.\n"
            + "d. Delete student by STUDENT_ID.\n" + "e. Add a student to the course (from a list).\n"
            + "f. Remove the student from one of his or her courses.\n" + SEPARATOR + "\n"
            + "Select the desired character or '0' for exit from menu: ";

    private CourseDAO courseDAO;
    private GroupDAO groupDAO;
    private StudentDAO studentDAO;

    public Menu(Connect connect) throws IOException, SQLException {
        Connection connection = connect.getConnection();
        courseDAO = new CourseDAO(connection);
        groupDAO = new GroupDAO(connection);
        studentDAO = new StudentDAO(connection);
    }

    public void menu() throws SQLException, IOException {
        System.out.print(MENU);
        Scanner scanner = new Scanner(System.in);
        char choiceMenu = scanner.next().charAt(0);
        switch (choiceMenu) {
        case 'a':
            findGroupsWithStudentCount();
            break;
        case 'b':
            findStudentsRelatedWithCourses();
            break;
        case 'c':
            addNewStudent();
            break;
        case 'd':
            deleteStudentByStudentId();
            break;
        case 'e':
            addStudentToCourse();
            break;
        case 'f':
            removeStudentFromOneOfCourse();
            break;
        case '0':
            exit();
            break;
        default:
        }
        scanner.close();
    }

    private void findGroupsWithStudentCount() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter student's count: ");
        int studentCount = scanner.nextInt();
        System.out.print(SEPARATOR + NEW_LINE);

        groupDAO.getStudentCount(studentCount).stream().forEach(
                group -> System.out.println(String.format("%2s. %s", group.getGroupId(), group.getGroupName())));

        System.out.print(SEPARATOR + NEW_LINE + NEW_LINE);
        menu();
        scanner.close();
    }

    private void findStudentsRelatedWithCourses() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print(SEPARATOR + NEW_LINE);
        courseDAO.findAll().stream().forEach(
                course -> System.out.println(String.format("%2s. %s", course.getCourseId(), course.getCourseName())));
        System.out.println(SEPARATOR);

        System.out.print("Enter course's name: ");
        String courseName = scanner.nextLine();
        System.out.println(SEPARATOR);
        studentDAO.addStudentToCourse(courseName).stream().forEach(student -> System.out.println(
                String.format("%d. - %s %s", student.getStudentId(), student.getFirstName(), student.getLastName())));

        System.out.println(SEPARATOR + NEW_LINE);
        menu();
        scanner.close();
    }

    public void addNewStudent() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first name of student: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name of student: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter group's id that add student: ");
        int groupId = scanner.nextInt();
        while (groupId > MAX_COUNT_OF_GROUPS || groupId < MIN_COUNT_OF_GROUPS) {
            if (groupId > MAX_COUNT_OF_GROUPS || groupId < MIN_COUNT_OF_GROUPS) {
                System.out.println("Out of limit!" + "Enter again!");
            }

        }
        studentDAO.create(new Student(groupId, firstName, lastName));
        System.out.println("Student successfully added!" + NEW_LINE);
        menu();
        scanner.close();
    }

    private void deleteStudentByStudentId() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter student's id: ");
        int studentId = scanner.nextInt();
        studentDAO.delete(studentId);
        System.out.println("Student was delete!" + NEW_LINE);
        menu();
        scanner.close();
    }

    private void addStudentToCourse() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        courseDAO.findAll().stream().forEach(course -> System.out.println(String.format("%d. %s - %s",
                course.getCourseId(), course.getCourseName(), course.getCourseDescription())));
        System.out.print("Enter course's id: ");
        int courseId = scanner.nextInt();

        System.out.print("Enter student's id: ");
        int studentId = scanner.nextInt();
        studentDAO.findAll().stream().forEach(student -> System.out.println(
                String.format("%d. %s %s", student.getStudentId(), student.getFirstName(), student.getLastName())));

        courseDAO.addStudentToCourse(studentDAO.getStudentById(studentId).get(),
                courseDAO.getCourseById(courseId).get());
        System.out.println("Student successfully added to course!" + NEW_LINE);
        menu();
        scanner.close();
    }

    public void removeStudentFromOneOfCourse() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter student's id: ");
        int studentId = scanner.nextInt();
        studentDAO.findAll().stream().forEach(student -> System.out.println(
                String.format("%d. %s %s", student.getStudentId(), student.getFirstName(), student.getLastName())));

        System.out.println("For removing enter course's id: ");
        courseDAO.getStudentById(studentId).stream().forEach(course -> System.out.println(String.format("%d. %s - %s",
                course.getCourseId(), course.getCourseName(), course.getCourseDescription())));
        System.out.print("Enter course's id: ");
        int courseId = scanner.nextInt();

        courseDAO.removeStudentFromCourse(studentId, courseId);
        System.out.println("Student was succesfully deleted from this course!" + NEW_LINE);
        menu();
        scanner.close();
    }

    public void exit() {
        System.out.println(NEW_LINE + "Wait! Doing exit from menu...");
        System.out.println("...");
        System.out.println("..., Ok, thank you for waiting, BYE!");
    }

}
