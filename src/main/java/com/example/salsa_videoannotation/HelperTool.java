package com.example.salsa_videoannotation;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.salsa_videoannotation.MainActivity.annotationsList;

public class HelperTool
{
    public HelperTool()
    {

    }

    public static String createTimeRepresentation(long time)
    {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    public static long convertTimeToMilliseconds(String time)
    {
        int pos = time.indexOf(":");
        String min = time.substring(0, pos);
        String sec = time.substring(pos + 1);
        return TimeUnit.MINUTES.toMillis(Long.parseLong(min)) + TimeUnit.SECONDS.toMillis(Long.parseLong(sec));
    }

    public static Annotations getAnnotationByVideoId(String id)
    {
        Optional<Annotations> existingAnnotationOptional = annotationsList.stream().filter(p -> p.getId().equals(id)).findFirst();

        if (existingAnnotationOptional.isPresent())
            return existingAnnotationOptional.get();
        else
            return new Annotations(id, "Teacher");
    }

}
