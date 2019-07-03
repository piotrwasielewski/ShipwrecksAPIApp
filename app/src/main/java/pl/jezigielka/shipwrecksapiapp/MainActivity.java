package pl.jezigielka.shipwrecksapiapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static retrofit2.converter.gson.GsonConverterFactory.create;

public class MainActivity extends AppCompatActivity {


    HerokuAPI herokuAPI;
    String searchedType;

    JSONObject responseJSON;


    EditText editQueryString;
    QuestionsList<Question> questions;
    QuestionsList<Question> questionsBackup;

    ListView questionsListView;
    Context context = this;
    TextView questionsFound;

    ItemQuestionAdapter itemQuestionAdapter;
    Button btnSzukaj;
    Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editQueryString = findViewById(R.id.editQueryString);

        questions = new QuestionsList<Question>();
        questions.items = new ArrayList<Question>();

        questionsBackup = new QuestionsList<>();
        questionsBackup.items = new ArrayList<>();

        //testowy wpis do sprawdzenia wyswietlenia listy
        //questions.items.add(0, new Question("aaa", "bbb", 2, 3, "11"));

        questionsListView = findViewById(R.id.questionsListView);
        itemQuestionAdapter = new ItemQuestionAdapter();
        questionsListView.setAdapter(itemQuestionAdapter);
        questionsFound = findViewById(R.id.questionsFound);

        btnSzukaj = findViewById(R.id.btnSzukaj);
        createAPI();
        btnSzukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.items.clear();
                questionsFound.setText("Pobieram dane z API. Poczekaj chwilę.");
                setCatalogContent();
                itemQuestionAdapter.notifyDataSetChanged();
                Log.d("btnSzukaj", "onClick: klikam button");
//                Log.d("btnSzukaj", "onClick: rozmiar listy: "+questions.items.size());

            }
        });




        //get the spinner from the xml.
        dropdown = findViewById(R.id.spinner);
        //create a list of items for the spinner.
        String[] items = new String[]{"All", "Visible", "Submerged", "Dangerous"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) searchedType = "";
                else if (position == 1) searchedType = "Visible";
                else if (position == 2) searchedType = "Submerged";
                else if (position == 3) searchedType = "dangerous";

                questionsFound.setText("Filtruję dane");
                Log.d("questBackup.items PRZED", "onItemSelected: "+questionsBackup.items.size());
                if (questions.items.size() > 3) {
                    Log.d("questions.items PRZED", "onItemSelected: " + questions.items.size());
                    Log.d("Test PRZED", "Czy q0 zawiera: " + questions.items.get(0).feature_type.contains(searchedType));
                    Log.d("Test PRZED", "Czy q1 zawiera: " + questions.items.get(1).feature_type.contains(searchedType));
                    Log.d("Test PRZED", "Czy q2 zawiera: " + questions.items.get(2).feature_type.contains(searchedType));
                }


                Log.d("quest.itemsPO clear", "onItemSelected: "+questions.items.size());

                QuestionsList filteredList = new QuestionsList();
                filteredList.items = new ArrayList<Question>();


                for (Question q : questionsBackup.items
                     ) {
                    if (q.feature_type.contains(searchedType)) {
                        filteredList.items.add(new Question(q));
                    }


                    Log.d("FILTER", "q.featureType= " +
                            "Czy zawiera "+searchedType+"? -> "+q.feature_type.contains(searchedType));
                }
                //questions.items.clear();

                questions = filteredList;

                Log.d("quest.items PO filter", "onItemSelected: "+questions.items.size());

                itemQuestionAdapter.notifyDataSetChanged();

                Log.d("SPINNER", "onItemSelected: searchedType="+searchedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    // Utworzenie obiektu wykonującego zapytania do API
    private void createAPI() {
        // Filtr GSON będzie automatycznie tłumaczył pobrany plik JSON na obiekty Javy
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setLenient()
                .create();

        // Fabryka buduje obiekt retrofit służący do pobierania danych
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HerokuAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Fabryka buduje obiekt dla naszego API
        herokuAPI = retrofit.create(HerokuAPI.class);
    }


    // Uruchomienie zapytania do API po kliknięciu przycisku.
    // Tytuł szukanego zapytania pochodzi ze spinnera
    // Klasa QuestionsCallback obsłuży metodę zwrotną
    public void setCatalogContent() {
        herokuAPI.getQuestions(searchedType).enqueue(questionsCallback);
    }


    // Funkcje zwrotne wywoływane po zakończeniu zapytania API
    // Zawsze trzeba zdefiniować zachowanie Retrofita po udanym wywołaniu (onResponse)
    // i po błędzie (onFailure)
    Callback<List<Question>> questionsCallback = new Callback<List<Question>>() {
        @Override
        public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
            if (response.isSuccessful()) {
                // Pobranie danych z odpowiedzi serwera
                List<Question> responseArrayList = response.body();
                Log.d("JSON", "onResponse: "+responseArrayList.toString());
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                //questions.items = gson.fromJson(response.body().toString().trim(), List.class);

                //itemQuestionAdapter.notifyDataSetChanged();
//                for (Question q:questions.items
//                     ) {
//                    Log.d("questions.items", "onResponse: "+questions.items.toString());
//
//                }


                Log.d("response ArrayList", "onResponse: Array size:"+responseArrayList.size());
//                try {
//                    Log.d("response ArrayList", "Element[0] z arraylisty"+responseArrayList.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                for (int i=0; i<responseArrayList.length(); i++) {
//                   // Log.d("JSON", "onResponse: klasa obiektu w responseArrayList= "+object.getClass());
//                    String objectString = "test";
//                    try {
//                        Log.d("JSON", "onResponse: object.toString= "+responseArrayList.getJSONObject(i).toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    //Gson gson = new Gson();
//                    try {
//                        objectString = responseArrayList.getJSONObject(i).toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    Question newQuestion = new Question();
//                    newQuestion = gson.fromJson(objectString, Question.class);
//                    Log.d("GSON", "onResponse: new question= "+newQuestion.toString());
//                    questions.items.add(newQuestion);
//                }

                if (questions.items  != null) {
                    Log.d("response ArrayList", "questions.items.size()= " + questions.items.size());
                    Log.d("response ArrayList", "questions.items.toString: " + questions.items.toString());
                }

                //responseJSON = response.body();

                Log.d("questionsCallback", "onResponse: pobrano dane z serwera ");

                // Odświeżenie widoku listy i informacji o pobranych danych
                questions.items = responseArrayList;
                questionsBackup = new QuestionsList<>(questions);

                itemQuestionAdapter.notifyDataSetChanged();
                questionsListView.setSelectionAfterHeaderView();
//                questionsFound.setText("We have found " + questions.items.size() + " questions");
            } else {
                Log.d("QuestionsCallback", "Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<List<Question>> call, Throwable t) {
            Log.d("Callback failure", "onFailure: :/");
            Log.d("Callback failure", Log.getStackTraceString(t));
            t.printStackTrace();
        }
    };


    // Adapter pozwalający wyświetlać listę obiektów w liście
    private class ItemQuestionAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;

        public ItemQuestionAdapter() {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (questions != null && questions.items != null)
                return questions.items.size();
            else return 0;
        }

        @Override
        public Object getItem(int position) {
            return questions.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            if(rowView == null) {
                rowView = layoutInflater.inflate(R.layout.item_question,null);
            }
            TextView tvTyp = rowView.findViewById(R.id.typ);
            TextView tvWatlev = rowView.findViewById(R.id.watlev);
            TextView tvDlugosc = rowView.findViewById(R.id.dlugosc);
            TextView tvSzerokosc = rowView.findViewById(R.id.szerokosc);

            Question currentQuestion = questions.items.get(position);
            tvTyp.setText(currentQuestion.feature_type);
            tvWatlev.setText(currentQuestion.watlev);
            tvDlugosc.setText(Double.toString(currentQuestion.londec));
            tvSzerokosc.setText(Double.toString(currentQuestion.latdec));

            return rowView;
        }
    }





}
