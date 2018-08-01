/**
 * 虚拟一种资源，以配合try-with-resources的使用，这种资源需要implements一下
 * {@link AutoCloseable}接口
 *
 * @author LightDance
 */
public class MyResource implements AutoCloseable{
    @Override
    public void close() throws Exception {
        //release resources here
    }

    public void useRes(){
        System.out.println("哈罗");
    }
}
