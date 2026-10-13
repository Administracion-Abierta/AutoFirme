package org.apache.pdfbox.pdmodel.common.function.type4;

import com.aowagie.text.pdf.ColumnText;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators.class */
class ArithmeticOperators {
    private ArithmeticOperators() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Abs.class */
    static class Abs implements Operator {
        Abs() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(Integer.valueOf(Math.abs(num.intValue())));
            } else {
                context.getStack().push(Float.valueOf(Math.abs(num.floatValue())));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Add.class */
    static class Add implements Operator {
        Add() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            if ((num1 instanceof Integer) && (num2 instanceof Integer)) {
                long sum = num1.longValue() + num2.longValue();
                if (sum < -2147483648L || sum > 2147483647L) {
                    context.getStack().push(Float.valueOf(sum));
                    return;
                } else {
                    context.getStack().push(Integer.valueOf((int) sum));
                    return;
                }
            }
            context.getStack().push(Float.valueOf(num1.floatValue() + num2.floatValue()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Atan.class */
    static class Atan implements Operator {
        Atan() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            float den = context.popReal();
            float num = context.popReal();
            float atan = ((float) Math.toDegrees((float) Math.atan2(num, den))) % 360.0f;
            if (atan < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                atan += 360.0f;
            }
            context.getStack().push(Float.valueOf(atan));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Ceiling.class */
    static class Ceiling implements Operator {
        Ceiling() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(num);
            } else {
                context.getStack().push(Float.valueOf((float) Math.ceil(num.doubleValue())));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Cos.class */
    static class Cos implements Operator {
        Cos() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            float angle = context.popReal();
            float cos = (float) Math.cos(Math.toRadians(angle));
            context.getStack().push(Float.valueOf(cos));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Cvi.class */
    static class Cvi implements Operator {
        Cvi() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(Integer.valueOf(num.intValue()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Cvr.class */
    static class Cvr implements Operator {
        Cvr() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(Float.valueOf(num.floatValue()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Div.class */
    static class Div implements Operator {
        Div() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            context.getStack().push(Float.valueOf(num1.floatValue() / num2.floatValue()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Exp.class */
    static class Exp implements Operator {
        Exp() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number exp = context.popNumber();
            Number base = context.popNumber();
            double value = Math.pow(base.doubleValue(), exp.doubleValue());
            context.getStack().push(Float.valueOf((float) value));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Floor.class */
    static class Floor implements Operator {
        Floor() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(num);
            } else {
                context.getStack().push(Float.valueOf((float) Math.floor(num.doubleValue())));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$IDiv.class */
    static class IDiv implements Operator {
        IDiv() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            int num2 = context.popInt();
            int num1 = context.popInt();
            context.getStack().push(Integer.valueOf(num1 / num2));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Ln.class */
    static class Ln implements Operator {
        Ln() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(Float.valueOf((float) Math.log(num.doubleValue())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Log.class */
    static class Log implements Operator {
        Log() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(Float.valueOf((float) Math.log10(num.doubleValue())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Mod.class */
    static class Mod implements Operator {
        Mod() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            int int2 = context.popInt();
            int int1 = context.popInt();
            context.getStack().push(Integer.valueOf(int1 % int2));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Mul.class */
    static class Mul implements Operator {
        Mul() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            if ((num1 instanceof Integer) && (num2 instanceof Integer)) {
                long result = num1.longValue() * num2.longValue();
                if (result >= -2147483648L && result <= 2147483647L) {
                    context.getStack().push(Integer.valueOf((int) result));
                    return;
                } else {
                    context.getStack().push(Float.valueOf(result));
                    return;
                }
            }
            context.getStack().push(Float.valueOf((float) (num1.doubleValue() * num2.doubleValue())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Neg.class */
    static class Neg implements Operator {
        Neg() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                int v = num.intValue();
                if (v == Integer.MIN_VALUE) {
                    context.getStack().push(Float.valueOf(-num.floatValue()));
                    return;
                } else {
                    context.getStack().push(Integer.valueOf(-num.intValue()));
                    return;
                }
            }
            context.getStack().push(Float.valueOf(-num.floatValue()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Round.class */
    static class Round implements Operator {
        Round() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(Integer.valueOf(num.intValue()));
            } else {
                context.getStack().push(Float.valueOf(Math.round(num.doubleValue())));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Sin.class */
    static class Sin implements Operator {
        Sin() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            float angle = context.popReal();
            float sin = (float) Math.sin(Math.toRadians(angle));
            context.getStack().push(Float.valueOf(sin));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Sqrt.class */
    static class Sqrt implements Operator {
        Sqrt() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            float num = context.popReal();
            if (num < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                throw new IllegalArgumentException("argument must be nonnegative");
            }
            context.getStack().push(Float.valueOf((float) Math.sqrt(num)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Sub.class */
    static class Sub implements Operator {
        Sub() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            if ((num1 instanceof Integer) && (num2 instanceof Integer)) {
                long result = num1.longValue() - num2.longValue();
                if (result < -2147483648L || result > 2147483647L) {
                    stack.push(Float.valueOf(result));
                    return;
                } else {
                    stack.push(Integer.valueOf((int) result));
                    return;
                }
            }
            stack.push(Float.valueOf(num1.floatValue() - num2.floatValue()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ArithmeticOperators$Truncate.class */
    static class Truncate implements Operator {
        Truncate() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(Integer.valueOf(num.intValue()));
            } else {
                context.getStack().push(Float.valueOf((int) num.floatValue()));
            }
        }
    }
}
