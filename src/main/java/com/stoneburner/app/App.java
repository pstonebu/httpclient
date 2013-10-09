package com.stoneburner.app;

import java.io.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.*;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String inputURI = "";
        System.out.println( "Please enter a URL for me to fetch: " );

        try
        {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            inputURI = bufferRead.readLine();
        }
        catch (Exception e)
        {
            System.out.println("Caught exception " + e.toString());
        }

        System.out.println( "Fetching '" + inputURI + "'");

        //Instantiate client and method
        HttpClient client = new DefaultHttpClient();
        HttpGet method = new HttpGet(inputURI);

        //Execute client with our method
        try
        {
            HttpResponse response = client.execute(method);

            System.out.println("The status: " + response.getStatusLine());

        }

        catch (Exception e)
        {
           System.out.println("Exception occurred: " + e.getStackTrace() + e.toString());
           System.exit(0);
        }

    }
}
