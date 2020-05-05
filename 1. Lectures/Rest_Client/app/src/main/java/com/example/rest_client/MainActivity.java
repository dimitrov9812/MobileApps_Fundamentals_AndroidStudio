package com.example.rest_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MainActivity extends AppCompatActivity {

    public EditText editName, editPhone, editEmail;
    public Button btnInsert;
    private ListView simpleList;

    // Checks if it matches the Regex
    public boolean match(String expression, String input)
    throws IllegalArgumentException, PatternSyntaxException
    {
        Pattern p = Pattern.compile(expression);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public String getPostDataString(HashMap<String,String> params)
            throws UnsupportedEncodingException
    {
        StringBuilder feedback = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String,String> entry: params.entrySet()){
            if(first){
                first = false
            }
            else{
                feedback.append('&');
            }
            feedback.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
            feedback.append("=");
            feedback.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
        }
        return feedback.toString();
    }

    public String postData(String methodName, String userName, String fileJSON)
            throws IOException, UnsupportedEncodingException, MalformedURLException
    {
        HashMap <String, String> params = new HashMap<>();
        params.put("methodName", methodName);
        params.put("userName", userName);
        params.put("fileJSON", fileJSON);

        String result = "";
        URL url = new URL("http://gp.gpashev.com:93/testTels/service.php");
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        client.setRequestMethod("POST");
        client.setRequestProperty("multipart/form-data", "http://gp.gpashev.com:93/testTels/service.php;charset=UTF-8");
        client.setDoOutput(true);
        client.setDoInput(true);
        OutputStream os = client.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(params));
        // close our buffers
        writer.flush();
        writer.close();
        os.close();
        // get the response code from the server
        int ResponseCode = client.getResponseCode();

        if(ResponseCode == HttpURLConnection.HTTP_OK){
            String line = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while((line = br.readLine()) != null){
                result += line;
                result += "\n";
            }
            br.close();
        }
        else{
            result = "{'error': 'HTTP Response code': "+ResponseCode +" }";
        }
    }

    public void GetPhones(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    getSimpleList().clearChoices();
                    String results = postData("GetListOfProjects","user","{}");

                    JSONArray ja = (JSONArray) new JSONTokener(results).nextValue();

                    ArrayList<String> listResults = new ArrayList<>();
                    for(int i = 0 ;i<ja.length();i++){
                        String name = ja.getJSONObject(i).getString("name");
                        listResults.add(name);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.activity_list_view,
                    R.id.textView,
                    listResults
                    );

                    getSimpleList().setAdapter(arrayAdapter);

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        t.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        btnInsert = findViewById(R.id.btnInsert);
        simpleList = findViewById(R.id.simpleList);

        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);

        GetPhones();

        btnInsert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String name = editName.getText().toString();
                final String email = editEmail.getText().toString();
                final String phone = editPhone.getText().toString();

                try{

                    if(!match("(//d{2,}[\\-\\.])+?)+", phone)){
                        throw new Exception("Invalid phone number");
                    }
                    if(!match("[\\w\\/\\-]@([\\w\\-\\.])+([A-Za-a]{2,4})", email)){
                        throw new Exception("Invalid E-mail Address");
                    }
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String fileJSON = "{";
                            fileJSON += "'username'+ '"+name+"', ";
                            fileJSON += "'email'+ '"+email+"', ";
                            fileJSON += "'phone'+ '"+phone+"' ";
                            fileJSON += "} ";

                            String result = "";

                            try{
                                JSONObject jo = (JSONObject)new JSONTokener(result).nextValue();
                                String message = jo.getString("message");
                                if(message == null){
                                    throw new Exception("Error message:" + result);
                                }
                            }catch (Exception e){
                                result = "{'exception': '"+e.getMessage()+"'}";
                            }

                            GetPhones();

                            try{
                                result = postData("SaveToFile", name, fileJSON);
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    t.start();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setSimpleList(ListView simpleList) {
        this.simpleList = simpleList;
    }
}
