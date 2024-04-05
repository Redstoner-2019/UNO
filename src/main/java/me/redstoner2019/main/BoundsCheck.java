package me.redstoner2019.main;

public class BoundsCheck {
    public static boolean within(int mouseX, int mouseY, int x, int y, int width, int height){
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }
}
