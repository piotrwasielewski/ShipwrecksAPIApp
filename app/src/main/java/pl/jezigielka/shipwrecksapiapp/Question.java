package pl.jezigielka.shipwrecksapiapp;



import com.google.gson.annotations.SerializedName;

    public class Question {
        public String feature_type;
        public String watlev;
        double latdec;
        double londec;

        @SerializedName("_id")
        public String id;

        @Override
        public String toString() {
            return ("id=" +id+" * type: "+feature_type+" * watlev= "+watlev+" * londec= "+londec+" * latdec= "+latdec+"\n");
        }

        public Question() {
        }

        public Question(Question q) {
            this.feature_type = q.feature_type;
            this.watlev = q.watlev;
            this.latdec = q.latdec;
            this.londec = q.londec;
            this.id = q.id;
        }

        public Question(String feature_type, String watlev, double latdec, double londec, String id) {
            this.feature_type = feature_type;
            this.watlev = watlev;
            this.latdec = latdec;
            this.londec = londec;
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
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






