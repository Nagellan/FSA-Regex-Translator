import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class reads FSA description in the 'fsa.txt' and gives output 'result.txt' containing an error description or
 * a report, indicating if FSA is complete (or incomplete) and warning if any.
 *
 * @author Irek Nazmiev <i.nazmiev@innopolis.ru> B17-05, Innopolis University
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("fsa.txt"));
        PrintWriter out = new PrintWriter("result.txt");

        Validator fsaValid = new Validator(in);
        String result = fsaValid.start();
        out.print(result);

        in.close();
        out.close();
    }
}
