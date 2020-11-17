import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        new MainController().start();
    }

    public static void print(Object object) {
        System.out.println(object);
        if (BuildConfig.DEBUG) {
            try {
                System.in.read();
            } catch (IOException ignored) {}
        }
    }
}
