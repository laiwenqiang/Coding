package concurrency;

import org.openjdk.jol.info.ClassLayout;

public class SynchronizedTest {
    static Monitor monitor = new Monitor();

    public static void main(String[] args) {
//        System.out.println(monitor.hashCode());
//        System.out.println(ClassLayout.parseInstance(monitor).toPrintable());
        System.out.println(ClassLayout.parseInstance(new String("10000000000000000000000000000000000000000000000000")).toPrintable());
    }

}

class Monitor {int i = 100;}
