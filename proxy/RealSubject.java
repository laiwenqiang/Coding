/**
 * Created by laiwenqiang on 2017/10/24.
 */
public class RealSubject implements Subject {

    @Override
    public void sayHello(String content) {
        System.out.println("hello " + content);
    }
}
