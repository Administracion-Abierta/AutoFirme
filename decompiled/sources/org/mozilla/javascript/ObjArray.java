package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ObjArray.class */
public class ObjArray implements Serializable {
    private static final long serialVersionUID = 4174889037736658296L;
    private int size;
    private boolean sealed;
    private static final int FIELDS_STORE_SIZE = 5;
    private transient Object f0;
    private transient Object f1;
    private transient Object f2;
    private transient Object f3;
    private transient Object f4;
    private transient Object[] data;

    public final boolean isSealed() {
        return this.sealed;
    }

    public final void seal() {
        this.sealed = true;
    }

    public final boolean isEmpty() {
        return this.size == 0;
    }

    public final int size() {
        return this.size;
    }

    public final void setSize(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException();
        }
        if (this.sealed) {
            throw onSeledMutation();
        }
        int N = this.size;
        if (newSize < N) {
            for (int i = newSize; i != N; i++) {
                setImpl(i, null);
            }
        } else if (newSize > N && newSize > 5) {
            ensureCapacity(newSize);
        }
        this.size = newSize;
    }

    public final Object get(int index) {
        if (0 > index || index >= this.size) {
            throw onInvalidIndex(index, this.size);
        }
        return getImpl(index);
    }

    public final void set(int index, Object value) {
        if (0 > index || index >= this.size) {
            throw onInvalidIndex(index, this.size);
        }
        if (this.sealed) {
            throw onSeledMutation();
        }
        setImpl(index, value);
    }

    private Object getImpl(int index) {
        switch (index) {
            case 0:
                return this.f0;
            case 1:
                return this.f1;
            case 2:
                return this.f2;
            case 3:
                return this.f3;
            case 4:
                return this.f4;
            default:
                return this.data[index - 5];
        }
    }

    private void setImpl(int index, Object value) {
        switch (index) {
            case 0:
                this.f0 = value;
                break;
            case 1:
                this.f1 = value;
                break;
            case 2:
                this.f2 = value;
                break;
            case 3:
                this.f3 = value;
                break;
            case 4:
                this.f4 = value;
                break;
            default:
                this.data[index - 5] = value;
                break;
        }
    }

    public int indexOf(Object obj) {
        int N = this.size;
        for (int i = 0; i != N; i++) {
            Object current = getImpl(i);
            if (current == obj || (current != null && current.equals(obj))) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(Object obj) {
        int i = this.size;
        while (i != 0) {
            i--;
            Object current = getImpl(i);
            if (current == obj || (current != null && current.equals(obj))) {
                return i;
            }
        }
        return -1;
    }

    public final Object peek() {
        int N = this.size;
        if (N == 0) {
            throw onEmptyStackTopRead();
        }
        return getImpl(N - 1);
    }

    public final Object pop() {
        Object top;
        if (this.sealed) {
            throw onSeledMutation();
        }
        int N = this.size - 1;
        switch (N) {
            case -1:
                throw onEmptyStackTopRead();
            case 0:
                top = this.f0;
                this.f0 = null;
                break;
            case 1:
                top = this.f1;
                this.f1 = null;
                break;
            case 2:
                top = this.f2;
                this.f2 = null;
                break;
            case 3:
                top = this.f3;
                this.f3 = null;
                break;
            case 4:
                top = this.f4;
                this.f4 = null;
                break;
            default:
                top = this.data[N - 5];
                this.data[N - 5] = null;
                break;
        }
        this.size = N;
        return top;
    }

    public final void push(Object value) {
        add(value);
    }

    public final void add(Object value) {
        if (this.sealed) {
            throw onSeledMutation();
        }
        int N = this.size;
        if (N >= 5) {
            ensureCapacity(N + 1);
        }
        this.size = N + 1;
        setImpl(N, value);
    }

    public final void add(int index, Object value) {
        int N = this.size;
        if (0 > index || index > N) {
            throw onInvalidIndex(index, N + 1);
        }
        if (this.sealed) {
            throw onSeledMutation();
        }
        switch (index) {
            case 0:
                if (N != 0) {
                    Object tmp = this.f0;
                    this.f0 = value;
                    value = tmp;
                    break;
                } else {
                    this.f0 = value;
                    break;
                }
            case 1:
                if (N != 1) {
                    Object tmp2 = this.f1;
                    this.f1 = value;
                    value = tmp2;
                    break;
                } else {
                    this.f1 = value;
                    break;
                }
            case 2:
                if (N != 2) {
                    Object tmp3 = this.f2;
                    this.f2 = value;
                    value = tmp3;
                    break;
                } else {
                    this.f2 = value;
                    break;
                }
            case 3:
                if (N != 3) {
                    Object tmp4 = this.f3;
                    this.f3 = value;
                    value = tmp4;
                    break;
                } else {
                    this.f3 = value;
                    break;
                }
            case 4:
                if (N != 4) {
                    Object tmp5 = this.f4;
                    this.f4 = value;
                    value = tmp5;
                    index = 5;
                    break;
                } else {
                    this.f4 = value;
                    break;
                }
            default:
                ensureCapacity(N + 1);
                if (index != N) {
                    System.arraycopy(this.data, index - 5, this.data, (index - 5) + 1, N - index);
                }
                this.data[index - 5] = value;
                break;
        }
        this.size = N + 1;
    }

    public final void remove(int index) {
        int N = this.size;
        if (0 > index || index >= N) {
            throw onInvalidIndex(index, N);
        }
        if (this.sealed) {
            throw onSeledMutation();
        }
        int N2 = N - 1;
        switch (index) {
            case 0:
                if (N2 != 0) {
                    this.f0 = this.f1;
                    break;
                } else {
                    this.f0 = null;
                    break;
                }
            case 1:
                if (N2 != 1) {
                    this.f1 = this.f2;
                    break;
                } else {
                    this.f1 = null;
                    break;
                }
            case 2:
                if (N2 != 2) {
                    this.f2 = this.f3;
                    break;
                } else {
                    this.f2 = null;
                    break;
                }
            case 3:
                if (N2 != 3) {
                    this.f3 = this.f4;
                    break;
                } else {
                    this.f3 = null;
                    break;
                }
            case 4:
                if (N2 != 4) {
                    this.f4 = this.data[0];
                    index = 5;
                    break;
                } else {
                    this.f4 = null;
                    break;
                }
            default:
                if (index != N2) {
                    System.arraycopy(this.data, (index - 5) + 1, this.data, index - 5, N2 - index);
                }
                this.data[N2 - 5] = null;
                break;
        }
        this.size = N2;
    }

    public final void clear() {
        if (this.sealed) {
            throw onSeledMutation();
        }
        int N = this.size;
        for (int i = 0; i != N; i++) {
            setImpl(i, null);
        }
        this.size = 0;
    }

    public final Object[] toArray() {
        Object[] array = new Object[this.size];
        toArray(array, 0);
        return array;
    }

    public final void toArray(Object[] array) {
        toArray(array, 0);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final void toArray(Object[] array, int offset) {
        int N = this.size;
        switch (N) {
            case 0:
                break;
            case 1:
                array[offset + 0] = this.f0;
                break;
            case 2:
                array[offset + 1] = this.f1;
                array[offset + 0] = this.f0;
                break;
            case 3:
                array[offset + 2] = this.f2;
                array[offset + 1] = this.f1;
                array[offset + 0] = this.f0;
                break;
            case 4:
                array[offset + 3] = this.f3;
                array[offset + 2] = this.f2;
                array[offset + 1] = this.f1;
                array[offset + 0] = this.f0;
                break;
            case 5:
                array[offset + 4] = this.f4;
                array[offset + 3] = this.f3;
                array[offset + 2] = this.f2;
                array[offset + 1] = this.f1;
                array[offset + 0] = this.f0;
                break;
            default:
                System.arraycopy(this.data, 0, array, offset + 5, N - 5);
                array[offset + 4] = this.f4;
                array[offset + 3] = this.f3;
                array[offset + 2] = this.f2;
                array[offset + 1] = this.f1;
                array[offset + 0] = this.f0;
                break;
        }
    }

    private void ensureCapacity(int minimalCapacity) {
        int alloc;
        int required = minimalCapacity - 5;
        if (required <= 0) {
            throw new IllegalArgumentException();
        }
        if (this.data == null) {
            int alloc2 = 10;
            if (10 < required) {
                alloc2 = required;
            }
            this.data = new Object[alloc2];
            return;
        }
        int alloc3 = this.data.length;
        if (alloc3 < required) {
            if (alloc3 <= 5) {
                alloc = 10;
            } else {
                alloc = alloc3 * 2;
            }
            if (alloc < required) {
                alloc = required;
            }
            Object[] tmp = new Object[alloc];
            if (this.size > 5) {
                System.arraycopy(this.data, 0, tmp, 0, this.size - 5);
            }
            this.data = tmp;
        }
    }

    private static RuntimeException onInvalidIndex(int index, int upperBound) {
        String msg = index + " ∉ [0, " + upperBound + ')';
        throw new IndexOutOfBoundsException(msg);
    }

    private static RuntimeException onEmptyStackTopRead() {
        throw new RuntimeException("Empty stack");
    }

    private static RuntimeException onSeledMutation() {
        throw new IllegalStateException("Attempt to modify sealed array");
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        int N = this.size;
        for (int i = 0; i != N; i++) {
            Object obj = getImpl(i);
            os.writeObject(obj);
        }
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        is.defaultReadObject();
        int N = this.size;
        if (N > 5) {
            this.data = new Object[N - 5];
        }
        for (int i = 0; i != N; i++) {
            Object obj = is.readObject();
            setImpl(i, obj);
        }
    }
}
