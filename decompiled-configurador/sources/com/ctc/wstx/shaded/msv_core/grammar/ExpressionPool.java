package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.util.PossibleNamesCollector;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ExpressionPool.class */
public class ExpressionPool implements Serializable {
    private final ClosedHash expTable;
    private static final long serialVersionUID = 1;

    public final Expression createAttribute(NameClass nameClass) {
        return unify(new AttributeExp(nameClass, Expression.anyString));
    }

    public final Expression createAttribute(NameClass nameClass, Expression content) {
        return content == Expression.nullSet ? content : unify(new AttributeExp(nameClass, content));
    }

    public final Expression createEpsilon() {
        return Expression.epsilon;
    }

    public final Expression createNullSet() {
        return Expression.nullSet;
    }

    public final Expression createAnyString() {
        return Expression.anyString;
    }

    public final Expression createChoice(Expression left, Expression right) {
        if (left == Expression.nullSet) {
            return right;
        }
        if (right == Expression.nullSet) {
            return left;
        }
        if (left == Expression.epsilon && right.isEpsilonReducible()) {
            return right;
        }
        if (right == Expression.epsilon && left.isEpsilonReducible()) {
            return left;
        }
        if (right instanceof ChoiceExp) {
            ChoiceExp c = (ChoiceExp) right;
            return createChoice(createChoice(left, c.exp1), c.exp2);
        }
        Expression expression = left;
        while (true) {
            Expression next = expression;
            if (next == right) {
                return left;
            }
            if (next instanceof ChoiceExp) {
                ChoiceExp cp = (ChoiceExp) next;
                if (cp.exp2 == right) {
                    return left;
                }
                expression = cp.exp1;
            } else {
                Expression o = this.expTable.getBinExp(left, right, ChoiceExp.class);
                if (o == null) {
                    return unify(new ChoiceExp(left, right));
                }
                return o;
            }
        }
    }

    public final Expression createOneOrMore(Expression child) {
        if (child == Expression.epsilon || child == Expression.anyString || child == Expression.nullSet || (child instanceof OneOrMoreExp)) {
            return child;
        }
        return unify(new OneOrMoreExp(child));
    }

    public final Expression createZeroOrMore(Expression child) {
        return createOptional(createOneOrMore(child));
    }

    public final Expression createOptional(Expression child) {
        return createChoice(child, Expression.epsilon);
    }

    public final Expression createData(XSDatatype dt) {
        String ns = dt.getNamespaceUri();
        if (ns == null) {
            ns = PossibleNamesCollector.MAGIC;
        }
        return createData(dt, new StringPair(ns, dt.displayName()));
    }

    public final Expression createData(Datatype dt, StringPair typeName) {
        return createData(dt, typeName, Expression.nullSet);
    }

    public final Expression createData(Datatype dt, StringPair typeName, Expression except) {
        return unify(new DataExp(dt, typeName, except));
    }

    public final Expression createValue(XSDatatype dt, Object value) {
        return createValue(dt, new StringPair("", dt.displayName()), value);
    }

    public final Expression createValue(Datatype dt, StringPair typeName, Object value) {
        return unify(new ValueExp(dt, typeName, value));
    }

    public final Expression createList(Expression exp) {
        return exp == Expression.nullSet ? exp : unify(new ListExp(exp));
    }

    public final Expression createMixed(Expression body) {
        if (body == Expression.nullSet) {
            return Expression.nullSet;
        }
        return body == Expression.epsilon ? Expression.anyString : unify(new MixedExp(body));
    }

    public final Expression createSequence(Expression left, Expression right) {
        if (left == Expression.nullSet || right == Expression.nullSet) {
            return Expression.nullSet;
        }
        if (left == Expression.epsilon) {
            return right;
        }
        if (right == Expression.epsilon) {
            return left;
        }
        if (right instanceof SequenceExp) {
            SequenceExp s = (SequenceExp) right;
            return createSequence(createSequence(left, s.exp1), s.exp2);
        }
        Expression o = this.expTable.getBinExp(left, right, SequenceExp.class);
        if (o == null) {
            return unify(new SequenceExp(left, right));
        }
        return o;
    }

    public final Expression createConcur(Expression left, Expression right) {
        if (left == Expression.nullSet || right == Expression.nullSet) {
            return Expression.nullSet;
        }
        if (left == Expression.epsilon) {
            return right.isEpsilonReducible() ? Expression.epsilon : Expression.nullSet;
        }
        if (right == Expression.epsilon) {
            return left.isEpsilonReducible() ? Expression.epsilon : Expression.nullSet;
        }
        if (right instanceof ConcurExp) {
            ConcurExp c = (ConcurExp) right;
            return createConcur(createConcur(left, c.exp1), c.exp2);
        }
        return unify(new ConcurExp(left, right));
    }

    public final Expression createInterleave(Expression left, Expression right) {
        if (left == Expression.epsilon) {
            return right;
        }
        if (right == Expression.epsilon) {
            return left;
        }
        if (left == Expression.nullSet || right == Expression.nullSet) {
            return Expression.nullSet;
        }
        if (right instanceof InterleaveExp) {
            InterleaveExp i = (InterleaveExp) right;
            return createInterleave(createInterleave(left, i.exp1), i.exp2);
        }
        return unify(new InterleaveExp(left, right));
    }

    public ExpressionPool(ExpressionPool parent) {
        this.expTable = new ClosedHash(parent.expTable);
    }

    public ExpressionPool() {
        this.expTable = new ClosedHash();
    }

    protected final Expression unify(Expression exp) {
        Object o = this.expTable.get(exp);
        if (o == null) {
            synchronized (this.expTable) {
                o = this.expTable.get(exp);
                if (o == null) {
                    this.expTable.put(exp);
                    return exp;
                }
            }
        }
        return (Expression) o;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ExpressionPool$ClosedHash.class */
    public static final class ClosedHash implements Serializable {
        private Expression[] table;
        private int count;
        private int threshold;
        private static final float loadFactor = 0.3f;
        private static final int initialCapacity = 191;
        private ClosedHash parent;
        private static final long serialVersionUID = -2924295970572669668L;
        private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("count", Integer.TYPE), new ObjectStreamField("streamVersion", Byte.TYPE), new ObjectStreamField("parent", ExpressionPool.class)};

        public ClosedHash() {
            this(null);
        }

        public ClosedHash(ClosedHash parent) {
            this.table = new Expression[191];
            this.threshold = 57;
            this.parent = parent;
        }

        public Expression getBinExp(Expression left, Expression right, Class type) {
            int hash = (left.hashCode() + right.hashCode()) ^ type.hashCode();
            return getBinExp(hash, left, right, type);
        }

        private Expression getBinExp(int hash, Expression left, Expression right, Class type) {
            Expression e;
            if (this.parent != null && (e = this.parent.getBinExp(hash, left, right, type)) != null) {
                return e;
            }
            Expression[] tab = this.table;
            int i = hash & Integer.MAX_VALUE;
            int length = tab.length;
            while (true) {
                int index = i % length;
                Expression e2 = tab[index];
                if (e2 == null) {
                    return null;
                }
                if (e2.hashCode() == hash && e2.getClass() == type) {
                    BinaryExp be = (BinaryExp) e2;
                    if (be.exp1 == left && be.exp2 == right) {
                        return be;
                    }
                }
                i = index + 1;
                length = tab.length;
            }
        }

        public Expression get(int hash, Expression child, Class type) {
            Expression e;
            if (this.parent != null && (e = this.parent.get(hash, child, type)) != null) {
                return e;
            }
            Expression[] tab = this.table;
            int i = hash & Integer.MAX_VALUE;
            int length = tab.length;
            while (true) {
                int index = i % length;
                Expression e2 = tab[index];
                if (e2 == null) {
                    return null;
                }
                if (e2.hashCode() == hash && e2.getClass() == type) {
                    UnaryExp ue = (UnaryExp) e2;
                    if (ue.exp == child) {
                        return ue;
                    }
                }
                i = index + 1;
                length = tab.length;
            }
        }

        public Expression get(Expression key) {
            Expression e;
            if (this.parent != null && (e = this.parent.get(key)) != null) {
                return e;
            }
            Expression[] tab = this.table;
            int iHashCode = key.hashCode() & Integer.MAX_VALUE;
            int length = tab.length;
            while (true) {
                int index = iHashCode % length;
                Expression e2 = tab[index];
                if (e2 == null) {
                    return null;
                }
                if (e2.equals(key)) {
                    return e2;
                }
                iHashCode = index + 1;
                length = tab.length;
            }
        }

        private void rehash() {
            int index;
            int oldCapacity = this.table.length;
            Expression[] oldMap = this.table;
            int newCapacity = (oldCapacity * 2) + 1;
            Expression[] newMap = new Expression[newCapacity];
            int i = oldCapacity;
            while (true) {
                int i2 = i;
                i--;
                if (i2 > 0) {
                    if (oldMap[i] != null) {
                        int iHashCode = oldMap[i].hashCode() & Integer.MAX_VALUE;
                        int length = newMap.length;
                        while (true) {
                            index = iHashCode % length;
                            if (newMap[index] == null) {
                                break;
                            }
                            iHashCode = index + 1;
                            length = newMap.length;
                        }
                        newMap[index] = oldMap[i];
                    }
                } else {
                    this.threshold = (int) (newCapacity * loadFactor);
                    this.table = newMap;
                    return;
                }
            }
        }

        public void put(Expression newExp) {
            if (this.count >= this.threshold) {
                rehash();
            }
            Expression[] tab = this.table;
            int iHashCode = newExp.hashCode() & Integer.MAX_VALUE;
            int length = tab.length;
            while (true) {
                int index = iHashCode % length;
                if (tab[index] != null) {
                    iHashCode = index + 1;
                    length = tab.length;
                } else {
                    tab[index] = newExp;
                    this.count++;
                    return;
                }
            }
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            ObjectOutputStream.PutField fields = s.putFields();
            fields.put("count", this.count);
            fields.put("parent", this.parent);
            fields.put("streamVersion", (byte) 1);
            s.writeFields();
            for (int i = 0; i < this.table.length; i++) {
                if (this.table[i] != null) {
                    s.writeObject(this.table[i]);
                }
            }
        }

        private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
            ObjectInputStream.GetField fields = s.readFields();
            byte version = fields.get("streamVersion", (byte) 0);
            if (version == 0) {
                this.count = fields.get("count", 0);
                this.parent = (ClosedHash) fields.get("parent", (Object) null);
                this.table = (Expression[]) fields.get("table", (Object) null);
                this.threshold = fields.get("threshold", 0);
                return;
            }
            int objCnt = fields.get("count", 0);
            this.parent = (ClosedHash) fields.get("parent", (Object) null);
            int size = (((int) (objCnt / loadFactor)) * 2) + 10;
            this.threshold = this.count * 2;
            this.count = 0;
            this.table = new Expression[size];
            for (int i = 0; i < this.count; i++) {
                put((Expression) s.readObject());
            }
        }
    }
}
