package test.drinkwater.rest.model;

public class FileReadResult {
    private String content;

    public FileReadResult(){

    }
    public FileReadResult(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
