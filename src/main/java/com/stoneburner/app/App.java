package com.stoneburner.app;

import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class App 
{
    private static final String peopleURL = "/api/core/v3/people";

    public static void main( String[] args )
    {
        String inputURI = args[0];
        int numUsers = Integer.valueOf(args[1]);
        String adminPassword = args[2];

        inputURI = inputURI + peopleURL;
        System.out.println("Creating " + numUsers + " users at " + inputURI);

        App app = new App();
        NameGenerator generator = new NameGenerator();
        List<Name> names = generator.generateNames(numUsers);
        List<String> titles = app.getValuesFromCSV("titles.csv");
        List<String> departments = app.getValuesFromCSV("departments.csv");

        JSONObject jsonObject = app.getJSONFromFile();

        for (int i = 0; i < numUsers; i++) {
            HttpClient client = new DefaultHttpClient();
            HttpPost method = new HttpPost(inputURI);
            String encoding = Base64.getEncoder().encodeToString(("admin:" + adminPassword).getBytes());
            method.setHeader("Authorization", "Basic " + encoding);
            method.setHeader("Content-Type", "application/json");

            Name name = names.get(i);
            String firstName = name.getFirstName();
            String lastName = name.getLastName();
            String displayName = firstName + "." + lastName;
            String password = "lkjlkj";
            String email = displayName + "@acmeinc.com";
            String username = email;
            int randomTitle = ThreadLocalRandom.current().nextInt(0, titles.size());
            String title = titles.get(randomTitle);
            int randomDepartment = ThreadLocalRandom.current().nextInt(0, departments.size());
            String department = departments.get(randomDepartment);

            jsonObject.put("displayName", displayName);
            ((JSONObject)((JSONArray)jsonObject.get("emails")).get(0)).put("value", email);
            ((JSONObject)jsonObject.get("name")).put("familyName", lastName);
            ((JSONObject)jsonObject.get("name")).put("givenName", firstName);
            ((JSONObject)jsonObject.get("jive")).put("username", username);
            ((JSONObject)jsonObject.get("jive")).put("password", password);
            for (Object object : ((JSONArray)((JSONObject)jsonObject.get("jive")).get("profile"))) {
                JSONObject jobj = (JSONObject)object;
                if (jobj.get("jive_label").equals("Title")) {
                    jobj.put("value", title);
                } else if (jobj.get("jive_label").equals("Department")) {
                    jobj.put("value", department);
                }
            }

            method.setEntity(new ByteArrayEntity(jsonObject.toJSONString().getBytes()));

            //Execute client with our method
            try {
                HttpResponse response = client.execute(method);
                if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                    System.out.println("Successfully created user " + i);
                }

            } catch (Exception e) {
                System.out.println("Exception occurred: ");
                for (StackTraceElement ste : e.getStackTrace()) {
                    System.out.println("\t" + ste);
                }
                System.out.println(e.toString());
                System.exit(0);
            }
        }

    }

    private JSONObject getJSONFromFile() {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream in = classLoader.getResourceAsStream("person.json");
            Object obj = parser.parse(new InputStreamReader(in));
            jsonObject = (JSONObject) obj;
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private List<String> getValuesFromCSV(String filepath) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream in = classLoader.getResourceAsStream(filepath);
            String source = IOUtils.toString(in, "UTF-8");
            return new ArrayList<String>(Arrays.asList(source.split("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
