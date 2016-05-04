package app.jokeme;

/**
 * Created by thaodv on 5/4/16.
 */
public class JokeModel {
    private long id;
    private String content;
    private String category;
    public JokeModel(long id, String content, String category){
        this.id = id;
        this.content = content;
        this.category = category;
    }
}
