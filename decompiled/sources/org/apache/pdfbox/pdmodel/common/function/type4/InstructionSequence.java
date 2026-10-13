package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/InstructionSequence.class */
public class InstructionSequence {
    private final List<Object> instructions = new ArrayList();

    public void addName(String name) {
        this.instructions.add(name);
    }

    public void addInteger(int value) {
        this.instructions.add(Integer.valueOf(value));
    }

    public void addReal(float value) {
        this.instructions.add(Float.valueOf(value));
    }

    public void addBoolean(boolean value) {
        this.instructions.add(Boolean.valueOf(value));
    }

    public void addProc(InstructionSequence child) {
        this.instructions.add(child);
    }

    public void execute(ExecutionContext context) {
        Stack<Object> stack = context.getStack();
        for (Object o : this.instructions) {
            if (o instanceof String) {
                String name = (String) o;
                Operator cmd = context.getOperators().getOperator(name);
                if (cmd != null) {
                    cmd.execute(context);
                } else {
                    throw new UnsupportedOperationException("Unknown operator or name: " + name);
                }
            } else {
                stack.push(o);
            }
        }
        while (!stack.isEmpty() && (stack.peek() instanceof InstructionSequence)) {
            InstructionSequence nested = (InstructionSequence) stack.pop();
            nested.execute(context);
        }
    }
}
