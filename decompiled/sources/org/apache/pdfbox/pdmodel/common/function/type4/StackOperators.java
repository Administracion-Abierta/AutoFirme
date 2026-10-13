package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/StackOperators.class */
class StackOperators {
    private StackOperators() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/StackOperators$Copy.class */
    static class Copy implements Operator {
        Copy() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int n = ((Number) stack.pop()).intValue();
            if (n > 0) {
                int size = stack.size();
                List<Object> copy = new ArrayList<>(stack.subList(size - n, size));
                stack.addAll(copy);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/StackOperators$Dup.class */
    static class Dup implements Operator {
        Dup() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.push(stack.peek());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/StackOperators$Exch.class */
    static class Exch implements Operator {
        Exch() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object any2 = stack.pop();
            Object any1 = stack.pop();
            stack.push(any2);
            stack.push(any1);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/StackOperators$Index.class */
    static class Index implements Operator {
        Index() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int n = ((Number) stack.pop()).intValue();
            if (n < 0) {
                throw new IllegalArgumentException("rangecheck: " + n);
            }
            int size = stack.size();
            stack.push(stack.get((size - n) - 1));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/StackOperators$Pop.class */
    static class Pop implements Operator {
        Pop() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.pop();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/StackOperators$Roll.class */
    static class Roll implements Operator {
        Roll() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int j = ((Number) stack.pop()).intValue();
            int n = ((Number) stack.pop()).intValue();
            if (j == 0) {
                return;
            }
            if (n < 0) {
                throw new IllegalArgumentException("rangecheck: " + n);
            }
            LinkedList<Object> rolled = new LinkedList<>();
            LinkedList<Object> moved = new LinkedList<>();
            if (j < 0) {
                int n1 = n + j;
                for (int i = 0; i < n1; i++) {
                    moved.addFirst(stack.pop());
                }
                for (int i2 = j; i2 < 0; i2++) {
                    rolled.addFirst(stack.pop());
                }
                stack.addAll(moved);
                stack.addAll(rolled);
                return;
            }
            int n2 = n - j;
            for (int i3 = j; i3 > 0; i3--) {
                rolled.addFirst(stack.pop());
            }
            for (int i4 = 0; i4 < n2; i4++) {
                moved.addFirst(stack.pop());
            }
            stack.addAll(rolled);
            stack.addAll(moved);
        }
    }
}
