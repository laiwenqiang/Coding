package concurrency;

/**
 * Created by laiwenqiang on 2019/6/11.
 */
public class VolatileTest {

//    private static volatile boolean stop = false;
    private static boolean stop = false;

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            int i = 0;
            while (!stop) {
                i++;
            }
        }).start();

        Thread.sleep(1000);

        stop = true;

    }
}
