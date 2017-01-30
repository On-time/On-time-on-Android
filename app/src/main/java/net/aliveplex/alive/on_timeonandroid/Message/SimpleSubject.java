package net.aliveplex.alive.on_timeonandroid.Message;

/**
 * Created by Aliveplex on 30/1/2560.
 */

public class SimpleSubject {
    private String Id;

    public SimpleSubject(String id, int section) {
        Id = id;
        Section = section;
    }

    public int getSection() {
        return Section;
    }

    public String getId() {
        return Id;
    }

    private int Section;
}
