package concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 启用debug手动控制A线程先执行，
 * 查看Condition的调用流程
 * Created by laiwenqiang on 2017/7/22.
 */
public class ConditionTest {
    public static void main(String[] args) {
        final Resource resource = new Resource();
        new Thread(new Runnable() {
            @Override
            public void run() {
                resource.a();
            }
        }, "A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                resource.b();
            }
        }, "B").start();

    }

    static final class Resource {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        void a() {
            try {
                lock.lock();
                condition.await();
                System.out.println("A...");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        void b() {
            try {
                lock.lock();
                condition.signal();
                System.out.println("B...");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }


}
