package com.example.adam.rozpiskimeczowe.offical;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adam.rozpiskimeczowe.R;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.Map;


public class GetDataFromBeachPzps extends AppCompatActivity {

    Button btnGetData;
    Document doc;
    String teams;

    //add
    int quantityOfTeam = 0;
    ArrayList<String> names;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data_from_beach_pzps);

        //ADD LIST
        final ListView list = findViewById(R.id.getDataListView);
        list.setItemsCanFocus(true);


        //GET DATA FROM WEBSITE
        names = new ArrayList<>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        btnGetData = findViewById(R.id.getDataBtn);


        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Selecja turnieju za pomocą numeru na końcu URL
                try {

                    Connection.Response res = Jsoup.connect("http://beach.pzps.pl/pl/site/login")
                            .data("LoginForm[email]", "adamandrys7@gmail.com", "LoginForm[password]", "66romek6")
                            .method(Connection.Method.POST)
                            .execute();

                    Map<String, String> loginCookies = res.cookies();

                    doc = Jsoup.connect("http://beach.pzps.pl/pl/tournament/793")
                            .cookies(loginCookies)
                            .get();

                    Element innerTable = doc.select(".items tbody").first();
                    Elements rows = innerTable.select("tr");

                    for (Element row : rows) {
                        Elements cells = row.select("td:eq(4)");
                        //MOŻLIWA ZMIANA DO TURNIEJU TO JEST ZROBIONE PO ZAKOŃCZENIU TURNIEJU !!!!!!!!!!
                        teams = cells.text();

                        quantityOfTeam++;

                        names.add(teams);

                    }

                    customAdapter = new CustomAdapter(getApplicationContext());
                    list.setAdapter(customAdapter);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    class CustomAdapter extends BaseAdapter {
        private Context context;
        private String[] rValues = new String[quantityOfTeam];

        CustomAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return quantityOfTeam;
        }

        @Override
        public String getItem(int position) {
            return names.get(position) + "\n" + names.get(position + quantityOfTeam);
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            for (int i = 0; i < quantityOfTeam; i++) {
                rValues[i] = "R" + (i + 1);
            }
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.brazylian_item_list, null, true);


                holder.editText = convertView.findViewById(R.id.brazItemListEditTextName1);
                holder.editText2 = convertView.findViewById(R.id.brazItemListEditTextName2);
                holder.textView = convertView.findViewById(R.id.brazItemListtextViewR);

                //convert
                names.set(position, names.get(position).replaceAll("\\(.*?\\)", ""));
                String team1 = names.get(position).split("/")[0];
                String team2 = names.get(position).split("/")[1];

                team1 = team1.substring(indexOfFirstCapitalLetter(team1));
                team2 = team2.substring(indexOfFirstCapitalLetter(team2));

                team1 = team1.split(" ")[0];
                team2 = team2.split(" ")[0];

                holder.editText.setText(team1);
                holder.editText2.setText(team2);


                convertView.setTag(holder);
            } else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(rValues[position]);

            holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        names.set(position, holder.editText.getText().toString());

                    }
                }
            });

            holder.editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        names.set(position + quantityOfTeam, holder.editText2.getText().toString());
                    }
                }
            });

            return convertView;
        }

        private class ViewHolder {

            protected EditText editText;
            protected EditText editText2;
            protected TextView textView;

        }

    }

    int indexOfFirstCapitalLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) return i;
        }
        return -1;
    }

}

