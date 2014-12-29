import java.io.IOException;

/**
 * Created by Hugo on 13/11/2014.
 */
public class teste {
    public static void main(String[] args) throws IOException {
        SimpleFTP simpleFTP = new SimpleFTP();
        simpleFTP.connect("ftp.xpg.com.br","hugo4566","teste123");
        System.out.println(simpleFTP.pwd());
    }
}
