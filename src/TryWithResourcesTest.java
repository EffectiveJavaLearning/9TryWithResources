import java.io.*;

/**
 * 许多资源使用完毕后需要用CLOSE方法手动关闭，例如InputStream、OutputStream和java.sql.Connection。
 * 释放资源通常不被重视，这样可能会造成很多严重的问题。虽然这些资源很多会用finalizer()方法保证一下被安全释放，
 * 但finalizer()的一些缺陷可能并不那么让人满意(见item 8)。
 *
 * Java7之前，一般会用try-finally处理有关资源释放的问题，保证即使出现异常，资源也会在finally中被释放，
 * 比如{@link TryFinallyTest}，但这种方式也存在可读性、debug等方面的些许不足
 *
 * Java7之后，try-with-resources语句应运而生，其实说白了就是给了个语法糖，
 * 反编译.class文件后看到的还是{@link TryFinallyTest#firstLineOfFile2(String)}的形式，
 * 但是可读性果然要高很多。并且，反编译后会有这么一行：var2.addSuppressed(var10);，
 * 这样就把在finally中抛出的异常抑制住了，不过如果我们像查看的话还是可以通过
 * {@link Throwable#getSuppressed()}去查看它。
 *
 * 使用的话，资源类需要implements一下{@link AutoCloseable}接口，然后就能用这种简单的方式了，
 * 比较简单，比如这个{@link MyResource}，然后直接在try后面的括号中添加需要保证被关闭的资源声明语句。
 * 现在Java自带的很多类已经修改为直接或间接地继承这个接口了，可以放心使用，比如
 * {@link java.io.OutputStream},{@link java.io.InputStream},{@link java.io.BufferedReader}等。
 *
 * 对比{@link #copy(String, String)}和之前的{@link TryFinallyTest#copy(String, String)}，
 * 可读性优势显而易见
 *
 * 同样，try-with-resources也可以设置捕获异常后的处理方案，比如这个{@link #firstLineOfFile(String, String)},
 * 可以在出现io异常时返回默认字符串，然后根据.close()方法释放资源。
 *
 * @author LightDance
 */
public class TryWithResourcesTest {

    private static final int BUFFER_SIZE = 1024;

    static void copy(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        }
    }

    static String firstLineOfFile(String path, String defaultVal) {
        try (BufferedReader br = new BufferedReader(
                new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            return defaultVal;
        }
    }

    public static void main(String[] args) throws Exception {
        try(MyResource resource = new MyResource()){
            resource.useRes();
        }
    }
}
