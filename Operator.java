/**
 * Created by Arga on 11.04.2015.
 */
public enum Operator {
    A_OVER_B (new Alpha_Blending()),
    A_IN_B (new A_in_B()),
    A_OUT_B (new A_out_B()),
    A_ATOP_B (new A_atop_B()),
    A_XOR_B (new A_xor_B());

    private Alpha_Blending operator;

    Operator(Alpha_Blending operator){
        this.operator = operator;
    }

    public Alpha_Blending get() {
        return operator;
    }
}
