/**
 * Created by Arga on 11.04.2015.
 */
public enum Operator {
    A_OVER_B (new Alpha_Blending()),
    A_IN_B (new AInB()),
    A_OUT_B (new AOutB()),
    A_ATOP_B (new AAtopB()),
    A_XOR_B (new AXorB());

    private Alpha_Blending operator;

    Operator(Alpha_Blending operator){
        this.operator = operator;
    }

    public Alpha_Blending get() {
        return operator;
    }
}
