package concurrency;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by laiwenqiang on 2017/7/29.
 */
public class Lock {
    static ReentrantLock lock = new ReentrantLock();
    static Runnable runnable = () -> {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    };

    public static void main(String[] args) {
        new Thread(runnable, "A").start();
        new Thread(runnable, "B").start();
    }


}
