package com.example.javaadvance;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    public Map<Long, Map<String, Long>> mapAll =  new HashMap<>();
    public Map<Long, Map<String, Long>> mapTemp =  new HashMap<>();

    private static LocalDateTime local = LocalDateTime.now();
    private static long timeReset =  local.getNano();
    private static long timeNews = timeReset;
    private int ttime = 0;
    private boolean sos = false;
    private boolean bel = false;
    private long timeSleep = 0;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static  String sosName = "";
    private Button start;
    private EditText editText;
    private int t = 0;
    private String[] strings = {"http://aressro.com/index2.php?mod=thunter","http://aressro.com/index2.php?mod=tthief","http://aressro.com/index2.php?mod=ttrade", "LaiLotHang", "7000"};
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        start = findViewById(R.id.button);
        editText = findViewById(R.id.editTextTextMultiLine4);
        editText.setEnabled(false);
        String str = "2021-06-24 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        editText.append("---- Hello Hung Ba ----");
        editText.append("\n");
        editText.append("---- Start Scan ----");
        editText.append("\n");
        editText.setTextColor(0xFF00FF00);

            loop(true);

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loop (boolean bl) {
        for (int tt = 0; tt<1000; tt++) {
            start.setText("Stop");
            if (editText.getText().length() > 500)
                editText.setText("");
            editText.setText("hungba " + editText.getText().toString());
//        sosName = strings[3];
            timeSleep = Long.valueOf(strings[4]);
            System.out.println("--------------");
            try {
                new JSONAsyncTask().execute("");
                if (sos && bel) {
                    System.out.println("--------------");
                    sos = false;
                    bel = false;
                } else if (bel) {
                    System.out.println("--------------");
                    bel = false;
                }
                Thread.sleep(timeSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void getNewest(String[] strings) throws IOException {
//
//    }
    private void setMapAll (Map<String, Long> listNews) {
        if (mapAll.isEmpty()) {
            mapAll.put(timeNews, listNews);
            mapTemp.putAll(mapAll);
            return;
        }
        scanAll(listNews);
    }
    private void scanAll(Map<String, Long> listNews) {
        Collection<String> a = listNews.keySet();

        a.stream().forEach(x -> {
            if (mapAll.get(timeNews).isEmpty()) {
                return;
            }
            if (mapAll.get(timeNews).get(x) == null) {
                mapAll.get(timeNews).put(x, listNews.get(x).longValue());
            }
            if ((mapAll.get(timeNews).get(x).longValue() != listNews.get(x).longValue())) {
                String text =  (mapAll.get(timeNews).get(x).longValue() > listNews.get(x).longValue()) ? " down" : " up";
                bel = true;
                if (x.equals(sosName)) {
                    sos = true;
                    System.out.print(ANSI_RED + "+ + +" + ANSI_RESET);
                }
                editText.append(" -> " + x + text + " point at: " +((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ) + "\n");
                mapAll.get(timeNews).put(x, listNews.get(x).longValue());
            }
        });
    }
    private Map<String, Long> listPersonRealTime(String[] sarray) {
        Map<String, Long> m = new HashMap<>();
        for (int i = 0; i < sarray.length - 1; i++) {
            String[] user = sarray[i].split("</td>");
            m.put(user[1].trim().substring(4, user[1].trim().length()), Long.valueOf(user[3].trim().substring(4, user[3].trim().length()).replace(".", "").toString()));
        }
        return m;
    }

    class JSONAsyncTask extends AsyncTask<String, Void, Map<String, Long>> {

        JSONAsyncTask (){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Map<String, Long>  doInBackground(String... urls) {
            try {
                for (int i = 0; i < 3; i++) {
                    URL githubEndpoint = null;
                    githubEndpoint = new URL(strings[i]);
                    HttpURLConnection myConnection =
                            (HttpURLConnection) githubEndpoint.openConnection();
                    myConnection.setRequestMethod("GET");
                    myConnection.setConnectTimeout(25000);
                    myConnection.setReadTimeout(25000);
                    myConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                    InputStream inputStream = myConnection.getInputStream();

                    StringBuilder textBuilder = new StringBuilder();
                    try (Reader reader = new BufferedReader(new InputStreamReader
                            (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                        int c = 0;
                        while ((c = reader.read()) != -1) {
                            textBuilder.append((char) c);
                        }
                    }
                    myConnection.disconnect();
                    inputStream.close();
                    Document page = Jsoup.parse(textBuilder.toString());
                    Elements elements = page.getElementsByClass("title_bot_bg");
                    String[] sarray = elements.toString().split("/tr");
                    return listPersonRealTime(sarray);

                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return new HashMap<>();
        }

        protected void onPostExecute(Map<String, Long> listNews) {
            setMapAll(listNews);
        }
    }
}