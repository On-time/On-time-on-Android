package net.aliveplex.alive.on_timeonandroid.Message;

/**
 * Created by Aliveplex on 30/1/2560.
 */

public class RegisterReturnStatus {
    private String status;

    public RegisterReturnStatus(String status, SimpleStudent[] students, SimpleSubject[] subjects, SimpleSubjectStudent[] subjectStudents) {
        this.status = status;
        this.students = students;
        this.subjects = subjects;
        this.subjectStudents = subjectStudents;
    }

    public SimpleStudent[] getStudents() {
        return students;
    }

    public SimpleSubject[] getSubjects() {
        return subjects;
    }

    public SimpleSubjectStudent[] getSubjectStudents() {
        return subjectStudents;
    }

    private SimpleStudent[] students;
    private SimpleSubject[] subjects;
    private SimpleSubjectStudent[] subjectStudents;

    public String getStatus() {
        return status;
    }
}
