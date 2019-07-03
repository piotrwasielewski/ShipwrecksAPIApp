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

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    HerokuAPI herokuAPI;
    String searchedType;

    JSONObject responseJSON;


    EditText editQueryString;
    QuestionsList<Question> questions;
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

        //testowy wpis do sprawdzenia wyswietlenia listy
        questions.items.add(0, new Question("aaa", "bbb", 2, 3, 11));

        questionsListView = findViewById(R.id.questionsListView);
        questionsListView.setAdapter(new ItemQuestionAdapter());
        questionsFound = findViewById(R.id.questionsFound);

        btnSzukaj = findViewById(R.id.btnSzukaj);
        createAPI();
        btnSzukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCatalogContent();
                Log.d("btnSzukaj", "onClick: klikam button");
                Log.d("btnSzukaj", "onClick: rozmiar listy: "+questions.items.size());

            }
        });




        //get the spinner from the xml.
        dropdown = findViewById(R.id.spinner);
        //create a list of items for the spinner.
        String[] items = new String[]{"Visible", "Submerged", "Dangerous"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) searchedType = "Visible";
                else if (position == 1) searchedType = "Submerged";
                else if (position == 2) searchedType = "Dangerous";
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
        herokuAPI.getQuestions().enqueue(questionsCallback);
    }


    // Funkcje zwrotne wywoływane po zakończeniu zapytania API
    // Zawsze trzeba zdefiniować zachowanie Retrofita po udanym wywołaniu (onResponse)
    // i po błędzie (onFailure)
    Callback<QuestionsList<Question>> questionsCallback = new Callback<QuestionsList<Question>>() {
        @Override
        public void onResponse(Call<QuestionsList<Question>> call, Response<QuestionsList<Question>> response) {
            if (response.isSuccessful()) {
                // Pobranie danych z odpowiedzi serwera
                questions = response.body();

                //responseJSON = response.body();
                Log.d("questionsCallback", "onResponse: pobrano dane z serwera ");

                // Odświeżenie widoku listy i informacji o pobranych danych
                ((ItemQuestionAdapter)questionsListView.getAdapter()).notifyDataSetChanged();
                questionsListView.setSelectionAfterHeaderView();
                questionsFound.setText("We have found " + questions.items.size() + " questions");
            } else {
                Log.d("QuestionsCallback", "Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<QuestionsList<Question>> call, Throwable t) {
            Log.d("Callback failure", "onFailure: :/");
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
            return questions.items.size();
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
            tvTyp.setText(currentQuestion.feature_type.toString());
            tvWatlev.setText(currentQuestion.watlev.toString());
            tvDlugosc.setText(Double.toString(currentQuestion.londec));
            tvSzerokosc.setText(Double.toString(currentQuestion.latdec));

            return rowView;
        }
    }





}
