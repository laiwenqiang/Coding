package concurrency;

/**
 * Created by laiwenqiang on 2017/7/27.
 */
public class Interrupt {
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                System.out.println("booting...");
                Thread.currentThread().interrupt();//设置中断状态
            }
        }).start();

    }
}
