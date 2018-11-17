/**
 * This class represents transitions between states in FSA.
 * Each transition has its name, source and target States connected by it.
 *
 * @author Irek Nazmiev <i.nazmiev@innopolis.ru> B17-05, Innopolis University
 */
public class Transition {
    Transition(String letter, State source, State target) {
        this.letter = letter;
        this.source = source;
        this.target = target;
    }

    Transition(String letter, State target) {       // constructor for transitions inside the States' transition lists
        this.letter = letter;
        this.source = null;
        this.target = target;
    }

    private String letter;      // name of the transition
    private State source;       // source State connected by the current transition
    private State target;       // target State connected by the current transition

    /**
     * This is a getter for transitions's name.
     *
     * @return transitions's name
     */
    public String getLetter() {
        return letter;
    }

    /**
     * This is a getter for source State.
     *
     * @return source state
     */
    public State getSource() {
        return source;
    }

    /**
     * This is a getter for target State.
     *
     * @return target State
     */
    public State getTarget() {
        return target;
    }
}
