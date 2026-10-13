package org.apache.fontbox.cff;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CharStringHandler.class */
public abstract class CharStringHandler {
    public abstract List<Number> handleCommand(List<Number> list, CharStringCommand charStringCommand);

    public List<Number> handleSequence(List<Object> sequence) {
        List<Number> numbers = new ArrayList<>();
        for (Object obj : sequence) {
            if (obj instanceof CharStringCommand) {
                List<Number> results = handleCommand(numbers, (CharStringCommand) obj);
                numbers.clear();
                if (results != null) {
                    numbers.addAll(results);
                }
            } else {
                numbers.add((Number) obj);
            }
        }
        return numbers;
    }
}
