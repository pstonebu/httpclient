package com.stoneburner.app;

import java.io.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.*;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.conn.SingleClientConnManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

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
        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        HttpClient client = new DefaultHttpClient();

        SchemeRegistry registry = new SchemeRegistry();
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        registry.register(new Scheme("https", socketFactory, 443));
        SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
        DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

        // Set verifier
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        HttpGet method = new HttpGet(inputURI);

        //Execute client with our method
        try
        {
            HttpResponse response = httpClient.execute(method);

            System.out.println("The status: " + response.getStatusLine());

        }

        catch (Exception e)
        {
           System.out.println("Exception occurred: ");
           for (StackTraceElement ste : e.getStackTrace()) {
                System.out.println(ste);
           }
           System.out.println(e.toString());
           System.exit(0);
        }

    }
}
