package net.aliveplex.alive.on_timeonandroid.Message;

/**
 * Created by Aliveplex on 30/1/2560.
 */

public class SimpleSubjectStudent {
    private String StudentId;
    private String SubjectId;

    public SimpleSubjectStudent(String studentId, String subjectId, int subjectSection) {
        StudentId = studentId;
        SubjectId = subjectId;
        SubjectSection = subjectSection;
    }

    public int getSubjectSection() {
        return SubjectSection;
    }

    public String getStudentId() {
        return StudentId;
    }

    public String getSubjectId() {
        return SubjectId;
    }

    private int SubjectSection;

}
