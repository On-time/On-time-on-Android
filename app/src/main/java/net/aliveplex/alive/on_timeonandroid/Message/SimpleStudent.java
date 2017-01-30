package net.aliveplex.alive.on_timeonandroid.Message;

/**
 * Created by Aliveplex on 30/1/2560.
 */

public class SimpleStudent
{
    private String Id;
    private String AndroidId;

    public SimpleStudent(String androidId, String id) {
        AndroidId = androidId;
        Id = id;
    }

    public String getAndroidId() {
        return AndroidId;
    }

    public String getId() {
        return Id;
    }
}
