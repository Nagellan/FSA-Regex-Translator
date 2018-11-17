import java.io.PrintWriter;
import java.util.*;

/**
 * This class represents an FSA. It checks the FSA and returns an error if exists.
 *
 * @author Irek Nazmiev <i.nazmiev@innopolis.ru> B17-05, Innopolis University
 */
public class Validator {
    public Validator(Scanner in, PrintWriter out) {
        this.out = out;

        HashMap<String, Object> fileData = formatInFile(in);

        this.states = (LinkedList<State>) fileData.get("states");
        this.alpha = (LinkedList<String>) fileData.get("alpha");
        this.initState = (State) fileData.get("initState");
        this.finStates = (LinkedList<State>) fileData.get("finState");
        this.trans = (LinkedList<Transition>) fileData.get("trans");
    }

    private PrintWriter out;
    private LinkedList<State> states;               // set of states of FSA
    private LinkedList<String> alpha;               // alphabet of FST
    private State initState;                        // initial state of FST
    private LinkedList<State> finStates;            // final states of FST
    private LinkedList<Transition> trans;           // transitions of FST

    /**
     * This method prints error message to the output file and immediately terminates the program.
     *
     * @param error - error message
     */
    private void reportError(String error) {
        out.print(error);
        out.close();
        System.exit(0);
    }

    /**
     * This method formats the input file data into the convenient format.
     * In addition, it also checks an Error 5.
     *
     * @param in - input file with data to format
     * @return HashMap with formatted data
     */
    private HashMap<String, Object> formatInFile(Scanner in) {
        HashMap<String, Object> fileData = new HashMap<>();

        String statesStr = in.nextLine();               // read lines from the input file
        String alphaStr = in.nextLine();
        String initStateStr = in.nextLine();
        String finStateStr = in.nextLine();
        String transStr = in.nextLine();

        if (fileIsMalformed(statesStr, alphaStr, initStateStr, finStateStr, transStr))      // E5 check
            reportError("Error:\nE5: Input file is malformed");

        LinkedList<State> states = formatStates(statesStr);                                 // reformat lines
        LinkedList<String> alpha = formatAlpha(alphaStr);
        State initState = formatInitState(initStateStr, states);
        LinkedList<State> finStates = formatFinStates(finStateStr, states);
        LinkedList<Transition> trans = formatTrans(transStr, states, alpha);

        fileData.put("states", states);
        fileData.put("alpha", alpha);
        fileData.put("initState", initState);
        fileData.put("finState", finStates);
        fileData.put("trans", trans);

        return fileData;
    }

    /**
     * This method checks whether input file is malformed or not.
     *
     * @param statesStr - 1st line of input file
     * @param alphaStr - 2nd line of input file
     * @param initStateStr - 3rd line of input file
     * @param finStateStr - 4th line of input file
     * @param transStr - 5th line of input file
     * @return boolean expression answering the question
     */
    private boolean fileIsMalformed(String statesStr, String alphaStr,
                                    String initStateStr, String finStateStr, String transStr) {
        return !(statesStr.substring(0, 8).equals("states={") & statesStr.charAt(statesStr.length() - 1) == '}'
                & alphaStr.substring(0, 7).equals("alpha={") & alphaStr.charAt(alphaStr.length() - 1) == '}'
                & initStateStr.substring(0, 9).equals("init.st={") & initStateStr.charAt(initStateStr.length() - 1) == '}'
                & finStateStr.substring(0, 8).equals("fin.st={") & finStateStr.charAt(finStateStr.length() - 1) == '}'
                & transStr.substring(0, 7).equals("trans={") & transStr.charAt(transStr.length() - 1) == '}');
    }

    /**
     * This method casts the given string with states' description (1st line of input file) to a convenient format -
     * linked list of States.
     *
     * @param statesStr - 1st line of input file with states' description
     * @return LinkedList of States
     */
    private LinkedList<State> formatStates(String statesStr) {
        LinkedList<State> resStates = new LinkedList<>();
        String[] states = statesStr.substring(8, statesStr.length() - 1).split(",");

        for (String stateName : states)
            resStates.add(new State(stateName));

        return resStates;
    }

    /**
     * This method casts the given string with alphabet's description (2nd line of input file) to a convenient format -
     * linked list of Strings.
     *
     * @param alphaStr - 2nd line of input file with alphabet's description
     * @return LinkedList of Strings
     */
    private LinkedList<String> formatAlpha(String alphaStr) {
        LinkedList<String> resAlpha = new LinkedList<>();
        String[] alpha = alphaStr.substring(7, alphaStr.length() - 1).split(",");

        resAlpha.addAll(Arrays.asList(alpha));

        return resAlpha;
    }

    /**
     * This method casts the given string with initial state's description (3rd line of input file) to a convenient format -
     * linked list of initial States.
     *
     * @param initStateStr - 3rd line of input file with initial state's description
     * @param states - LinkedList of FSA's States
     * @return initial State
     */
    private State formatInitState(String initStateStr, LinkedList<State> states) {
        String initStateName = initStateStr.substring(9, initStateStr.length() - 1);

        return findByName(initStateName, states);
    }

    /**
     * This method casts the given string with final states' description (4th line of input file) to a convenient format -
     * linked list of final States.
     *
     * @param finStateStr - 4th line of input file with final states' description
     * @param states - LinkedList of FSA's States
     * @return LinkedList of final states
     */
    private LinkedList<State> formatFinStates(String finStateStr, LinkedList<State> states) {
        LinkedList<State> resFinStates = new LinkedList<>();
        String[] finStates = finStateStr.substring(8, finStateStr.length() - 1).split(",");

        for (String finState : finStates)
            if (!finState.equals(""))
                resFinStates.add(findByName(finState, states));

        return resFinStates;
    }

    /**
     * This method casts the given string with transitions' description (5th line of input file) to a convenient format -
     * linked list of Transitions. Moreover, it connects all FSA's states by this transitions.
     * It also checks Error 3.
     *
     * @param transStr - 5th line of input file with transition's description
     * @param states - LinkedList of FSA's States
     * @param alpha - LinkedList with alphabet
     * @return LinkedList of pairs: connected first and second states
     */
    private LinkedList<Transition> formatTrans(String transStr, LinkedList<State> states, LinkedList<String> alpha) {
        LinkedList<Transition> resTrans = new LinkedList<>();
        String[] trans = transStr.substring(7, transStr.length() - 1).split(",");

        for (String transitionStr : trans) {
            String[] transSep = transitionStr.split(">");
            State state1 = findByName(transSep[0], states);
            State state2 = findByName(transSep[2], states);

            if (!alpha.contains(transSep[1]))                                                        // E3 check
                reportError("Error:\nE3: A transition '" + transSep[1] + "' is not represented in the alphabet");

            if (state1 != null & state2 != null)
                state1.addTrans(transSep[1], state2);

            resTrans.add(new Transition(transSep[1], state1, state2));
        }

        return resTrans;
    }

    /**
     * This method searches the state with given name in a States list and then returns it.
     * It also checks Error 1.
     *
     * @param name - name of state to find
     * @param states - LinkedList of FSA's states
     * @return state with given name
     */
    private State findByName(String name, LinkedList<State> states) {
        for (State state : states)
            if (state.getName().equals(name))
                return state;

        reportError("Error:\nE1: A state '" + name + "' is not in set of states");     // E1 check

        return null;
    }

    /**
     * This method starts the validation of FSA.
     * It checks the E2, E4, E6.
     */
    public void start() {
        if (initState == null)
            reportError("Error:\nE4: Initial state is not defined");      //E4 check

        LinkedList<State> undirectedStates = makeUndirected(deepClone(states));
        LinkedList<State> reachedStates = getReachableStatesFrom(undirectedStates.get(0), new LinkedList<>());

        if (reachedStates.size() != states.size())
            reportError("Error:\nE2: Some states are disjoint");                // check E2

        if (fsaIsNondeterministic())                                        // check E6
            reportError("Error:\nE6: FSA is nondeterministic");

        if (finStates.size() == 0)
            reportError("{}");
    }

    /**
     * This method performs deep cloning of LinkedList of states.
     *
     * @param states - LinkedList to be cloned
     * @return deeply cloned LinkedList
     */
    private LinkedList<State> deepClone(LinkedList<State> states) {
        LinkedList<State> newStates = new LinkedList<>();

        for (State state : states)                                            // duplicate states
            newStates.add(new State(state.getName()));

        for (int i = 0; i < states.size(); i++)                               // duplicate transitions
            for (Transition trans : states.get(i).getTrans()) {
                State state = findByName(trans.getTarget().getName(), newStates);
                newStates.get(i).addTrans(trans.getLetter(), state);
            }

        return newStates;
    }

    /**
     * This method makes the graph undirected (LinkedList of states).
     *
     * @param states - directed graph (LinkedList of states)
     * @return undirected version of given directed graph
     */
    private LinkedList<State> makeUndirected(LinkedList<State> states) {
        for (State state : states)
            for (Transition trans : state.getTrans())
                if (state != trans.getTarget() & !trans.getTarget().getTrans().contains(new Transition(trans.getLetter(), state)))
                    trans.getTarget().addTrans(trans.getLetter(), state);
        return states;
    }

    /**
     * This function performs recursively Depth First Search in the States graph from the given State.
     * The search allows to figure out whether all states can be reached from the given one or not.
     *
     * @param state - starting state of search
     * @param result - LinkedList of all reached states
     * @return - result (see above)
     */
    private LinkedList<State> getReachableStatesFrom(State state, LinkedList<State> result) {
        result.add(state);
        for (Transition trans : state.getTrans())
            if (!result.contains(trans.getTarget()))
                result = getReachableStatesFrom(trans.getTarget(), result);
        return result;
    }

    /**
     * This method checks whether FSA is nondeterministic or not.
     *
     * @return boolean expression answering the given in method's name question
     */
    private boolean fsaIsNondeterministic() {
        LinkedList<Transition> localTrans = new LinkedList<>();

        for (Transition trn : trans) {
            if (localTrans.contains(trn))
                return true;
            localTrans.add(trn);
        }

        return false;
    }
}