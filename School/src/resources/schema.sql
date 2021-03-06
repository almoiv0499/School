DROP TABLE IF EXISTS GROUPS CASCADE;
DROP TABLE IF EXISTS STUDENTS CASCADE;
DROP TABLE IF EXISTS COURSES CASCADE;
DROP TABLE IF EXISTS STUDENTS_COURSES CASCADE;


CREATE TABLE IF NOT EXISTS GROUPS
(
    group_id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    group_name character varying(255) NOT NULL,
    CONSTRAINT GROUPS_pkey PRIMARY KEY (group_id)
);
ALTER TABLE GROUPS OWNER to rector;
GRANT ALL ON TABLE GROUPS TO rector;


CREATE TABLE IF NOT EXISTS STUDENTS
(
    student_id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    group_id integer NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    CONSTRAINT STUDENTS_pkey PRIMARY KEY (student_id),
    CONSTRAINT STUDENTS_fkey FOREIGN KEY (group_id) REFERENCES GROUPS(group_id) ON DELETE RESTRICT
);
ALTER TABLE STUDENTS OWNER to rector;
GRANT ALL ON TABLE STUDENTS TO rector;


CREATE TABLE IF NOT EXISTS COURSES
(
    course_id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    course_name character varying(255) NOT NULL,
    course_description character varying(255) NOT NULL,
    CONSTRAINT COURSES_pkey PRIMARY KEY (course_id)
);
ALTER TABLE COURSES OWNER to rector;
GRANT ALL ON TABLE COURSES TO rector;

CREATE TABLE IF NOT EXISTS STUDENTS_COURSES
(
    student_id integer NOT NULL,
    course_id integer NOT NULL,
    CONSTRAINT STUDENTS_COURSES_pkey PRIMARY KEY (student_id, course_id),
    CONSTRAINT STUDENTS_COURSES_fkey1 FOREIGN KEY (student_id) REFERENCES STUDENTS(student_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT STUDENTS_COURSES_fkey2 FOREIGN KEY (course_id) REFERENCES COURSES(course_id) ON DELETE CASCADE ON UPDATE CASCADE
);
ALTER TABLE STUDENTS_COURSES OWNER to rector;
GRANT ALL ON TABLE STUDENTS_COURSES TO rector;

INSERT INTO Student_Group(group_id, student_id) VALUES
(1, 1), (1, 2),
(2, 3),
(3, 4);