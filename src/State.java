import java.util.LinkedList;

/**
 * This class represents a state in FSA.
 * Each state has name and LinkedList of transitions that are adjacent to it.
 *
 * @author Irek Nazmiev <i.nazmiev@innopolis.ru> B17-05, Innopolis University
 */
public class State {
    State(String name) {
        this.name = name;
        this.trans = new LinkedList<>();
    }

    private String name;                       // name of a state
    private LinkedList<Transition> trans;      // transitions of state

    /**
     * This is a getter method for 'name' variable.
     *
     * @return value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * This method adds a new transition to the state (add a new element to the 'trans' list).
     *
     * @param letter - name of transition
     * @param state - state which is connected to the current state by the given transition
     */
    public void addTrans(String letter, State state) {
        trans.add(new Transition(letter, state));
    }

    /**
     * This is a getter for 'trans' list.
     *
     * @return LinkedList of Transitions
     */
    public LinkedList<Transition> getTrans() {
        return trans;
    }
}
