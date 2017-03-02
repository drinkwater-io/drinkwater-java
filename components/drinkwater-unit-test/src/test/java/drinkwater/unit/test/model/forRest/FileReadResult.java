package drinkwater.unit.test.model.forRest;


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