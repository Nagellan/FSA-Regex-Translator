import java.util.LinkedList;

/**
 * This class represents FSA-to-Regex translator which builds a Regular Expression by the given FSA by using
 * the Kleene's algorithm.
 *
 * @author Irek Nazmiev <i.nazmiev@innopolis.ru> B17-05, Innopolis University
 */
public class RegexBuilder {
    RegexBuilder(LinkedList<State> states, State initState, LinkedList<State> finStates) {
        this.states = states;
        this.initState = initState;
        this.finStates = finStates;

        this.initIndex = 0;
        this.finIndexes = new LinkedList<>();
    }

    private LinkedList<State> states;       // states of an FSA
    private State initState;                // init state of an FSA
    private LinkedList<State> finStates;    // final states of an FSA

    private int initIndex;                      // index of an initial state in states list
    private LinkedList<Integer> finIndexes;     // indexes of final states in states list

    /**
     * This method translates FSA to Regular Expression.
     *
     * @return string containing translated regular expression
     */
    public String build() {
        String[][] R = getInitRegex();

        for (int k = 0; k < states.size(); k++) {
            String[][] newR = new String[states.size()][states.size()];

            for (int i = 0; i < states.size(); i++)
                for (int j = 0; j < states.size(); j++)
                    newR[i][j] = "(" + R[i][k] + ")(" + R[k][k] + ")*(" + R[k][j] + ")|(" + R[i][j] + ")";

            R = newR;
        }

        String result = "";
        for (int j : finIndexes)
            result = result.concat(R[initIndex][j]) + "|";

        return result.substring(0, result.length() - 1);
    }

    /**
     * This method builds initial Regular expressions from an FSA (R^-1).
     *
     * @return string containing initial regular expressions
     */
    private String[][] getInitRegex() {
        String[][] initRegex = new String[states.size()][states.size()];

        for (int j = 0; j < states.size(); j++)     // find the indexes of final states
            if (finStates.contains(states.get(j)))
                finIndexes.add(j);

        for (int i = 0; i < states.size(); i++) {
            State state1 = states.get(i);

            if (state1 == initState)                // find the index of initial state
                initIndex = i;

            for (int j = 0; j < states.size(); j++) {
                State state2 = states.get(j);
                String reg = "";

                for (Transition trans : state1.getTrans())
                    if (trans.getTarget() == state2)
                        reg = reg.concat(trans.getLetter() + "|");

                if (state1 == state2)
                    reg = reg.concat("eps");
                if (reg.isEmpty())
                    reg = "{}";
                if (reg.charAt(reg.length() - 1) == '|')
                    reg = reg.substring(0, reg.length() - 1);

                initRegex[i][j] = reg;
            }
        }

        return initRegex;
    }
}
