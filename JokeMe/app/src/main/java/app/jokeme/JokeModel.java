package app.jokeme;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thaodv on 5/4/16.
 */
public class JokeModel {
    @SerializedName("id")
    private long id;
    private String content;
    private String category;
    @SerializedName("question")
    private String question;
    @SerializedName("answer")
    private String answer;
    private boolean isJoke;
    public JokeModel(long id, String content, String category){
        this.id = id;
        this.content = content;
        this.category = category;
        isJoke = true;
    }

//    public JokeModel(long id, String q, String a, boolean isJoke){
//        this.id = id;
//        this.question = q;
//        this.answer = a;
//        this.isJoke = isJoke;
//    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isJoke() {
        return isJoke;
    }
}
