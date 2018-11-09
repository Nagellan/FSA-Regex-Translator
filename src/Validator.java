import javafx.util.Pair;
import java.util.*;

/**
 * This class represents an FSA. It checks the FSA and returns an error if exists.
 *
 * @author Irek Nazmiev <i.nazmiev@innopolis.ru> B17-05, Innopolis University
 */
public class Validator {
    public Validator(Scanner in) {
        this.result = new String[11];                       // result of validation
        Arrays.fill(this.result, "");                   // setting default values to the result array

        HashMap fileData = formatInFile(in);

        this.states = (LinkedList<State>) fileData.get("states");
        this.alpha = (LinkedList<String>) fileData.get("alpha");
        this.initState = (State) fileData.get("initState");
        this.finStates = (LinkedList<State>) fileData.get("finState");
        this.trans = (LinkedList<LinkedList<State>>) fileData.get("trans");
    }

    private LinkedList<State> states;               // set of states of FSA
    private LinkedList<String> alpha;               // alphabet of FST
    private State initState;                        // initial state of FST
    private LinkedList<State> finStates;            // final states of FST
    private LinkedList<LinkedList<State>> trans;    // transitions of FST
    private String[] result;                        // result of validation

    /**
     * This method formats the input file data into the convenient format of Linked Lists and necessary objects.
     * In addition, it also checks on an Error 5.
     *
     * @param in - input file with data to format
     * @return HashMap with formatted data
     */
    private HashMap formatInFile(Scanner in) {
        HashMap fileData = new HashMap();

        String statesStr = in.next();           // read lines from the input file
        String alphaStr = in.next();
        String initStateStr = in.next();
        String finStateStr = in.next();
        String transStr = in.next();

        if (fileIsMalformed(statesStr, alphaStr, initStateStr, finStateStr, transStr)) {
            result[5] = "E5: Input file is malformed";      // E5 check and force function's break
            return fileData;
        }

        LinkedList<State> states = formatStates(statesStr);     // reformat lines
        LinkedList<String> alpha = formatAlpha(alphaStr);
        String initStateName = initStateStr.substring(9, initStateStr.length() - 1);
        LinkedList<State> finStateName = formatFinStates(finStateStr, states);
        LinkedList<LinkedList<State>> trans = formatTrans(transStr, states, alpha);

        fileData.put("states", states);
        fileData.put("alpha", alpha);
        fileData.put("initState", findByName(initStateName, states));
        fileData.put("finState", finStateName);
        fileData.put("trans", trans);

        return fileData;
    }

    /**
     * This method checks whether input file is malformed or not.
     *
     * @param statesStr - 1st line of in file
     * @param alphaStr - 2nd line of in file
     * @param initStateStr - 3rd line of in file
     * @param finStateStr - 4th line of in file
     * @param transStr - 5th line of on file
     * @return boolean expression answering the given in method's name question
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
     * linked list of letters (words).
     *
     * @param alphaStr - 2nd line of input file with alphabet's description
     * @return LinkedList of Strings (words, letters)
     */
    private LinkedList<String> formatAlpha(String alphaStr) {
        LinkedList<String> resAlpha = new LinkedList<>();
        String[] alpha = alphaStr.substring(7, alphaStr.length() - 1).split(",");

        resAlpha.addAll(Arrays.asList(alpha));

        return resAlpha;
    }

    /**
     * This method casts the given string with final states' description (4th line of input file) to a convenient format -
     * linked list of final States.
     *
     * @param finStateStr - 4th line of input file with final states' description
     * @param states - linked list of states
     * @return Linked List of final states
     */
    private LinkedList<State> formatFinStates(String finStateStr, LinkedList<State> states) {
        LinkedList<State> resFinStates = new LinkedList<>();
        String[] finStates = finStateStr.substring(8, finStateStr.length() - 1).split(",");

        for (String finState : finStates)
            resFinStates.add(findByName(finState, states));

        return resFinStates;
    }

    /**
     * This method casts the given string with transition's description (5th line of input file) to a convenient format -
     * linked list containing a list of 2 states - connected by this transition.
     * Both these states store a transition to another states.
     *
     * @param transStr - 5th line of input file with transition's description
     * @param states - linked list of states
     * @param alpha - linked list of letters of an alphabet of FSA
     * @return LinkedList of pairs: connected first and second states
     */
    private LinkedList<LinkedList<State>> formatTrans(String transStr,
                                                      LinkedList<State> states, LinkedList<String> alpha) {
        LinkedList<LinkedList<State>> resTrans = new LinkedList<>();
        String[] trans = transStr.substring(7, transStr.length() - 1).split(",");
        String excessTrans = "";

        for (String transitionStr : trans) {
            String[] transSep = transitionStr.split(">");
            State state1 = findByName(transSep[0], states);
            State state2 = findByName(transSep[2], states);

            if (!alpha.contains(transSep[1]))
                excessTrans = transSep[1];

            if (state1 != null)
                state1.addTrans(transSep[1], state2);

            resTrans.add(new LinkedList(Arrays.asList(state1, state2)));
        }
        if (!excessTrans.equals(""))    // E3 check
            result[3] = "E3: A transition '" + excessTrans + "' is not represented in the alphabet\n";

        return resTrans;
    }

    /**
     * This method finds and returns state in State list by the name. It also checks Error 1.
     *
     * @param name - name of state to find
     * @param states - linked list of states
     * @return state with given name
     */
    private State findByName(String name, LinkedList<State> states) {
        for (State state : states) {
            if (state.getName().equals(name))
                return state;
        }
        if (!name.equals("")) {   // E1 check
            result[1] = "E1: A state '" + name + "' is not in set of states\n";
            states.add(new State(name));
        }

        return null;
    }

    /**
     * This method starts the validation of FSA and returns its result.
     * It checks the E2, E4, W1, W2, W3, completeness.
     *
     * @return String with the result of validation
     */
    public String start() {
         if (!result[5].equals(""))                           // if there's E5, just return message with only this error
            return result[5];

        if (initState == null)                                //E4 check
            result[4] = "E4: Initial state is not defined\n";

        if (!result[1].equals("") || !result[3].equals("") || !result[4].equals(""))
            result[0] = "Error:\n";                           // add Error label if needed

        if (result[0].equals("")) {                           // if there's no error, check Warnings and completeness
            if (fsaIsComplete())
                result[6] = "FSA is complete\n";              // check the completeness
            else
                result[6] = "FSA is incomplete\n";

            if (finStates.size() == 1 & finStates.get(0) == null)                             // check W1
                result[8] = "W1: Accepting state is not defined\n";

            if (initState != null) {                          // check W2
                LinkedList<State> reachedStatesW = getReachableStatesFrom(initState, states, new LinkedList<>());
                if (reachedStatesW.size() != states.size())
                    result[9] = "W2: Some states are not reachable from initial state\n";
            }

            if (fsaIsNondeterministic())                      // check W3
                result[10] = "W3: FSA is nondeterministic\n";

            if (!result[8].equals("") || !result[9].equals("") || !result[10].equals(""))
                result[7] = "Warning:\n";                     // add Warning label if needed
        }

        LinkedList<State> undirectedStates = makeUndirected((LinkedList<State>) states.clone());
        LinkedList<State> reachedStates = getReachableStatesFrom(states.get(0), undirectedStates, new LinkedList<>());
        if (reachedStates.size() != states.size()) {
            result[2] = "E2: Some states are disjoint\n";     // check E2
            result[0] = "Error:\n";
            result = Arrays.copyOfRange(result, 0, 4);
        }

        return arrayToStr(result);
    }

    /**
     * This function transforms array of strings into the string
     *
     * @param result - array of strings
     * @return concatenation of all strings consisting in 'result' array
     */
    private String arrayToStr(String[] result) {
        String res = "";

        for (String str : result)
            res += str;

        return res.substring(0, res.length() - 1);  // return the string without last line break
    }

    /**
     * This method checks FSA on completeness.
     *
     * @return boolean expression answering the given in method's name question
     */
    private boolean fsaIsComplete() {
        for (State state : states) {
            LinkedList<String> localAlpha = new LinkedList<>();

            for (Pair<String, State> trans : state.getTrans())
                if (!localAlpha.contains(trans.getKey()))
                    localAlpha.add(trans.getKey());

            if (localAlpha.size() != alpha.size())
                return false;
        }

        return true;
    }

    /**
     * This method checks whether FSA is nondeterministic or not.
     *
     * @return boolean expression answering the given in method's name question
     */
    private boolean fsaIsNondeterministic() {
        for (State state : states) {
            LinkedList<String> localAlpha = new LinkedList<>();

            for (Pair<String, State> trans : state.getTrans()) {
                if (localAlpha.contains(trans.getKey()))
                    return true;
                localAlpha.add(trans.getKey());
            }
        }

        return false;
    }

    /**
     * This function performs recursively Depth First Search in the States graph from the given State.
     * The search allows to figure out whether all states can be reached from the given one or not.
     *
     * @param state - starting state of search
     * @param result - LinkedList of all reached states
     * @return - result (see above)
     */
    private LinkedList<State> getReachableStatesFrom(State state, LinkedList<State> states, LinkedList<State> result) {
        result.add(state);
        for (Pair<String, State> trans : state.getTrans())
            if (!result.contains(trans.getValue()))
                result = getReachableStatesFrom(trans.getValue(), states, result);
        return result;
    }

    /**
     * This method makes the graph undirected (LinkedList of states).
     *
     * @param states - directed graph (LinkedList of states)
     * @return undirected version of given directed graph
     */
    private LinkedList<State> makeUndirected(LinkedList<State> states) {
        for (State state : states)
            for (Pair<String, State> trans : state.getTrans())
                if (!trans.getValue().getTrans().contains(new Pair<>(trans.getKey(), state)))
                    trans.getValue().addTrans(trans.getKey(), state);
        return states;
    }
}