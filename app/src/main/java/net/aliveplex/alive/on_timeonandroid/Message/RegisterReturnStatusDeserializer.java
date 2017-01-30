package net.aliveplex.alive.on_timeonandroid.Message;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.aliveplex.alive.on_timeonandroid.Message.RegisterReturnStatus;
import net.aliveplex.alive.on_timeonandroid.Message.SimpleStudent;
import net.aliveplex.alive.on_timeonandroid.Message.SimpleSubject;
import net.aliveplex.alive.on_timeonandroid.Message.SimpleSubjectStudent;

import java.lang.reflect.Type;

/**
 * Created by Aliveplex on 30/1/2560.
 */

public class RegisterReturnStatusDeserializer implements JsonDeserializer<RegisterReturnStatus> {
    @Override
    public RegisterReturnStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject rootObj = json.getAsJsonObject();
        String statusText = rootObj.get("status").getAsString();
        // SimpleStudent
        JsonArray jsonStudents = rootObj.getAsJsonArray("students");
        SimpleStudent[] students = new SimpleStudent[jsonStudents.size()];

        for (int i = 0; i < students.length; i++) {
            JsonElement studentEle = jsonStudents.get(i);
            JsonObject studentObj = studentEle.getAsJsonObject();
            String studentId = studentObj.get("id").getAsString();
            JsonElement androidIdEle = studentObj.get("androidId");
            String androidId = null;

            if (!androidIdEle.isJsonNull()) {
                androidId = androidIdEle.getAsString();
            }

            students[i] = new SimpleStudent(androidId, studentId);
        }

        // SimpleSubject
        JsonArray jsonSubjects = rootObj.getAsJsonArray("subjects");
        SimpleSubject[] subjects = new SimpleSubject[jsonSubjects.size()];

        for (int i = 0; i < subjects.length; i++) {
            JsonElement subjectEle = jsonSubjects.get(i);
            JsonObject subjectObj = subjectEle.getAsJsonObject();
            String subjectId = subjectObj.get("id").getAsString();
            int subjectSection = subjectObj.get("section").getAsInt();
            subjects[i] = new SimpleSubject(subjectId, subjectSection);
        }

        // SimpleSubjectStudent
        JsonArray jsonSubjectStudents = rootObj.getAsJsonArray("subjectStudents");
        SimpleSubjectStudent[] subjectStudents = new SimpleSubjectStudent[jsonSubjectStudents.size()];

        for (int i = 0; i < subjectStudents.length; i++) {
            JsonElement subjectStudentEle = jsonSubjectStudents.get(i);
            JsonObject subjectStudentObj = subjectStudentEle.getAsJsonObject();
            String subjectId = subjectStudentObj.get("subjectId").getAsString();
            int subjectSection = subjectStudentObj.get("subjectSection").getAsInt();
            String studentId = subjectStudentObj.get("studentId").getAsString();
            subjectStudents[i] = new SimpleSubjectStudent(studentId, subjectId, subjectSection);
        }

        RegisterReturnStatus status = new RegisterReturnStatus(statusText, students, subjects, subjectStudents);

        return status;
    }
}
