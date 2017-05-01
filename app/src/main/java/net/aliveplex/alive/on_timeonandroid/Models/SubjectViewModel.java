package net.aliveplex.alive.on_timeonandroid.Models;

/**
 * Created by Aliveplex on 30/4/2560.
 */

public class SubjectViewModel {
    private String subjectId;
    private int section;

    public SubjectViewModel(String subjectId, int section) {
        this.subjectId = subjectId;
        this.section = section;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public int getSection() {
        return section;
    }
}
