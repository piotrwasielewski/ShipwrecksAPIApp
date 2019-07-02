package pl.jezigielka.shipwrecksapiapp;



import com.google.gson.annotations.SerializedName;

    public class Question {
        public String feature_type;
        public String watlev;
        double latdec;
        double londec;

        @SerializedName("_id")
        public long id;

        @Override
        public String toString() {
            return (feature_type);
        }

        public Question() {
        }

        public Question(String feature_type, String watlev, double latdec, double londec, long id) {
            this.feature_type = feature_type;
            this.watlev = watlev;
            this.latdec = latdec;
            this.londec = londec;
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

//        public String getTitle() {
//            return title;
//        }
//
//        public void setTitle(String title) {
//            this.title = title;
//        }
//
//        public String getLink() {
//            return link;
//        }
//
//        public void setLink(String link) {
//            this.link = link;
//        }
//    }






