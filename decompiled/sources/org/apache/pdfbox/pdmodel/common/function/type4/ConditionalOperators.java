package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ConditionalOperators.class */
class ConditionalOperators {
    private ConditionalOperators() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ConditionalOperators$If.class */
    static class If implements Operator {
        If() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            InstructionSequence proc = (InstructionSequence) stack.pop();
            Boolean condition = (Boolean) stack.pop();
            if (condition.booleanValue()) {
                proc.execute(context);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/ConditionalOperators$IfElse.class */
    static class IfElse implements Operator {
        IfElse() {
        }

        @Override // org.apache.pdfbox.pdmodel.common.function.type4.Operator
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            InstructionSequence proc2 = (InstructionSequence) stack.pop();
            InstructionSequence proc1 = (InstructionSequence) stack.pop();
            Boolean condition = (Boolean) stack.pop();
            if (condition.booleanValue()) {
                proc1.execute(context);
            } else {
                proc2.execute(context);
            }
        }
    }
}
