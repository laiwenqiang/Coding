package concurrency;

/**
 * Created by laiwenqiang on 2019/4/14.
 */
public class MemoryModel extends Thread {

    private boolean stop;

    public void run() {
        int i = 0;
        while (!stop) {
            i++;
        }
    }

    public void stopIt() {
        stop = true;
    }

    public boolean getStop() {
        return stop;
    }

    public static void main(String[] args) throws InterruptedException {
        MemoryModel test = new MemoryModel();
        test.start();
        Thread.sleep(1000);
        test.stopIt();
        Thread.sleep(2000);
        System.out.println("finish main");
        System.out.println(test.getStop());
    }
}
