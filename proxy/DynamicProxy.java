import java.lang.reflect.Proxy;

/**
 * Created by laiwenqiang on 2017/10/24.
 */
public class DynamicProxy {
    public static void main(String[] args) {
        Subject subject = new RealSubject();
        Subject proxy = (Subject) Proxy.newProxyInstance(subject.getClass().getClassLoader(), subject.getClass().getInterfaces(),
                new MyInvocationHandler(subject));
        proxy.sayHello("laiwenqiang");
    }
}
