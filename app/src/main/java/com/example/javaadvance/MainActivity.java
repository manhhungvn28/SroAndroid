package com.example.javaadvance;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    public Map<Long, Map<String, Long>> mapAll =  new HashMap<>();

    private static LocalDateTime local = LocalDateTime.now();
    private static long timeNews = local.getNano();;
    private boolean sos = false;
    private boolean bel = false;
    private boolean pause = false;
    private long timeSleep = 0;
    private long timeReload = 0;
    private Button start;
    private TextView editText;
    private ScrollView scrollView;
    private String[] strings = {"http://aressro.com/index2.php?mod=thunter",
                                "http://aressro.com/index2.php?mod=tthief",
                                "http://aressro.com/index2.php?mod=ttrade",
                                "7000",
                                "1000"};
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        start = findViewById(R.id.button);
        editText = findViewById(R.id.editTextTextMultiLine4);
        scrollView = findViewById(R.id.scrollView2);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        timeSleep = Long.valueOf(strings[3]);
        timeReload = Long.valueOf(strings[4]);
        editText.append("--->  Bấm nút Start để bắt đầu quét.");
        editText.append("\n");
        editText.setEnabled(false);
        editText.setTextColor(0xFF00FF00);
        start.setOnClickListener(v -> {
            pause = !pause;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    getNewest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getNewest() throws IOException {
        if (pause == true) {
            start.setText("Stop");
            new Process().execute();
        } else {
            start.setText("Start");
            editText.append("-> Đã tắt quét !!! \n");
        }
    }

    class Process extends AsyncTask<Void, String, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            String str = "2021-06-30 00:00:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
                while (local.compareTo(dateTime.plusDays(15)) < 0) {
                    if (!pause) {
                        return "Stop";
                    }
                    Map<String, Long> listNewAll =  new HashMap<>();
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

                            Document page = Jsoup.parse(textBuilder.toString());
                            Elements elements = page.getElementsByClass("title_bot_bg");
                            String[] sarray = elements.toString().split("/tr");
                            listNewAll.putAll(listPersonRealTime(sarray));
                        }
                        publishProgress(setMapAll(listNewAll));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(timeSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            return "Đã hết ngày dùng thử";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            editText.setText(" --> Đang quét . . . \n");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0].length() > 0) {
                editText.append(values[0]);
                editText.append("   -----   -----   -----   -----   -----" + "\n");
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, timeReload);
            }
            if (bel) {
                Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                final Ringtone ringtone1 = RingtoneManager.getRingtone(context, ringtone);
                ringtone1.play();
                bel = false;
            }
        }
    }


    private synchronized String setMapAll (Map<String, Long> listNews) {
        if (mapAll.isEmpty()) {
            mapAll.put(timeNews, listNews);
            return "";
        }
        return scanAll(listNews);
    }
    private synchronized String scanAll(Map<String, Long> listNews) {
        Collection<String> a = listNews.keySet();
        AtomicReference<String> string = new AtomicReference<>("");
        a.stream().forEach(x -> {
            if (mapAll.get(timeNews).isEmpty()) {
                return;
            }
            if (mapAll.get(timeNews).get(x) == null) {
                mapAll.get(timeNews).put(x, listNews.get(x).longValue());
            }
            if (mapAll.get(timeNews).get(x).longValue() != listNews.get(x).longValue()) {
                String text =  (mapAll.get(timeNews).get(x).longValue() > listNews.get(x).longValue()) ? " down" : " up";
                bel = true;
                string.set(string + (" -> " + x + text + " point at: " + ((LocalDateTime.now().getHour() < 10) ? "0" + LocalDateTime.now().getHour() : LocalDateTime.now().getHour()) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0" + LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute()) + "\n"));
                mapAll.get(timeNews).put(x, listNews.get(x).longValue());
            }
        });
        return string.get();
    }

    private Map<String, Long> listPersonRealTime(String[] sarray) {
        Map<String, Long> m = new HashMap<>();
        for (int i = 0; i < sarray.length - 1; i++) {
            String[] user = sarray[i].split("</td>");
            m.put(user[1].trim().substring(4, user[1].trim().length()), Long.valueOf(user[3].trim().substring(4, user[3].trim().length()).replace(".", "").toString()));
        }
        return m;
    }
}