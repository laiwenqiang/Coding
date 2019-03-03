package nio;

import java.nio.ByteBuffer;

/**
 * Created by laiwenqiang on 2019/3/3.
 */
public class ByteBufferTest {
    public static void main(String[] args) {
        ByteBufferTest test = new ByteBufferTest();
        test.direct();
        test.heap();
    }

    public void direct() {
        System.out.println("DirectBuffer:");
        System.out.println("create time: " + createTest("Direct"));

        ByteBuffer buffer = ByteBuffer.allocateDirect(512);
        System.out.println("IO time: " + ioTest(buffer));
    }

    public void heap() {
        System.out.println("HeapBuffer:");
        System.out.println("create time: " + createTest("Heap"));

        ByteBuffer buffer = ByteBuffer.allocate(512);
        System.out.println("IO time: " + ioTest(buffer));
    }

    public long createTest(String type) {
        long start = current();
        for (int i=0; i<10000; i++) {
            if ("Direct".equals(type)) {
                ByteBuffer.allocateDirect(512);
            } else {
                ByteBuffer.allocate(512);
            }
        }
        return current() - start;
    }

    public long ioTest(ByteBuffer buffer) {
        long start = current();
        for (int i = 0; i < 100000000; i++) {
            for (int j=0; j<100; j++) {
                buffer.putInt(j);
            }
            buffer.flip();
            for (int j=0; j<100; j++) {
                buffer.get();
            }
            buffer.clear();
        }
        return current() - start;
    }

    public static long current() {
        return System.currentTimeMillis();
    }

}
