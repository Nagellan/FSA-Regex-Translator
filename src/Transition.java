public class Transition {
    public Transition(String letter, State source, State target) {
        this.letter = letter;
        this.source = source;
        this.target = target;
    }

    public Transition(String letter, State target) {
        this.letter = letter;
        this.source = null;
        this.target = target;
    }

    private String letter;
    private State source;
    private State target;

    public String getLetter() {
        return letter;
    }

    public State getSource() {
        return source;
    }

    public State getTarget() {
        return target;
    }
}
