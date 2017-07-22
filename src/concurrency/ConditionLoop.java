package concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 启动三个线程，按照顺序循环打印。
 * thread01 -> print("01")
 * thread02 -> print("02")
 * thread03 -> print("03)
 *
 * 运用Condition来实现
 *
 * Created by laiwenqiang on 2017/7/13.
 */

public class ConditionLoop {

    public static void main(String[] args) {
        final Print print = new Print();
        Thread thread01 = new Thread(new Runnable() {
            @Override
            public void run() {
               print.doPrint();
            }
        });
        thread01.setName("01");
        thread01.start();

        Thread thread02 = new Thread(new Runnable() {
            @Override
            public void run() {
               print.doPrint();
            }
        });
        thread02.setName("02");
        thread02.start();

        Thread thread03 = new Thread(new Runnable() {
            @Override
            public void run() {
                print.doPrint();
            }
        });
        thread03.setName("03");
        thread03.start();
    }

}

class Print {

    private Lock lock = new ReentrantLock();
    private boolean flag01 = true, flag02 = false, flag03 = false;
    private Condition condition01 = lock.newCondition(), condition02 = lock.newCondition(),
            condition03 = lock.newCondition();

    public void doPrint() {
        String name = Thread.currentThread().getName();
        try {
            lock.lock();
            if (name.equals("01")) {
                print01();
            }
            if (name.equals("02")) {
                print02();
            }
            if (name.equals("03")) {
                print03();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void print01() throws InterruptedException {
        while (true) {
            if (flag01) {
                System.out.println("print01...");
                flag01 = false;
                flag02 = true;
                condition02.signal();
                Thread.sleep(1000);
            } else {
                condition01.await();
            }
        }
    }

    private void print02() throws InterruptedException {
        while (true) {
            if (flag02) {
                System.out.println("print02...");
                flag02 = false;
                flag03 = true;
                condition03.signal();
                Thread.sleep(1000);
            } else {
                condition02.await();
            }
        }
    }

    private void print03() throws InterruptedException {
        while (true) {
            if (flag03) {
                System.out.println("print03...");
                flag03 = false;
                flag01 = true;
                condition01.signal();
                Thread.sleep(1000);
            } else {
                condition03.await();
            }
        }
    }
}
