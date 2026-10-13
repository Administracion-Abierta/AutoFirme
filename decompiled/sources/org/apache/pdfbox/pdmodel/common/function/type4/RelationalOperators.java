package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators.class */
class RelationalOperators {
    private RelationalOperators() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators$Eq.class */
    static class Eq implements Operator {
        Eq() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op2 = stack.pop();
            Object op1 = stack.pop();
            boolean result = isEqual(op1, op2);
            stack.push(Boolean.valueOf(result));
        }

        protected boolean isEqual(Object op1, Object op2) {
            boolean result;
            if ((op1 instanceof Number) && (op2 instanceof Number)) {
                Number num1 = (Number) op1;
                Number num2 = (Number) op2;
                result = num1.floatValue() == num2.floatValue();
            } else {
                result = op1.equals(op2);
            }
            return result;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators$AbstractNumberComparisonOperator.class */
    private static abstract class AbstractNumberComparisonOperator implements Operator {
        protected abstract boolean compare(Number number, Number number2);

        private AbstractNumberComparisonOperator() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op2 = stack.pop();
            Object op1 = stack.pop();
            Number num1 = (Number) op1;
            Number num2 = (Number) op2;
            boolean result = compare(num1, num2);
            stack.push(Boolean.valueOf(result));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators$Ge.class */
    static class Ge extends AbstractNumberComparisonOperator {
        Ge() {
            super();
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.RelationalOperators.AbstractNumberComparisonOperator
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() >= num2.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators$Gt.class */
    static class Gt extends AbstractNumberComparisonOperator {
        Gt() {
            super();
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.RelationalOperators.AbstractNumberComparisonOperator
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() > num2.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators$Le.class */
    static class Le extends AbstractNumberComparisonOperator {
        Le() {
            super();
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.RelationalOperators.AbstractNumberComparisonOperator
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() <= num2.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators$Lt.class */
    static class Lt extends AbstractNumberComparisonOperator {
        Lt() {
            super();
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.RelationalOperators.AbstractNumberComparisonOperator
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() < num2.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/RelationalOperators$Ne.class */
    static class Ne extends Eq {
        Ne() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.RelationalOperators.Eq
        protected boolean isEqual(Object op1, Object op2) {
            boolean result = super.isEqual(op1, op2);
            return !result;
        }
    }
}
