package pl.jezigielka.shipwrecksapiapp;


    import java.util.List;

// Klasa przechowująca tablicę odpowiedzi otrzymaną ze StackOverflow

    public class QuestionsList<T> {
        List<T> items;

        public QuestionsList(List<T> items) {
            this.items = items;
        }



        public QuestionsList(QuestionsList q) {
            this.items = q.items;
        }

        public QuestionsList()  {


        }

    }





