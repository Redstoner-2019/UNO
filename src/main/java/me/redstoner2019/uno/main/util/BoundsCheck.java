package me.redstoner2019.uno.main.util;

public class BoundsCheck {
    public static boolean within(double mouseX, double mouseY, int x, int y, int width, int height){
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }
}
