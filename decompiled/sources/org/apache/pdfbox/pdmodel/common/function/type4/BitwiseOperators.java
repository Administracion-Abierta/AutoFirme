package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators.class */
class BitwiseOperators {
    private BitwiseOperators() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$AbstractLogicalOperator.class */
    private static abstract class AbstractLogicalOperator implements Operator {
        protected abstract boolean applyForBoolean(boolean z, boolean z2);

        protected abstract int applyforInteger(int i, int i2);

        private AbstractLogicalOperator() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op2 = stack.pop();
            Object op1 = stack.pop();
            if ((op1 instanceof Boolean) && (op2 instanceof Boolean)) {
                boolean bool1 = ((Boolean) op1).booleanValue();
                boolean bool2 = ((Boolean) op2).booleanValue();
                boolean result = applyForBoolean(bool1, bool2);
                stack.push(Boolean.valueOf(result));
                return;
            }
            if ((op1 instanceof Integer) && (op2 instanceof Integer)) {
                int int1 = ((Integer) op1).intValue();
                int int2 = ((Integer) op2).intValue();
                int result2 = applyforInteger(int1, int2);
                stack.push(Integer.valueOf(result2));
                return;
            }
            throw new ClassCastException("Operands must be bool/bool or int/int");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$And.class */
    static class And extends AbstractLogicalOperator {
        And() {
            super();
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.BitwiseOperators.AbstractLogicalOperator
        protected boolean applyForBoolean(boolean bool1, boolean bool2) {
            return bool1 && bool2;
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.BitwiseOperators.AbstractLogicalOperator
        protected int applyforInteger(int int1, int int2) {
            return int1 & int2;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$Bitshift.class */
    static class Bitshift implements Operator {
        Bitshift() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int shift = ((Integer) stack.pop()).intValue();
            int int1 = ((Integer) stack.pop()).intValue();
            if (shift < 0) {
                int result = int1 >> Math.abs(shift);
                stack.push(Integer.valueOf(result));
            } else {
                int result2 = int1 << shift;
                stack.push(Integer.valueOf(result2));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$False.class */
    static class False implements Operator {
        False() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.push(Boolean.FALSE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$Not.class */
    static class Not implements Operator {
        Not() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op1 = stack.pop();
            if (op1 instanceof Boolean) {
                boolean bool1 = ((Boolean) op1).booleanValue();
                boolean result = !bool1;
                stack.push(Boolean.valueOf(result));
            } else {
                if (op1 instanceof Integer) {
                    int int1 = ((Integer) op1).intValue();
                    int result2 = -int1;
                    stack.push(Integer.valueOf(result2));
                    return;
                }
                throw new ClassCastException("Operand must be bool or int");
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$Or.class */
    static class Or extends AbstractLogicalOperator {
        Or() {
            super();
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.BitwiseOperators.AbstractLogicalOperator
        protected boolean applyForBoolean(boolean bool1, boolean bool2) {
            return bool1 || bool2;
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.BitwiseOperators.AbstractLogicalOperator
        protected int applyforInteger(int int1, int int2) {
            return int1 | int2;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$True.class */
    static class True implements Operator {
        True() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.push(Boolean.TRUE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/BitwiseOperators$Xor.class */
    static class Xor extends AbstractLogicalOperator {
        Xor() {
            super();
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.BitwiseOperators.AbstractLogicalOperator
        protected boolean applyForBoolean(boolean bool1, boolean bool2) {
            return bool1 ^ bool2;
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.BitwiseOperators.AbstractLogicalOperator
        protected int applyforInteger(int int1, int int2) {
            return int1 ^ int2;
        }
    }
}
