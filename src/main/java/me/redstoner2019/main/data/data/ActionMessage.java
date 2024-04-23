package me.redstoner2019.main.data.data;

public class ActionMessage {
    private String text;
    private int offset;
    private long age;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public ActionMessage(String text, int offset, long age) {
        this.text = text;
        this.offset = offset;
        this.age = age;
    }
}
