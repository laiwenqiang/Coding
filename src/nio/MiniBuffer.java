package nio;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by laiwenqiang on 2019/3/3.
 */
public class MiniBuffer {
    long address;
    int position;

    static Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MiniBuffer(int cap) {
        address = unsafe.allocateMemory(cap);
        position = 0;
    }

    public MiniBuffer put(byte b) {
        unsafe.putByte(ix(nextPosition(1)), b);
        return this;
    }

    public MiniBuffer putInt(int i) {
        unsafe.putInt(ix(nextPosition(4)), i);
        return this;
    }

    public byte get() {
        return unsafe.getByte(ix(nextPosition(1)));
    }

    public int getInt() {
        return unsafe.getInt(ix(nextPosition(4)));
    }


    private long ix(int position) {
        return address + position;
    }

    private int nextPosition(int length) {
        int index = position;
        position = position + length;
        return index;
    }

    public void flip() {
        position = 0;
    }

    public static void main(String[] args) {
        MiniBuffer buffer = new MiniBuffer(512);
        int limit = 10;
        for (int i=0; i<limit; i++) {
            buffer.put((byte) i);
        }

        buffer.flip();

        for (int i=0; i<limit; i++) {
            System.out.println(buffer.get());
        }


    }

}
