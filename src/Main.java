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

        Validator fsaValid = new Validator(in, out);
        fsaValid.start();

        RegexBuilder regexBuilder = new RegexBuilder(fsaValid.getStates(), fsaValid.getInitState(), fsaValid.getFinStates());
        String regex = regexBuilder.build();

        out.print(regex);

        in.close();
        out.close();
    }
}
