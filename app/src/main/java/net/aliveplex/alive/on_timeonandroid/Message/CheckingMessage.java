package net.aliveplex.alive.on_timeonandroid.Message;

/**
 * Created by Aliveplex on 1/2/2560.
 */

public class CheckingMessage {
    public CheckingStudent[] getStudents() {
        return students;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public int getSubjectSection() {
        return subjectSection;
    }

    private String subjectId;
    private int subjectSection;
    private CheckingStudent[] students;

    public CheckingMessage(CheckingStudent[] students, String subjectId, int subjectSection) {
        this.students = students;
        this.subjectId = subjectId;
        this.subjectSection = subjectSection;
    }
}
