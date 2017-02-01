package net.aliveplex.alive.on_timeonandroid.Message;

import org.joda.time.DateTime;

/**
 * Created by Aliveplex on 1/2/2560.
 */

public class CheckingStudent {
    private String id;

    public CheckingStudent(String id, DateTime attendTime) {
        this.attendTime = attendTime;
        this.id = id;
    }

    public DateTime getAttendTime() {
        return attendTime;
    }

    public String getId() {
        return id;
    }

    private DateTime attendTime;
}
