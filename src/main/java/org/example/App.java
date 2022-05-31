package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {

        Gson gson = new Gson();
        DateFormat formatter = new SimpleDateFormat("dd.MM.yy,HH:mm");
        ArrayList<Long> timeDeltas = new ArrayList<>();
        long timesSum = 0;

        try {
            Object object = gson.fromJson(new FileReader("src/json files/tickets.json"), Object.class);
            JsonArray tickets = gson.toJsonTree(object).getAsJsonObject().get("tickets").getAsJsonArray();

            for(JsonElement ticket: tickets) {
                JsonObject obj = ticket.getAsJsonObject();
                String departure_date_time = obj.get("departure_date").getAsString() + "," + obj.get("departure_time").getAsString();
                String arrival_date_time = obj.get("arrival_date").getAsString() + "," + obj.get("arrival_time").getAsString();
                Date departure = formatter.parse(departure_date_time);
                Date arrival = formatter.parse(arrival_date_time);
                long delta = Math.abs(arrival.getTime() - departure.getTime());
                timeDeltas.add(delta);
                timesSum += delta;
            }

            Collections.sort(timeDeltas);
            int i = timeDeltas.size();
            int p = (int) Math.ceil(90 / 100.0 * i);

            long averageFlightTime = timesSum / i;
            long percentile = timeDeltas.get(p - 1);


            System.out.printf("Cреднее время полёта между Владивостоком и Тель-Авивом: %d часов, %d минут%n" +
                            "90-процентиль времени полёта: %d часов, %d минут",
                    TimeUnit.MILLISECONDS.toHours(averageFlightTime),
                    TimeUnit.MILLISECONDS.toMinutes(averageFlightTime) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(averageFlightTime)),
                    TimeUnit.MILLISECONDS.toHours(percentile),
                    TimeUnit.MILLISECONDS.toMinutes(percentile) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(percentile)));
        } catch (FileNotFoundException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}