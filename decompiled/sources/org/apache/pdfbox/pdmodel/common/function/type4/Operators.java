package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/Operators.class */
public class Operators {
    private static final Operator ABS = new ArithmeticOperators.Abs();
    private static final Operator ADD = new ArithmeticOperators.Add();
    private static final Operator ATAN = new ArithmeticOperators.Atan();
    private static final Operator CEILING = new ArithmeticOperators.Ceiling();
    private static final Operator COS = new ArithmeticOperators.Cos();
    private static final Operator CVI = new ArithmeticOperators.Cvi();
    private static final Operator CVR = new ArithmeticOperators.Cvr();
    private static final Operator DIV = new ArithmeticOperators.Div();
    private static final Operator EXP = new ArithmeticOperators.Exp();
    private static final Operator FLOOR = new ArithmeticOperators.Floor();
    private static final Operator IDIV = new ArithmeticOperators.IDiv();
    private static final Operator LN = new ArithmeticOperators.Ln();
    private static final Operator LOG = new ArithmeticOperators.Log();
    private static final Operator MOD = new ArithmeticOperators.Mod();
    private static final Operator MUL = new ArithmeticOperators.Mul();
    private static final Operator NEG = new ArithmeticOperators.Neg();
    private static final Operator ROUND = new ArithmeticOperators.Round();
    private static final Operator SIN = new ArithmeticOperators.Sin();
    private static final Operator SQRT = new ArithmeticOperators.Sqrt();
    private static final Operator SUB = new ArithmeticOperators.Sub();
    private static final Operator TRUNCATE = new ArithmeticOperators.Truncate();
    private static final Operator AND = new BitwiseOperators.And();
    private static final Operator BITSHIFT = new BitwiseOperators.Bitshift();
    private static final Operator EQ = new RelationalOperators.Eq();
    private static final Operator FALSE = new BitwiseOperators.False();
    private static final Operator GE = new RelationalOperators.Ge();
    private static final Operator GT = new RelationalOperators.Gt();
    private static final Operator LE = new RelationalOperators.Le();
    private static final Operator LT = new RelationalOperators.Lt();
    private static final Operator NE = new RelationalOperators.Ne();
    private static final Operator NOT = new BitwiseOperators.Not();
    private static final Operator OR = new BitwiseOperators.Or();
    private static final Operator TRUE = new BitwiseOperators.True();
    private static final Operator XOR = new BitwiseOperators.Xor();
    private static final Operator IF = new ConditionalOperators.If();
    private static final Operator IFELSE = new ConditionalOperators.IfElse();
    private static final Operator COPY = new StackOperators.Copy();
    private static final Operator DUP = new StackOperators.Dup();
    private static final Operator EXCH = new StackOperators.Exch();
    private static final Operator INDEX = new StackOperators.Index();
    private static final Operator POP = new StackOperators.Pop();
    private static final Operator ROLL = new StackOperators.Roll();
    private final Map<String, Operator> operators = new HashMap();

    public Operators() {
        this.operators.put("add", ADD);
        this.operators.put("abs", ABS);
        this.operators.put("atan", ATAN);
        this.operators.put("ceiling", CEILING);
        this.operators.put("cos", COS);
        this.operators.put("cvi", CVI);
        this.operators.put("cvr", CVR);
        this.operators.put("div", DIV);
        this.operators.put("exp", EXP);
        this.operators.put("floor", FLOOR);
        this.operators.put("idiv", IDIV);
        this.operators.put("ln", LN);
        this.operators.put("log", LOG);
        this.operators.put("mod", MOD);
        this.operators.put("mul", MUL);
        this.operators.put("neg", NEG);
        this.operators.put("round", ROUND);
        this.operators.put("sin", SIN);
        this.operators.put("sqrt", SQRT);
        this.operators.put("sub", SUB);
        this.operators.put("truncate", TRUNCATE);
        this.operators.put("and", AND);
        this.operators.put("bitshift", BITSHIFT);
        this.operators.put("eq", EQ);
        this.operators.put("false", FALSE);
        this.operators.put("ge", GE);
        this.operators.put("gt", GT);
        this.operators.put("le", LE);
        this.operators.put("lt", LT);
        this.operators.put("ne", NE);
        this.operators.put("not", NOT);
        this.operators.put("or", OR);
        this.operators.put("true", TRUE);
        this.operators.put("xor", XOR);
        this.operators.put("if", IF);
        this.operators.put("ifelse", IFELSE);
        this.operators.put("copy", COPY);
        this.operators.put("dup", DUP);
        this.operators.put("exch", EXCH);
        this.operators.put("index", INDEX);
        this.operators.put("pop", POP);
        this.operators.put("roll", ROLL);
    }

    public Operator getOperator(String operatorName) {
        return this.operators.get(operatorName);
    }
}
