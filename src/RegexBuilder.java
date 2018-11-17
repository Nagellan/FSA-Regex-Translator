import java.util.LinkedList;

public class RegexBuilder {
    public RegexBuilder(LinkedList<State> states) {
        this.states = states;
    }

    private LinkedList<State> states;

    public String buildRegex() {
        LinkedList<String> initRegex = getInitRegex();

        return "";
    }

    private LinkedList<String> getInitRegex() {
        LinkedList<String> initRegex = new LinkedList<>();
        State state1, state2;

        for (int i = 0; i < states.size(); i++)
            for (int j = 0; j < states.size(); j++) {
                state1 = states.get(i);
                state2 = states.get(j);

//                if (state1.getTrans().contains())
//                    System.out.println("KIK");

//                System.out.print(state1.getName() + " > ");
//                System.out.print(state2.getName());
//                System.out.println(state1.connectedWith(state2));
            }


        return initRegex;
    }
}
