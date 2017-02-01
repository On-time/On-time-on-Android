package net.aliveplex.alive.on_timeonandroid.Message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

/**
 * Created by Aliveplex on 1/2/2560.
 */

public class CheckingMessageSerializer implements JsonSerializer<CheckingMessage> {
    private DateTimeFormatter fmt;

    public CheckingMessageSerializer() {
        fmt = ISODateTimeFormat.dateTime();
    }

    @Override
    public JsonElement serialize(CheckingMessage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject rootObj = new JsonObject();
        rootObj.addProperty("subjectId", src.getSubjectId());
        rootObj.addProperty("subjectSection", src.getSubjectSection());

        JsonArray students = new JsonArray();
        CheckingStudent[] tempStudents = src.getStudents();

        for (int i = 0; i < tempStudents.length; i++) {
            JsonObject studentObj = new JsonObject();
            studentObj.addProperty("Id", tempStudents[i].getId());
            studentObj.addProperty("attendtime", fmt.print(tempStudents[0].getAttendTime()));
            students.add(studentObj);
        }

        rootObj.add("students", students);

        return rootObj;
    }
}
