package sample;

public class Word {
    private int id;
    private String wordTarget;
    private String html;
    private String description;
    private String pronounce;

    public Word() {
    }

    public Word(int id, String wordTarget, String html, String description, String pronounce) {
        this.id = id;
        this.wordTarget = wordTarget;
        this.html = html;
        this.description = description;
        this.pronounce = pronounce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordTarget() {
        return wordTarget;
    }

    public void setWordTarget(String wordTarget) {
        this.wordTarget = wordTarget;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }


}
