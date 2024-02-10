import jangl.Jangl;
import jangl.color.ColorFactory;
import jangl.io.Window;

public class Main {
    public static void main(String[] args) {
        Jangl.init(0.5f, 16f / 9);
        Window.setVsync(true);
        Window.setResizable(true);
        Window.setClearColor(ColorFactory.fromNorm(0.075f, 0.075f, 0.075f, 1.0f));

        new Mathematix().run();

        Window.close();
    }
}