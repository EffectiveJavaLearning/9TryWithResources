import java.io.*;

/**
 * 这个类用来说明Java7之前保证共享资源一定会被释放的方式。
 *
 * 这个方法{@link #firstLineOfFile(String)}看上去似乎还不错，可读性也可以，
 * 因为它只涉及到一种资源的释放。但如果再加一种，就比较丑陋了{@link #copy(String, String)},
 * 这种涉及到两项及以上资源关闭的方法，有时候会不得不使用嵌套try-finally，代码可读性一下子就拉下来了。
 *
 * 此外，更重要的是，有时候finally中的语句也可能会抛出异常。比如{@link #firstLineOfFile(String)}中，
 * br.close()也有可能会有异常抛出来，于是有了这种代码
 * {@link #firstLineOfFile2(String)}
 *
 * 并且，由于底层物理设备可能会失效，br.readLine()和br.close()两者抛出异常的原因又相同，
 * 那么就非常可能给debug工作带来困难。我们通常想捕获的是前一个异常，但却无法进行区分。
 * 虽然通过代码能忽略其一来寻找异常源，可是实际上这样做的人很少，因为麻烦而且代码比较冗长。
 *
 * 在Java7之后，这些问题也都随着try-with-resources的出现而得到解决{@link TryWithResourcesTest}，
 * 比如在IDEA中，自动的代码提示也会建议开发者用这种方式。
 *
 * @author LightDance
 */
public class TryFinallyTest {

    private static final int BUFFER_SIZE = 1024;

    /**这种方式已不是释放资源最优雅的方案了*/
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }

    static void copy(String src, String dst) throws IOException{
        InputStream in = new FileInputStream(src);
        try{
            OutputStream out = new FileOutputStream(dst);
            try{
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.write(buf, 0, n);
                }
            }finally {
                out.close();
            }
        }finally {
            in.close();
        }
    }
    static void firstLineOfFile2(String path) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path));
            System.out.println(inputStream.read());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }
}
