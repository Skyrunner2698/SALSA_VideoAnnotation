package com.example.salsa_videoannotation;

import java.util.concurrent.TimeUnit;

public class HelperTool
{
    public HelperTool()
    {

    }

    public static String createTimeRepresentation(long time)
    {
        return String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}
