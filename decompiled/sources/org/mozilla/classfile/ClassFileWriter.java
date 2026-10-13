package org.mozilla.classfile;

import com.aowagie.text.pdf.Barcode128;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.UintMap;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ClassFileWriter.class */
public class ClassFileWriter {
    public static final short ACC_PUBLIC = 1;
    public static final short ACC_PRIVATE = 2;
    public static final short ACC_PROTECTED = 4;
    public static final short ACC_STATIC = 8;
    public static final short ACC_FINAL = 16;
    public static final short ACC_SUPER = 32;
    public static final short ACC_SYNCHRONIZED = 32;
    public static final short ACC_VOLATILE = 64;
    public static final short ACC_TRANSIENT = 128;
    public static final short ACC_NATIVE = 256;
    public static final short ACC_ABSTRACT = 1024;
    private static final int SuperBlockStartsSize = 4;
    private static final int LineNumberTableSize = 16;
    private static final int ExceptionTableSize = 4;
    private static final int MajorVersion;
    private static final int MinorVersion;
    private static final boolean GenerateStackMap;
    private static final int FileHeaderConstant = -889275714;
    private static final boolean DEBUGSTACK = false;
    private static final boolean DEBUGLABELS = false;
    private static final boolean DEBUGCODE = false;
    private String generatedClassName;
    private ExceptionTableEntry[] itsExceptionTable;
    private int itsExceptionTableTop;
    private int[] itsLineNumberTable;
    private int itsLineNumberTableTop;
    private int itsCodeBufferTop;
    private ClassFileMethod itsCurrentMethod;
    private short itsStackTop;
    private short itsMaxStack;
    private short itsMaxLocals;
    private short itsFlags;
    private short itsThisClassIndex;
    private short itsSuperClassIndex;
    private short itsSourceFileNameIndex;
    private static final int MIN_LABEL_TABLE_SIZE = 32;
    private int[] itsLabelTable;
    private int itsLabelTableTop;
    private static final int MIN_FIXUP_TABLE_SIZE = 40;
    private long[] itsFixupTable;
    private int itsFixupTableTop;
    private ObjArray itsVarDescriptors;
    private ObjArray itsBootstrapMethods;
    private int[] itsSuperBlockStarts = null;
    private int itsSuperBlockStartsTop = 0;
    private UintMap itsJumpFroms = null;
    private byte[] itsCodeBuffer = new byte[256];
    private ObjArray itsMethods = new ObjArray();
    private ObjArray itsFields = new ObjArray();
    private ObjArray itsInterfaces = new ObjArray();
    private int itsBootstrapMethodsLength = 0;
    private char[] tmpCharBuffer = new char[64];
    private ConstantPool itsConstantPool = new ConstantPool(this);

    static /* synthetic */ int access$410(ClassFileWriter x0) {
        int i = x0.itsExceptionTableTop;
        x0.itsExceptionTableTop = i - 1;
        return i;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ClassFileWriter$ClassFileFormatException.class */
    public static class ClassFileFormatException extends RuntimeException {
        private static final long serialVersionUID = 1263998431033790599L;

        ClassFileFormatException(String message) {
            super(message);
        }
    }

    public ClassFileWriter(String className, String superClassName, String sourceFileName) {
        this.generatedClassName = className;
        this.itsThisClassIndex = this.itsConstantPool.addClass(className);
        this.itsSuperClassIndex = this.itsConstantPool.addClass(superClassName);
        if (sourceFileName != null) {
            this.itsSourceFileNameIndex = this.itsConstantPool.addUtf8(sourceFileName);
        }
        this.itsFlags = (short) 33;
    }

    public final String getClassName() {
        return this.generatedClassName;
    }

    public void addInterface(String interfaceName) {
        short interfaceIndex = this.itsConstantPool.addClass(interfaceName);
        this.itsInterfaces.add(Short.valueOf(interfaceIndex));
    }

    public void setFlags(short flags) {
        this.itsFlags = flags;
    }

    static String getSlashedForm(String name) {
        return name.replace('.', '/');
    }

    public static String classNameToSignature(String name) {
        int nameLength = name.length();
        int colonPos = 1 + nameLength;
        char[] buf = new char[colonPos + 1];
        buf[0] = 'L';
        buf[colonPos] = ';';
        name.getChars(0, nameLength, buf, 1);
        for (int i = 1; i != colonPos; i++) {
            if (buf[i] == '.') {
                buf[i] = '/';
            }
        }
        return new String(buf, 0, colonPos + 1);
    }

    public void addField(String fieldName, String type, short flags) {
        short fieldNameIndex = this.itsConstantPool.addUtf8(fieldName);
        short typeIndex = this.itsConstantPool.addUtf8(type);
        this.itsFields.add(new ClassFileField(fieldNameIndex, typeIndex, flags));
    }

    public void addField(String fieldName, String type, short flags, int value) {
        short fieldNameIndex = this.itsConstantPool.addUtf8(fieldName);
        short typeIndex = this.itsConstantPool.addUtf8(type);
        ClassFileField field = new ClassFileField(fieldNameIndex, typeIndex, flags);
        field.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short) 0, (short) 0, this.itsConstantPool.addConstant(value));
        this.itsFields.add(field);
    }

    public void addField(String fieldName, String type, short flags, long value) {
        short fieldNameIndex = this.itsConstantPool.addUtf8(fieldName);
        short typeIndex = this.itsConstantPool.addUtf8(type);
        ClassFileField field = new ClassFileField(fieldNameIndex, typeIndex, flags);
        field.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short) 0, (short) 2, this.itsConstantPool.addConstant(value));
        this.itsFields.add(field);
    }

    public void addField(String fieldName, String type, short flags, double value) {
        short fieldNameIndex = this.itsConstantPool.addUtf8(fieldName);
        short typeIndex = this.itsConstantPool.addUtf8(type);
        ClassFileField field = new ClassFileField(fieldNameIndex, typeIndex, flags);
        field.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short) 0, (short) 2, this.itsConstantPool.addConstant(value));
        this.itsFields.add(field);
    }

    public void addVariableDescriptor(String name, String type, int startPC, int register) {
        int nameIndex = this.itsConstantPool.addUtf8(name);
        int descriptorIndex = this.itsConstantPool.addUtf8(type);
        int[] chunk = {nameIndex, descriptorIndex, startPC, register};
        if (this.itsVarDescriptors == null) {
            this.itsVarDescriptors = new ObjArray();
        }
        this.itsVarDescriptors.add(chunk);
    }

    public void startMethod(String methodName, String type, short flags) {
        short methodNameIndex = this.itsConstantPool.addUtf8(methodName);
        short typeIndex = this.itsConstantPool.addUtf8(type);
        this.itsCurrentMethod = new ClassFileMethod(methodName, methodNameIndex, type, typeIndex, flags);
        this.itsJumpFroms = new UintMap();
        this.itsMethods.add(this.itsCurrentMethod);
        addSuperBlockStart(0);
    }

    public void stopMethod(short maxLocals) {
        int index;
        int stackMapWriteSize;
        if (this.itsCurrentMethod == null) {
            throw new IllegalStateException("No method to stop");
        }
        fixLabelGotos();
        this.itsMaxLocals = maxLocals;
        StackMapTable stackMap = null;
        if (GenerateStackMap) {
            finalizeSuperBlockStarts();
            stackMap = new StackMapTable();
            stackMap.generate();
        }
        int lineNumberTableLength = 0;
        if (this.itsLineNumberTable != null) {
            lineNumberTableLength = 8 + (this.itsLineNumberTableTop * 4);
        }
        int variableTableLength = 0;
        if (this.itsVarDescriptors != null) {
            variableTableLength = 8 + (this.itsVarDescriptors.size() * 10);
        }
        int stackMapTableLength = 0;
        if (stackMap != null && (stackMapWriteSize = stackMap.computeWriteSize()) > 0) {
            stackMapTableLength = 6 + stackMapWriteSize;
        }
        int attrLength = 14 + this.itsCodeBufferTop + 2 + (this.itsExceptionTableTop * 8) + 2 + lineNumberTableLength + variableTableLength + stackMapTableLength;
        if (attrLength > 65536) {
            throw new ClassFileFormatException("generated bytecode for method exceeds 64K limit.");
        }
        byte[] codeAttribute = new byte[attrLength];
        int codeAttrIndex = this.itsConstantPool.addUtf8(StandardStructureTypes.CODE);
        int index2 = putInt32(this.itsCodeBufferTop, codeAttribute, putInt16(this.itsMaxLocals, codeAttribute, putInt16(this.itsMaxStack, codeAttribute, putInt32(attrLength - 6, codeAttribute, putInt16(codeAttrIndex, codeAttribute, 0)))));
        System.arraycopy(this.itsCodeBuffer, 0, codeAttribute, index2, this.itsCodeBufferTop);
        int index3 = index2 + this.itsCodeBufferTop;
        if (this.itsExceptionTableTop > 0) {
            index = putInt16(this.itsExceptionTableTop, codeAttribute, index3);
            for (int i = 0; i < this.itsExceptionTableTop; i++) {
                ExceptionTableEntry ete = this.itsExceptionTable[i];
                int startPC = getLabelPC(ete.itsStartLabel);
                int endPC = getLabelPC(ete.itsEndLabel);
                int handlerPC = getLabelPC(ete.itsHandlerLabel);
                short catchType = ete.itsCatchType;
                if (startPC == -1) {
                    throw new IllegalStateException("start label not defined");
                }
                if (endPC == -1) {
                    throw new IllegalStateException("end label not defined");
                }
                if (handlerPC == -1) {
                    throw new IllegalStateException("handler label not defined");
                }
                index = putInt16(catchType, codeAttribute, putInt16(handlerPC, codeAttribute, putInt16(endPC, codeAttribute, putInt16(startPC, codeAttribute, index))));
            }
        } else {
            index = putInt16(0, codeAttribute, index3);
        }
        int attributeCount = 0;
        if (this.itsLineNumberTable != null) {
            attributeCount = 0 + 1;
        }
        if (this.itsVarDescriptors != null) {
            attributeCount++;
        }
        if (stackMapTableLength > 0) {
            attributeCount++;
        }
        int index4 = putInt16(attributeCount, codeAttribute, index);
        if (this.itsLineNumberTable != null) {
            int lineNumberTableAttrIndex = this.itsConstantPool.addUtf8("LineNumberTable");
            int index5 = putInt16(lineNumberTableAttrIndex, codeAttribute, index4);
            int tableAttrLength = 2 + (this.itsLineNumberTableTop * 4);
            index4 = putInt16(this.itsLineNumberTableTop, codeAttribute, putInt32(tableAttrLength, codeAttribute, index5));
            for (int i2 = 0; i2 < this.itsLineNumberTableTop; i2++) {
                index4 = putInt32(this.itsLineNumberTable[i2], codeAttribute, index4);
            }
        }
        if (this.itsVarDescriptors != null) {
            int variableTableAttrIndex = this.itsConstantPool.addUtf8("LocalVariableTable");
            int index6 = putInt16(variableTableAttrIndex, codeAttribute, index4);
            int varCount = this.itsVarDescriptors.size();
            int tableAttrLength2 = 2 + (varCount * 10);
            index4 = putInt16(varCount, codeAttribute, putInt32(tableAttrLength2, codeAttribute, index6));
            for (int i3 = 0; i3 < varCount; i3++) {
                int[] chunk = (int[]) this.itsVarDescriptors.get(i3);
                int nameIndex = chunk[0];
                int descriptorIndex = chunk[1];
                int startPC2 = chunk[2];
                int register = chunk[3];
                int length = this.itsCodeBufferTop - startPC2;
                index4 = putInt16(register, codeAttribute, putInt16(descriptorIndex, codeAttribute, putInt16(nameIndex, codeAttribute, putInt16(length, codeAttribute, putInt16(startPC2, codeAttribute, index4)))));
            }
        }
        if (stackMapTableLength > 0) {
            int stackMapTableAttrIndex = this.itsConstantPool.addUtf8("StackMapTable");
            stackMap.write(codeAttribute, putInt16(stackMapTableAttrIndex, codeAttribute, index4));
        }
        this.itsCurrentMethod.setCodeAttribute(codeAttribute);
        this.itsExceptionTable = null;
        this.itsExceptionTableTop = 0;
        this.itsLineNumberTableTop = 0;
        this.itsCodeBufferTop = 0;
        this.itsCurrentMethod = null;
        this.itsMaxStack = (short) 0;
        this.itsStackTop = (short) 0;
        this.itsLabelTableTop = 0;
        this.itsFixupTableTop = 0;
        this.itsVarDescriptors = null;
        this.itsSuperBlockStarts = null;
        this.itsSuperBlockStartsTop = 0;
        this.itsJumpFroms = null;
    }

    public void add(int theOpCode) {
        if (opcodeCount(theOpCode) != 0) {
            throw new IllegalArgumentException("Unexpected operands");
        }
        int newStack = this.itsStackTop + stackChange(theOpCode);
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        addToCodeBuffer(theOpCode);
        this.itsStackTop = (short) newStack;
        if (newStack > this.itsMaxStack) {
            this.itsMaxStack = (short) newStack;
        }
        if (theOpCode == 191) {
            addSuperBlockStart(this.itsCodeBufferTop);
        }
    }

    public void add(int theOpCode, int theOperand) {
        int newStack = this.itsStackTop + stackChange(theOpCode);
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        switch (theOpCode) {
            case 16:
                if (((byte) theOperand) != theOperand) {
                    throw new IllegalArgumentException("out of range byte");
                }
                addToCodeBuffer(theOpCode);
                addToCodeBuffer((byte) theOperand);
                break;
                break;
            case 17:
                if (((short) theOperand) != theOperand) {
                    throw new IllegalArgumentException("out of range short");
                }
                addToCodeBuffer(theOpCode);
                addToCodeInt16(theOperand);
                break;
                break;
            case 18:
            case 19:
            case 20:
                if (0 > theOperand || theOperand >= 65536) {
                    throw new ClassFileFormatException("out of range index");
                }
                if (theOperand >= 256 || theOpCode == 19 || theOpCode == 20) {
                    if (theOpCode == 18) {
                        addToCodeBuffer(19);
                    } else {
                        addToCodeBuffer(theOpCode);
                    }
                    addToCodeInt16(theOperand);
                } else {
                    addToCodeBuffer(theOpCode);
                    addToCodeBuffer(theOperand);
                }
                break;
                break;
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 169:
                if (theOperand < 0 || 65536 <= theOperand) {
                    throw new ClassFileFormatException("out of range variable");
                }
                if (theOperand >= 256) {
                    addToCodeBuffer(196);
                    addToCodeBuffer(theOpCode);
                    addToCodeInt16(theOperand);
                } else {
                    addToCodeBuffer(theOpCode);
                    addToCodeBuffer(theOperand);
                }
                break;
                break;
            case 167:
                addSuperBlockStart(this.itsCodeBufferTop + 3);
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 168:
            case 198:
            case 199:
                if ((theOperand & Integer.MIN_VALUE) != Integer.MIN_VALUE && (theOperand < 0 || theOperand > 65535)) {
                    throw new IllegalArgumentException("Bad label for branch");
                }
                int branchPC = this.itsCodeBufferTop;
                addToCodeBuffer(theOpCode);
                if ((theOperand & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
                    addToCodeInt16(theOperand);
                    int target = theOperand + branchPC;
                    addSuperBlockStart(target);
                    this.itsJumpFroms.put(target, branchPC);
                } else {
                    int targetPC = getLabelPC(theOperand);
                    if (targetPC != -1) {
                        int offset = targetPC - branchPC;
                        addToCodeInt16(offset);
                        addSuperBlockStart(targetPC);
                        this.itsJumpFroms.put(targetPC, branchPC);
                    } else {
                        addLabelFixup(theOperand, branchPC + 1);
                        addToCodeInt16(0);
                    }
                }
                break;
                break;
            case 180:
            case 181:
                if (0 > theOperand || theOperand >= 65536) {
                    throw new IllegalArgumentException("out of range field");
                }
                addToCodeBuffer(theOpCode);
                addToCodeInt16(theOperand);
                break;
                break;
            case 188:
                if (0 > theOperand || theOperand >= 256) {
                    throw new IllegalArgumentException("out of range index");
                }
                addToCodeBuffer(theOpCode);
                addToCodeBuffer(theOperand);
                break;
                break;
            default:
                throw new IllegalArgumentException("Unexpected opcode for 1 operand");
        }
        this.itsStackTop = (short) newStack;
        if (newStack > this.itsMaxStack) {
            this.itsMaxStack = (short) newStack;
        }
    }

    public void addLoadConstant(int k) {
        switch (k) {
            case 0:
                add(3);
                break;
            case 1:
                add(4);
                break;
            case 2:
                add(5);
                break;
            case 3:
                add(6);
                break;
            case 4:
                add(7);
                break;
            case 5:
                add(8);
                break;
            default:
                add(18, this.itsConstantPool.addConstant(k));
                break;
        }
    }

    public void addLoadConstant(long k) {
        add(20, this.itsConstantPool.addConstant(k));
    }

    public void addLoadConstant(float k) {
        add(18, this.itsConstantPool.addConstant(k));
    }

    public void addLoadConstant(double k) {
        add(20, this.itsConstantPool.addConstant(k));
    }

    public void addLoadConstant(String k) {
        add(18, this.itsConstantPool.addConstant(k));
    }

    public void add(int theOpCode, int theOperand1, int theOperand2) {
        int newStack = this.itsStackTop + stackChange(theOpCode);
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        if (theOpCode == 132) {
            if (theOperand1 < 0 || 65536 <= theOperand1) {
                throw new ClassFileFormatException("out of range variable");
            }
            if (theOperand2 < 0 || 65536 <= theOperand2) {
                throw new ClassFileFormatException("out of range increment");
            }
            if (theOperand1 > 255 || theOperand2 > 127) {
                addToCodeBuffer(196);
                addToCodeBuffer(132);
                addToCodeInt16(theOperand1);
                addToCodeInt16(theOperand2);
            } else {
                addToCodeBuffer(132);
                addToCodeBuffer(theOperand1);
                addToCodeBuffer(theOperand2);
            }
        } else if (theOpCode == 197) {
            if (0 > theOperand1 || theOperand1 >= 65536) {
                throw new IllegalArgumentException("out of range index");
            }
            if (0 > theOperand2 || theOperand2 >= 256) {
                throw new IllegalArgumentException("out of range dimensions");
            }
            addToCodeBuffer(197);
            addToCodeInt16(theOperand1);
            addToCodeBuffer(theOperand2);
        } else {
            throw new IllegalArgumentException("Unexpected opcode for 2 operands");
        }
        this.itsStackTop = (short) newStack;
        if (newStack > this.itsMaxStack) {
            this.itsMaxStack = (short) newStack;
        }
    }

    public void add(int theOpCode, String className) {
        int newStack = this.itsStackTop + stackChange(theOpCode);
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        switch (theOpCode) {
            case 187:
            case 189:
            case 192:
            case 193:
                short classIndex = this.itsConstantPool.addClass(className);
                addToCodeBuffer(theOpCode);
                addToCodeInt16(classIndex);
                this.itsStackTop = (short) newStack;
                if (newStack > this.itsMaxStack) {
                    this.itsMaxStack = (short) newStack;
                    return;
                }
                return;
            case 188:
            case 190:
            case 191:
            default:
                throw new IllegalArgumentException("bad opcode for class reference");
        }
    }

    public void add(int theOpCode, String className, String fieldName, String fieldType) {
        int newStack;
        int newStack2 = this.itsStackTop + stackChange(theOpCode);
        char fieldTypeChar = fieldType.charAt(0);
        int fieldSize = (fieldTypeChar == 'J' || fieldTypeChar == 'D') ? 2 : 1;
        switch (theOpCode) {
            case 178:
            case 180:
                newStack = newStack2 + fieldSize;
                break;
            case 179:
            case 181:
                newStack = newStack2 - fieldSize;
                break;
            default:
                throw new IllegalArgumentException("bad opcode for field reference");
        }
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        short fieldRefIndex = this.itsConstantPool.addFieldRef(className, fieldName, fieldType);
        addToCodeBuffer(theOpCode);
        addToCodeInt16(fieldRefIndex);
        this.itsStackTop = (short) newStack;
        if (newStack > this.itsMaxStack) {
            this.itsMaxStack = (short) newStack;
        }
    }

    public void addInvoke(int theOpCode, String className, String methodName, String methodType) {
        int parameterInfo = sizeOfParameters(methodType);
        int parameterCount = parameterInfo >>> 16;
        int stackDiff = (short) parameterInfo;
        int newStack = this.itsStackTop + stackDiff + stackChange(theOpCode);
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        switch (theOpCode) {
            case 182:
            case 183:
            case 184:
            case 185:
                addToCodeBuffer(theOpCode);
                if (theOpCode == 185) {
                    short ifMethodRefIndex = this.itsConstantPool.addInterfaceMethodRef(className, methodName, methodType);
                    addToCodeInt16(ifMethodRefIndex);
                    addToCodeBuffer(parameterCount + 1);
                    addToCodeBuffer(0);
                } else {
                    short methodRefIndex = this.itsConstantPool.addMethodRef(className, methodName, methodType);
                    addToCodeInt16(methodRefIndex);
                }
                this.itsStackTop = (short) newStack;
                if (newStack > this.itsMaxStack) {
                    this.itsMaxStack = (short) newStack;
                    return;
                }
                return;
            default:
                throw new IllegalArgumentException("bad opcode for method reference");
        }
    }

    public void addInvokeDynamic(String methodName, String methodType, MHandle bsm, Object... bsmArgs) {
        if (MajorVersion < 51) {
            throw new RuntimeException("Please build and run with JDK 1.7 for invokedynamic support");
        }
        int parameterInfo = sizeOfParameters(methodType);
        int stackDiff = (short) parameterInfo;
        int newStack = this.itsStackTop + stackDiff;
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        BootstrapEntry bsmEntry = new BootstrapEntry(bsm, bsmArgs);
        if (this.itsBootstrapMethods == null) {
            this.itsBootstrapMethods = new ObjArray();
        }
        int bootstrapIndex = this.itsBootstrapMethods.indexOf(bsmEntry);
        if (bootstrapIndex == -1) {
            bootstrapIndex = this.itsBootstrapMethods.size();
            this.itsBootstrapMethods.add(bsmEntry);
            this.itsBootstrapMethodsLength += bsmEntry.code.length;
        }
        short invokedynamicIndex = this.itsConstantPool.addInvokeDynamic(methodName, methodType, bootstrapIndex);
        addToCodeBuffer(186);
        addToCodeInt16(invokedynamicIndex);
        addToCodeInt16(0);
        this.itsStackTop = (short) newStack;
        if (newStack > this.itsMaxStack) {
            this.itsMaxStack = (short) newStack;
        }
    }

    public void addPush(int k) {
        if (((byte) k) == k) {
            if (k == -1) {
                add(2);
                return;
            } else if (0 <= k && k <= 5) {
                add((byte) (3 + k));
                return;
            } else {
                add(16, (byte) k);
                return;
            }
        }
        if (((short) k) == k) {
            add(17, (short) k);
        } else {
            addLoadConstant(k);
        }
    }

    public void addPush(boolean k) {
        add(k ? 4 : 3);
    }

    public void addPush(long k) {
        int ik = (int) k;
        if (ik == k) {
            addPush(ik);
            add(133);
        } else {
            addLoadConstant(k);
        }
    }

    public void addPush(double k) {
        if (k == 0.0d) {
            add(14);
            if (1.0d / k < 0.0d) {
                add(119);
                return;
            }
            return;
        }
        if (k == 1.0d || k == -1.0d) {
            add(15);
            if (k < 0.0d) {
                add(119);
                return;
            }
            return;
        }
        addLoadConstant(k);
    }

    public void addPush(String k) {
        int length = k.length();
        int limit = this.itsConstantPool.getUtfEncodingLimit(k, 0, length);
        if (limit == length) {
            addLoadConstant(k);
            return;
        }
        add(187, "java/lang/StringBuilder");
        add(89);
        addPush(length);
        addInvoke(183, "java/lang/StringBuilder", "<init>", "(I)V");
        int cursor = 0;
        while (true) {
            add(89);
            String s = k.substring(cursor, limit);
            addLoadConstant(s);
            addInvoke(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            add(87);
            if (limit != length) {
                cursor = limit;
                limit = this.itsConstantPool.getUtfEncodingLimit(k, limit, length);
            } else {
                addInvoke(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                return;
            }
        }
    }

    public boolean isUnderStringSizeLimit(String k) {
        return this.itsConstantPool.isUnderUtfEncodingLimit(k);
    }

    public void addIStore(int local) {
        xop(59, 54, local);
    }

    public void addLStore(int local) {
        xop(63, 55, local);
    }

    public void addFStore(int local) {
        xop(67, 56, local);
    }

    public void addDStore(int local) {
        xop(71, 57, local);
    }

    public void addAStore(int local) {
        xop(75, 58, local);
    }

    public void addILoad(int local) {
        xop(26, 21, local);
    }

    public void addLLoad(int local) {
        xop(30, 22, local);
    }

    public void addFLoad(int local) {
        xop(34, 23, local);
    }

    public void addDLoad(int local) {
        xop(38, 24, local);
    }

    public void addALoad(int local) {
        xop(42, 25, local);
    }

    public void addLoadThis() {
        add(42);
    }

    private void xop(int shortOp, int op, int local) {
        switch (local) {
            case 0:
                add(shortOp);
                break;
            case 1:
                add(shortOp + 1);
                break;
            case 2:
                add(shortOp + 2);
                break;
            case 3:
                add(shortOp + 3);
                break;
            default:
                add(op, local);
                break;
        }
    }

    public int addTableSwitch(int low, int high) {
        if (low > high) {
            throw new ClassFileFormatException("Bad bounds: " + low + ' ' + high);
        }
        int newStack = this.itsStackTop + stackChange(170);
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        int entryCount = (high - low) + 1;
        int padSize = 3 & (this.itsCodeBufferTop ^ (-1));
        int N = addReservedCodeSpace(1 + padSize + (4 * (3 + entryCount)));
        int N2 = N + 1;
        this.itsCodeBuffer[N] = -86;
        while (padSize != 0) {
            int i = N2;
            N2++;
            this.itsCodeBuffer[i] = 0;
            padSize--;
        }
        putInt32(high, this.itsCodeBuffer, putInt32(low, this.itsCodeBuffer, N2 + 4));
        this.itsStackTop = (short) newStack;
        if (newStack > this.itsMaxStack) {
            this.itsMaxStack = (short) newStack;
        }
        return N;
    }

    public final void markTableSwitchDefault(int switchStart) {
        addSuperBlockStart(this.itsCodeBufferTop);
        this.itsJumpFroms.put(this.itsCodeBufferTop, switchStart);
        setTableSwitchJump(switchStart, -1, this.itsCodeBufferTop);
    }

    public final void markTableSwitchCase(int switchStart, int caseIndex) {
        addSuperBlockStart(this.itsCodeBufferTop);
        this.itsJumpFroms.put(this.itsCodeBufferTop, switchStart);
        setTableSwitchJump(switchStart, caseIndex, this.itsCodeBufferTop);
    }

    public final void markTableSwitchCase(int switchStart, int caseIndex, int stackTop) {
        if (0 > stackTop || stackTop > this.itsMaxStack) {
            throw new IllegalArgumentException("Bad stack index: " + stackTop);
        }
        this.itsStackTop = (short) stackTop;
        addSuperBlockStart(this.itsCodeBufferTop);
        this.itsJumpFroms.put(this.itsCodeBufferTop, switchStart);
        setTableSwitchJump(switchStart, caseIndex, this.itsCodeBufferTop);
    }

    public void setTableSwitchJump(int switchStart, int caseIndex, int jumpTarget) {
        int caseOffset;
        if (jumpTarget < 0 || this.itsCodeBufferTop < jumpTarget) {
            throw new IllegalArgumentException("Bad jump target: " + jumpTarget);
        }
        if (caseIndex < -1) {
            throw new IllegalArgumentException("Bad case index: " + caseIndex);
        }
        int padSize = 3 & (switchStart ^ (-1));
        if (caseIndex < 0) {
            caseOffset = switchStart + 1 + padSize;
        } else {
            caseOffset = switchStart + 1 + padSize + (4 * (3 + caseIndex));
        }
        if (switchStart < 0 || ((this.itsCodeBufferTop - 16) - padSize) - 1 < switchStart) {
            throw new IllegalArgumentException(switchStart + " is outside a possible range of tableswitch in already generated code");
        }
        if ((255 & this.itsCodeBuffer[switchStart]) != 170) {
            throw new IllegalArgumentException(switchStart + " is not offset of tableswitch statement");
        }
        if (caseOffset < 0 || this.itsCodeBufferTop < caseOffset + 4) {
            throw new ClassFileFormatException("Too big case index: " + caseIndex);
        }
        putInt32(jumpTarget - switchStart, this.itsCodeBuffer, caseOffset);
    }

    public int acquireLabel() {
        int top = this.itsLabelTableTop;
        if (this.itsLabelTable == null || top == this.itsLabelTable.length) {
            if (this.itsLabelTable == null) {
                this.itsLabelTable = new int[32];
            } else {
                int[] tmp = new int[this.itsLabelTable.length * 2];
                System.arraycopy(this.itsLabelTable, 0, tmp, 0, top);
                this.itsLabelTable = tmp;
            }
        }
        this.itsLabelTableTop = top + 1;
        this.itsLabelTable[top] = -1;
        return top | Integer.MIN_VALUE;
    }

    public void markLabel(int label) {
        if (label >= 0) {
            throw new IllegalArgumentException("Bad label, no biscuit");
        }
        int label2 = label & WinNT.MAXLONG;
        if (label2 > this.itsLabelTableTop) {
            throw new IllegalArgumentException("Bad label");
        }
        if (this.itsLabelTable[label2] != -1) {
            throw new IllegalStateException("Can only mark label once");
        }
        this.itsLabelTable[label2] = this.itsCodeBufferTop;
    }

    public void markLabel(int label, short stackTop) {
        markLabel(label);
        this.itsStackTop = stackTop;
    }

    public void markHandler(int theLabel) {
        this.itsStackTop = (short) 1;
        markLabel(theLabel);
    }

    public int getLabelPC(int label) {
        if (label >= 0) {
            throw new IllegalArgumentException("Bad label, no biscuit");
        }
        int label2 = label & WinNT.MAXLONG;
        if (label2 >= this.itsLabelTableTop) {
            throw new IllegalArgumentException("Bad label");
        }
        return this.itsLabelTable[label2];
    }

    private void addLabelFixup(int label, int fixupSite) {
        if (label >= 0) {
            throw new IllegalArgumentException("Bad label, no biscuit");
        }
        int label2 = label & WinNT.MAXLONG;
        if (label2 >= this.itsLabelTableTop) {
            throw new IllegalArgumentException("Bad label");
        }
        int top = this.itsFixupTableTop;
        if (this.itsFixupTable == null || top == this.itsFixupTable.length) {
            if (this.itsFixupTable == null) {
                this.itsFixupTable = new long[40];
            } else {
                long[] tmp = new long[this.itsFixupTable.length * 2];
                System.arraycopy(this.itsFixupTable, 0, tmp, 0, top);
                this.itsFixupTable = tmp;
            }
        }
        this.itsFixupTableTop = top + 1;
        this.itsFixupTable[top] = (((long) label2) << 32) | ((long) fixupSite);
    }

    private void fixLabelGotos() {
        byte[] codeBuffer = this.itsCodeBuffer;
        for (int i = 0; i < this.itsFixupTableTop; i++) {
            long fixup = this.itsFixupTable[i];
            int label = (int) (fixup >> 32);
            int fixupSite = (int) fixup;
            int pc = this.itsLabelTable[label];
            if (pc == -1) {
                throw new RuntimeException("unlocated label");
            }
            addSuperBlockStart(pc);
            this.itsJumpFroms.put(pc, fixupSite - 1);
            int offset = pc - (fixupSite - 1);
            if (((short) offset) != offset) {
                throw new ClassFileFormatException("Program too complex: too big jump offset");
            }
            codeBuffer[fixupSite] = (byte) (offset >> 8);
            codeBuffer[fixupSite + 1] = (byte) offset;
        }
        this.itsFixupTableTop = 0;
    }

    public int getCurrentCodeOffset() {
        return this.itsCodeBufferTop;
    }

    public short getStackTop() {
        return this.itsStackTop;
    }

    public void setStackTop(short n) {
        this.itsStackTop = n;
    }

    public void adjustStackTop(int delta) {
        int newStack = this.itsStackTop + delta;
        if (newStack < 0 || 32767 < newStack) {
            badStack(newStack);
        }
        this.itsStackTop = (short) newStack;
        if (newStack > this.itsMaxStack) {
            this.itsMaxStack = (short) newStack;
        }
    }

    private void addToCodeBuffer(int b) {
        int N = addReservedCodeSpace(1);
        this.itsCodeBuffer[N] = (byte) b;
    }

    private void addToCodeInt16(int value) {
        int N = addReservedCodeSpace(2);
        putInt16(value, this.itsCodeBuffer, N);
    }

    private int addReservedCodeSpace(int size) {
        if (this.itsCurrentMethod == null) {
            throw new IllegalArgumentException("No method to add to");
        }
        int oldTop = this.itsCodeBufferTop;
        int newTop = oldTop + size;
        if (newTop > this.itsCodeBuffer.length) {
            int newSize = this.itsCodeBuffer.length * 2;
            if (newTop > newSize) {
                newSize = newTop;
            }
            byte[] tmp = new byte[newSize];
            System.arraycopy(this.itsCodeBuffer, 0, tmp, 0, oldTop);
            this.itsCodeBuffer = tmp;
        }
        this.itsCodeBufferTop = newTop;
        return oldTop;
    }

    public void addExceptionHandler(int startLabel, int endLabel, int handlerLabel, String catchClassName) {
        if ((startLabel & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Bad startLabel");
        }
        if ((endLabel & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Bad endLabel");
        }
        if ((handlerLabel & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Bad handlerLabel");
        }
        short catch_type_index = catchClassName == null ? (short) 0 : this.itsConstantPool.addClass(catchClassName);
        ExceptionTableEntry newEntry = new ExceptionTableEntry(startLabel, endLabel, handlerLabel, catch_type_index);
        int N = this.itsExceptionTableTop;
        if (N == 0) {
            this.itsExceptionTable = new ExceptionTableEntry[4];
        } else if (N == this.itsExceptionTable.length) {
            ExceptionTableEntry[] tmp = new ExceptionTableEntry[N * 2];
            System.arraycopy(this.itsExceptionTable, 0, tmp, 0, N);
            this.itsExceptionTable = tmp;
        }
        this.itsExceptionTable[N] = newEntry;
        this.itsExceptionTableTop = N + 1;
    }

    public void addLineNumberEntry(short lineNumber) {
        if (this.itsCurrentMethod == null) {
            throw new IllegalArgumentException("No method to stop");
        }
        int N = this.itsLineNumberTableTop;
        if (N == 0) {
            this.itsLineNumberTable = new int[16];
        } else if (N == this.itsLineNumberTable.length) {
            int[] tmp = new int[N * 2];
            System.arraycopy(this.itsLineNumberTable, 0, tmp, 0, N);
            this.itsLineNumberTable = tmp;
        }
        this.itsLineNumberTable[N] = (this.itsCodeBufferTop << 16) + lineNumber;
        this.itsLineNumberTableTop = N + 1;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ClassFileWriter$StackMapTable.class */
    final class StackMapTable {
        static final boolean DEBUGSTACKMAP = false;
        private SuperBlock[] superBlocks = null;
        private int[] stack = null;
        private int[] locals = null;
        private SuperBlock[] workList = null;
        private byte[] rawStackMap = null;
        private int localsTop = 0;
        private int stackTop = 0;
        private int workListTop = 0;
        private int rawStackMapTop = 0;
        private boolean wide = false;

        StackMapTable() {
        }

        void generate() {
            this.superBlocks = new SuperBlock[ClassFileWriter.this.itsSuperBlockStartsTop];
            int[] initialLocals = ClassFileWriter.this.createInitialLocals();
            int i = 0;
            while (i < ClassFileWriter.this.itsSuperBlockStartsTop) {
                int start = ClassFileWriter.this.itsSuperBlockStarts[i];
                int end = i == ClassFileWriter.this.itsSuperBlockStartsTop - 1 ? ClassFileWriter.this.itsCodeBufferTop : ClassFileWriter.this.itsSuperBlockStarts[i + 1];
                this.superBlocks[i] = new SuperBlock(i, start, end, initialLocals);
                i++;
            }
            verify();
        }

        private SuperBlock getSuperBlockFromOffset(int offset) {
            SuperBlock sb;
            for (int i = 0; i < this.superBlocks.length && (sb = this.superBlocks[i]) != null; i++) {
                if (offset >= sb.getStart() && offset < sb.getEnd()) {
                    return sb;
                }
            }
            throw new IllegalArgumentException("bad offset: " + offset);
        }

        private boolean isSuperBlockEnd(int opcode) {
            switch (opcode) {
                case 167:
                case 170:
                case 171:
                case 172:
                case 173:
                case 174:
                case 176:
                case 177:
                case 191:
                case 200:
                    return true;
                case 168:
                case 169:
                case 175:
                case 178:
                case 179:
                case 180:
                case 181:
                case 182:
                case 183:
                case 184:
                case 185:
                case 186:
                case 187:
                case 188:
                case 189:
                case 190:
                case 192:
                case 193:
                case 194:
                case 195:
                case 196:
                case 197:
                case 198:
                case 199:
                default:
                    return false;
            }
        }

        private SuperBlock[] getSuperBlockDependencies() {
            SuperBlock[] deps = new SuperBlock[this.superBlocks.length];
            for (int i = 0; i < ClassFileWriter.this.itsExceptionTableTop; i++) {
                ExceptionTableEntry ete = ClassFileWriter.this.itsExceptionTable[i];
                int startPC = ClassFileWriter.this.getLabelPC(ete.itsStartLabel);
                int handlerPC = ClassFileWriter.this.getLabelPC(ete.itsHandlerLabel);
                SuperBlock handlerSB = getSuperBlockFromOffset(handlerPC);
                SuperBlock dep = getSuperBlockFromOffset(startPC);
                deps[handlerSB.getIndex()] = dep;
            }
            int[] targetPCs = ClassFileWriter.this.itsJumpFroms.getKeys();
            for (int targetPC : targetPCs) {
                int branchPC = ClassFileWriter.this.itsJumpFroms.getInt(targetPC, -1);
                SuperBlock branchSB = getSuperBlockFromOffset(branchPC);
                SuperBlock targetSB = getSuperBlockFromOffset(targetPC);
                deps[targetSB.getIndex()] = branchSB;
            }
            return deps;
        }

        private SuperBlock getBranchTarget(int bci) {
            int target;
            if ((ClassFileWriter.this.itsCodeBuffer[bci] & 255) == 200) {
                target = bci + getOperand(bci + 1, 4);
            } else {
                target = bci + ((short) getOperand(bci + 1, 2));
            }
            return getSuperBlockFromOffset(target);
        }

        private boolean isBranch(int opcode) {
            switch (opcode) {
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 167:
                case 198:
                case 199:
                case 200:
                    return true;
                case 168:
                case 169:
                case 170:
                case 171:
                case 172:
                case 173:
                case 174:
                case 175:
                case 176:
                case 177:
                case 178:
                case 179:
                case 180:
                case 181:
                case 182:
                case 183:
                case 184:
                case 185:
                case 186:
                case 187:
                case 188:
                case 189:
                case 190:
                case 191:
                case 192:
                case 193:
                case 194:
                case 195:
                case 196:
                case 197:
                default:
                    return false;
            }
        }

        private int getOperand(int offset) {
            return getOperand(offset, 1);
        }

        private int getOperand(int start, int size) {
            int result = 0;
            if (size > 4) {
                throw new IllegalArgumentException("bad operand size");
            }
            for (int i = 0; i < size; i++) {
                result = (result << 8) | (ClassFileWriter.this.itsCodeBuffer[start + i] & 255);
            }
            return result;
        }

        private void verify() {
            int[] initialLocals = ClassFileWriter.this.createInitialLocals();
            this.superBlocks[0].merge(initialLocals, initialLocals.length, new int[0], 0, ClassFileWriter.this.itsConstantPool);
            this.workList = new SuperBlock[]{this.superBlocks[0]};
            this.workListTop = 1;
            executeWorkList();
            for (int i = 0; i < this.superBlocks.length; i++) {
                SuperBlock sb = this.superBlocks[i];
                if (!sb.isInitialized()) {
                    killSuperBlock(sb);
                }
            }
            executeWorkList();
        }

        private void killSuperBlock(SuperBlock sb) {
            int[] locals = new int[0];
            int[] stack = {TypeInfo.OBJECT("java/lang/Throwable", ClassFileWriter.this.itsConstantPool)};
            for (int i = 0; i < ClassFileWriter.this.itsExceptionTableTop; i++) {
                ExceptionTableEntry ete = ClassFileWriter.this.itsExceptionTable[i];
                int eteStart = ClassFileWriter.this.getLabelPC(ete.itsStartLabel);
                int eteEnd = ClassFileWriter.this.getLabelPC(ete.itsEndLabel);
                int handlerPC = ClassFileWriter.this.getLabelPC(ete.itsHandlerLabel);
                SuperBlock handlerSB = getSuperBlockFromOffset(handlerPC);
                if ((sb.getStart() > eteStart && sb.getStart() < eteEnd) || (eteStart > sb.getStart() && eteStart < sb.getEnd() && handlerSB.isInitialized())) {
                    locals = handlerSB.getLocals();
                    break;
                }
            }
            int i2 = 0;
            while (i2 < ClassFileWriter.this.itsExceptionTableTop) {
                if (ClassFileWriter.this.getLabelPC(ClassFileWriter.this.itsExceptionTable[i2].itsStartLabel) == sb.getStart()) {
                    for (int j = i2 + 1; j < ClassFileWriter.this.itsExceptionTableTop; j++) {
                        ClassFileWriter.this.itsExceptionTable[j - 1] = ClassFileWriter.this.itsExceptionTable[j];
                    }
                    ClassFileWriter.access$410(ClassFileWriter.this);
                    i2--;
                }
                i2++;
            }
            sb.merge(locals, locals.length, stack, stack.length, ClassFileWriter.this.itsConstantPool);
            int end = sb.getEnd() - 1;
            ClassFileWriter.this.itsCodeBuffer[end] = -65;
            for (int bci = sb.getStart(); bci < end; bci++) {
                ClassFileWriter.this.itsCodeBuffer[bci] = 0;
            }
        }

        private void executeWorkList() {
            while (this.workListTop > 0) {
                SuperBlock[] superBlockArr = this.workList;
                int i = this.workListTop - 1;
                this.workListTop = i;
                SuperBlock work = superBlockArr[i];
                work.setInQueue(false);
                this.locals = work.getLocals();
                this.stack = work.getStack();
                this.localsTop = this.locals.length;
                this.stackTop = this.stack.length;
                executeBlock(work);
            }
        }

        private void executeBlock(SuperBlock work) {
            int nextIndex;
            int exceptionType;
            int bc = 0;
            int start = work.getStart();
            while (true) {
                int bci = start;
                if (bci >= work.getEnd()) {
                    break;
                }
                bc = ClassFileWriter.this.itsCodeBuffer[bci] & 255;
                int next = execute(bci);
                if (isBranch(bc)) {
                    SuperBlock targetSB = getBranchTarget(bci);
                    flowInto(targetSB);
                } else if (bc == 170) {
                    int switchStart = bci + 1 + (3 & (bci ^ (-1)));
                    int defaultOffset = getOperand(switchStart, 4);
                    SuperBlock targetSB2 = getSuperBlockFromOffset(bci + defaultOffset);
                    flowInto(targetSB2);
                    int low = getOperand(switchStart + 4, 4);
                    int high = getOperand(switchStart + 8, 4);
                    int numCases = (high - low) + 1;
                    int caseBase = switchStart + 12;
                    for (int i = 0; i < numCases; i++) {
                        int label = bci + getOperand(caseBase + (4 * i), 4);
                        SuperBlock targetSB3 = getSuperBlockFromOffset(label);
                        flowInto(targetSB3);
                    }
                }
                for (int i2 = 0; i2 < ClassFileWriter.this.itsExceptionTableTop; i2++) {
                    ExceptionTableEntry ete = ClassFileWriter.this.itsExceptionTable[i2];
                    int startPC = ClassFileWriter.this.getLabelPC(ete.itsStartLabel);
                    int endPC = ClassFileWriter.this.getLabelPC(ete.itsEndLabel);
                    if (bci >= startPC && bci < endPC) {
                        int handlerPC = ClassFileWriter.this.getLabelPC(ete.itsHandlerLabel);
                        SuperBlock sb = getSuperBlockFromOffset(handlerPC);
                        if (ete.itsCatchType == 0) {
                            exceptionType = TypeInfo.OBJECT(ClassFileWriter.this.itsConstantPool.addClass("java/lang/Throwable"));
                        } else {
                            exceptionType = TypeInfo.OBJECT(ete.itsCatchType);
                        }
                        sb.merge(this.locals, this.localsTop, new int[]{exceptionType}, 1, ClassFileWriter.this.itsConstantPool);
                        addToWorkList(sb);
                    }
                }
                start = bci + next;
            }
            if (!isSuperBlockEnd(bc) && (nextIndex = work.getIndex() + 1) < this.superBlocks.length) {
                flowInto(this.superBlocks[nextIndex]);
            }
        }

        private void flowInto(SuperBlock sb) {
            if (sb.merge(this.locals, this.localsTop, this.stack, this.stackTop, ClassFileWriter.this.itsConstantPool)) {
                addToWorkList(sb);
            }
        }

        private void addToWorkList(SuperBlock sb) {
            if (!sb.isInQueue()) {
                sb.setInQueue(true);
                sb.setInitialized(true);
                if (this.workListTop == this.workList.length) {
                    SuperBlock[] tmp = new SuperBlock[this.workListTop * 2];
                    System.arraycopy(this.workList, 0, tmp, 0, this.workListTop);
                    this.workList = tmp;
                }
                SuperBlock[] superBlockArr = this.workList;
                int i = this.workListTop;
                this.workListTop = i + 1;
                superBlockArr[i] = sb;
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code duplicated, block: B:122:0x08fe  */
        /* JADX WARN: Code duplicated, block: B:125:0x090f  */
        /* JADX WARN: Switch 'out' block B:120:0x08f9 for B:67:0x050f already processed. Defaulting to fallback option. */
        private int execute(int bci) {
            int instType;
            int tag;
            int index;
            int bc = ClassFileWriter.this.itsCodeBuffer[bci] & 255;
            int length = 0;
            switch (bc) {
                case 0:
                case 132:
                case 167:
                case 200:
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide && bc != 196) {
                        this.wide = false;
                    }
                    return length;
                case 1:
                    push(5);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 16:
                case 17:
                case 21:
                case 26:
                case 27:
                case 28:
                case 29:
                    push(1);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 9:
                case 10:
                case 22:
                case 30:
                case 31:
                case 32:
                case 33:
                    push(4);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 11:
                case 12:
                case 13:
                case 23:
                case 34:
                case 35:
                case 36:
                case 37:
                    push(2);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 14:
                case 15:
                case 24:
                case 38:
                case 39:
                case 40:
                case 41:
                    push(3);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 18:
                case 19:
                case 20:
                    if (bc == 18) {
                        index = getOperand(bci + 1);
                    } else {
                        index = getOperand(bci + 1, 2);
                    }
                    byte constType = ClassFileWriter.this.itsConstantPool.getConstantType(index);
                    switch (constType) {
                        case 3:
                            push(1);
                            if (length == 0) {
                                length = ClassFileWriter.opcodeLength(bc, this.wide);
                            }
                            if (this.wide) {
                                this.wide = false;
                            }
                            return length;
                        case 4:
                            push(2);
                            if (length == 0) {
                                length = ClassFileWriter.opcodeLength(bc, this.wide);
                            }
                            if (this.wide) {
                                this.wide = false;
                            }
                            return length;
                        case 5:
                            push(4);
                            if (length == 0) {
                                length = ClassFileWriter.opcodeLength(bc, this.wide);
                            }
                            if (this.wide) {
                                this.wide = false;
                            }
                            return length;
                        case 6:
                            push(3);
                            if (length == 0) {
                                length = ClassFileWriter.opcodeLength(bc, this.wide);
                            }
                            if (this.wide) {
                                this.wide = false;
                            }
                            return length;
                        case 7:
                        default:
                            throw new IllegalArgumentException("bad const type " + ((int) constType));
                        case 8:
                            push(TypeInfo.OBJECT("java/lang/String", ClassFileWriter.this.itsConstantPool));
                            if (length == 0) {
                                length = ClassFileWriter.opcodeLength(bc, this.wide);
                            }
                            if (this.wide) {
                                this.wide = false;
                            }
                            return length;
                    }
                case 25:
                    executeALoad(getOperand(bci + 1, this.wide ? 2 : 1));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 42:
                case 43:
                case 44:
                case 45:
                    executeALoad(bc - 42);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 46:
                case 51:
                case 52:
                case 53:
                case 96:
                case 100:
                case 104:
                case 108:
                case 112:
                case 120:
                case 122:
                case 124:
                case 126:
                case 128:
                case 130:
                case 148:
                case 149:
                case 150:
                case 151:
                case 152:
                    pop();
                    pop();
                    push(1);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 47:
                case 97:
                case 101:
                case 105:
                case 109:
                case 113:
                case 121:
                case 123:
                case 125:
                case 127:
                case 129:
                case 131:
                    pop();
                    pop();
                    push(4);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 48:
                case 98:
                case 102:
                case 106:
                case 110:
                case 114:
                    pop();
                    pop();
                    push(2);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 49:
                case 99:
                case 103:
                case 107:
                case 111:
                case 115:
                    pop();
                    pop();
                    push(3);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 50:
                    pop();
                    int typeIndex = pop() >>> 8;
                    String className = (String) ClassFileWriter.this.itsConstantPool.getConstantData(typeIndex);
                    if (className.charAt(0) != '[') {
                        throw new IllegalStateException("bad array type");
                    }
                    String elementDesc = className.substring(1);
                    String elementType = ClassFileWriter.descriptorToInternalName(elementDesc);
                    int typeIndex2 = ClassFileWriter.this.itsConstantPool.addClass(elementType);
                    push(TypeInfo.OBJECT(typeIndex2));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 54:
                    executeStore(getOperand(bci + 1, this.wide ? 2 : 1), 1);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 55:
                    executeStore(getOperand(bci + 1, this.wide ? 2 : 1), 4);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 56:
                    executeStore(getOperand(bci + 1, this.wide ? 2 : 1), 2);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 57:
                    executeStore(getOperand(bci + 1, this.wide ? 2 : 1), 3);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 58:
                    executeAStore(getOperand(bci + 1, this.wide ? 2 : 1));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 59:
                case 60:
                case 61:
                case 62:
                    executeStore(bc - 59, 1);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 63:
                case 64:
                case 65:
                case 66:
                    executeStore(bc - 63, 4);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 67:
                case 68:
                case 69:
                case 70:
                    executeStore(bc - 67, 2);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 71:
                case 72:
                case 73:
                case 74:
                    executeStore(bc - 71, 3);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 75:
                case 76:
                case 77:
                case 78:
                    executeAStore(bc - 75);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                    pop();
                    pop();
                    pop();
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 87:
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 179:
                case 194:
                case 195:
                case 198:
                case 199:
                    pop();
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 88:
                    pop2();
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 89:
                    int type = pop();
                    push(type);
                    push(type);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 90:
                    int type2 = pop();
                    int type3 = pop();
                    push(type2);
                    push(type3);
                    push(type2);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 91:
                    int type4 = pop();
                    long lType = pop2();
                    push(type4);
                    push2(lType);
                    push(type4);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 92:
                    long lType2 = pop2();
                    push2(lType2);
                    push2(lType2);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 93:
                    long lType3 = pop2();
                    int type5 = pop();
                    push2(lType3);
                    push(type5);
                    push2(lType3);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 94:
                    long lType4 = pop2();
                    long lType5 = pop2();
                    push2(lType4);
                    push2(lType5);
                    push2(lType4);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 95:
                    int type6 = pop();
                    int type7 = pop();
                    push(type6);
                    push(type7);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 116:
                case 136:
                case 139:
                case 142:
                case 145:
                case 146:
                case 147:
                case 190:
                case 193:
                    pop();
                    push(1);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 117:
                case 133:
                case 140:
                case 143:
                    pop();
                    push(4);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 118:
                case 134:
                case 137:
                case 144:
                    pop();
                    push(2);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 119:
                case 135:
                case 138:
                case 141:
                    pop();
                    push(3);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 181:
                    pop();
                    pop();
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 168:
                case 169:
                case 171:
                case 197:
                case 201:
                default:
                    throw new IllegalArgumentException("bad opcode: " + bc);
                case 170:
                    int switchStart = bci + 1 + (3 & (bci ^ (-1)));
                    int low = getOperand(switchStart + 4, 4);
                    int high = getOperand(switchStart + 8, 4);
                    length = ((4 * ((high - low) + 4)) + switchStart) - bci;
                    pop();
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 172:
                case 173:
                case 174:
                case 175:
                case 176:
                case 177:
                    clearStack();
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 178:
                    int index2 = getOperand(bci + 1, 2);
                    FieldOrMethodRef f = (FieldOrMethodRef) ClassFileWriter.this.itsConstantPool.getConstantData(index2);
                    String fieldType = ClassFileWriter.descriptorToInternalName(f.getType());
                    push(TypeInfo.fromType(fieldType, ClassFileWriter.this.itsConstantPool));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 180:
                    pop();
                    int index3 = getOperand(bci + 1, 2);
                    FieldOrMethodRef f2 = (FieldOrMethodRef) ClassFileWriter.this.itsConstantPool.getConstantData(index3);
                    String fieldType2 = ClassFileWriter.descriptorToInternalName(f2.getType());
                    push(TypeInfo.fromType(fieldType2, ClassFileWriter.this.itsConstantPool));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 182:
                case 183:
                case 184:
                case 185:
                    int index4 = getOperand(bci + 1, 2);
                    FieldOrMethodRef m = (FieldOrMethodRef) ClassFileWriter.this.itsConstantPool.getConstantData(index4);
                    String methodType = m.getType();
                    String methodName = m.getName();
                    int parameterCount = ClassFileWriter.sizeOfParameters(methodType) >>> 16;
                    for (int i = 0; i < parameterCount; i++) {
                        pop();
                    }
                    if (bc != 184 && ((tag = TypeInfo.getTag((instType = pop()))) == TypeInfo.UNINITIALIZED_VARIABLE(0) || tag == 6)) {
                        if ("<init>".equals(methodName)) {
                            int newType = TypeInfo.OBJECT(ClassFileWriter.this.itsThisClassIndex);
                            initializeTypeInfo(instType, newType);
                        } else {
                            throw new IllegalStateException("bad instance");
                        }
                    }
                    int rParen = methodType.indexOf(41);
                    String returnType = ClassFileWriter.descriptorToInternalName(methodType.substring(rParen + 1));
                    if (!returnType.equals("V")) {
                        push(TypeInfo.fromType(returnType, ClassFileWriter.this.itsConstantPool));
                    }
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 186:
                    int index5 = getOperand(bci + 1, 2);
                    String methodType2 = (String) ClassFileWriter.this.itsConstantPool.getConstantData(index5);
                    int parameterCount2 = ClassFileWriter.sizeOfParameters(methodType2) >>> 16;
                    for (int i2 = 0; i2 < parameterCount2; i2++) {
                        pop();
                    }
                    int rParen2 = methodType2.indexOf(41);
                    String returnType2 = ClassFileWriter.descriptorToInternalName(methodType2.substring(rParen2 + 1));
                    if (!returnType2.equals("V")) {
                        push(TypeInfo.fromType(returnType2, ClassFileWriter.this.itsConstantPool));
                    }
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 187:
                    push(TypeInfo.UNINITIALIZED_VARIABLE(bci));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 188:
                    pop();
                    char componentType = ClassFileWriter.arrayTypeToName(ClassFileWriter.this.itsCodeBuffer[bci + 1]);
                    int index6 = ClassFileWriter.this.itsConstantPool.addClass("[" + componentType);
                    push(TypeInfo.OBJECT((short) index6));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 189:
                    int index7 = getOperand(bci + 1, 2);
                    String className2 = (String) ClassFileWriter.this.itsConstantPool.getConstantData(index7);
                    pop();
                    push(TypeInfo.OBJECT("[L" + className2 + ';', ClassFileWriter.this.itsConstantPool));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 191:
                    int type8 = pop();
                    clearStack();
                    push(type8);
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 192:
                    pop();
                    push(TypeInfo.OBJECT(getOperand(bci + 1, 2)));
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
                case 196:
                    this.wide = true;
                    if (length == 0) {
                        length = ClassFileWriter.opcodeLength(bc, this.wide);
                    }
                    if (this.wide) {
                        this.wide = false;
                    }
                    return length;
            }
        }

        private void executeALoad(int localIndex) {
            int type = getLocal(localIndex);
            int tag = TypeInfo.getTag(type);
            if (tag == 7 || tag == 6 || tag == 8 || tag == 5) {
                push(type);
                return;
            }
            throw new IllegalStateException("bad local variable type: " + type + " at index: " + localIndex);
        }

        private void executeAStore(int localIndex) {
            setLocal(localIndex, pop());
        }

        private void executeStore(int localIndex, int typeInfo) {
            pop();
            setLocal(localIndex, typeInfo);
        }

        private void initializeTypeInfo(int prevType, int newType) {
            initializeTypeInfo(prevType, newType, this.locals, this.localsTop);
            initializeTypeInfo(prevType, newType, this.stack, this.stackTop);
        }

        private void initializeTypeInfo(int prevType, int newType, int[] data, int dataTop) {
            for (int i = 0; i < dataTop; i++) {
                if (data[i] == prevType) {
                    data[i] = newType;
                }
            }
        }

        private int getLocal(int localIndex) {
            if (localIndex < this.localsTop) {
                return this.locals[localIndex];
            }
            return 0;
        }

        private void setLocal(int localIndex, int typeInfo) {
            if (localIndex >= this.localsTop) {
                int[] tmp = new int[localIndex + 1];
                System.arraycopy(this.locals, 0, tmp, 0, this.localsTop);
                this.locals = tmp;
                this.localsTop = localIndex + 1;
            }
            this.locals[localIndex] = typeInfo;
        }

        private void push(int typeInfo) {
            if (this.stackTop == this.stack.length) {
                int[] tmp = new int[Math.max(this.stackTop * 2, 4)];
                System.arraycopy(this.stack, 0, tmp, 0, this.stackTop);
                this.stack = tmp;
            }
            int[] iArr = this.stack;
            int i = this.stackTop;
            this.stackTop = i + 1;
            iArr[i] = typeInfo;
        }

        private int pop() {
            int[] iArr = this.stack;
            int i = this.stackTop - 1;
            this.stackTop = i;
            return iArr[i];
        }

        private void push2(long typeInfo) {
            push((int) (typeInfo & 16777215));
            long typeInfo2 = typeInfo >>> 32;
            if (typeInfo2 != 0) {
                push((int) (typeInfo2 & 16777215));
            }
        }

        private long pop2() {
            long type = pop();
            if (TypeInfo.isTwoWords((int) type)) {
                return type;
            }
            return (type << 32) | ((long) (pop() & 16777215));
        }

        private void clearStack() {
            this.stackTop = 0;
        }

        int computeWriteSize() {
            int writeSize = getWorstCaseWriteSize();
            this.rawStackMap = new byte[writeSize];
            computeRawStackMap();
            return this.rawStackMapTop + 2;
        }

        int write(byte[] data, int offset) {
            int offset2 = ClassFileWriter.putInt16(this.superBlocks.length - 1, data, ClassFileWriter.putInt32(this.rawStackMapTop + 2, data, offset));
            System.arraycopy(this.rawStackMap, 0, data, offset2, this.rawStackMapTop);
            return offset2 + this.rawStackMapTop;
        }

        private void computeRawStackMap() {
            SuperBlock prev = this.superBlocks[0];
            int[] prevLocals = prev.getTrimmedLocals();
            int prevOffset = -1;
            for (int i = 1; i < this.superBlocks.length; i++) {
                SuperBlock current = this.superBlocks[i];
                int[] currentLocals = current.getTrimmedLocals();
                int[] currentStack = current.getStack();
                int offsetDelta = (current.getStart() - prevOffset) - 1;
                if (currentStack.length == 0) {
                    int last = prevLocals.length > currentLocals.length ? currentLocals.length : prevLocals.length;
                    int delta = Math.abs(prevLocals.length - currentLocals.length);
                    int j = 0;
                    while (j < last && prevLocals[j] == currentLocals[j]) {
                        j++;
                    }
                    if (j == currentLocals.length && delta == 0) {
                        writeSameFrame(offsetDelta);
                    } else if (j == currentLocals.length && delta <= 3) {
                        writeChopFrame(delta, offsetDelta);
                    } else if (j == prevLocals.length && delta <= 3) {
                        writeAppendFrame(currentLocals, delta, offsetDelta);
                    } else {
                        writeFullFrame(currentLocals, currentStack, offsetDelta);
                    }
                } else if (currentStack.length == 1) {
                    if (Arrays.equals(prevLocals, currentLocals)) {
                        writeSameLocalsOneStackItemFrame(currentStack, offsetDelta);
                    } else {
                        writeFullFrame(currentLocals, currentStack, offsetDelta);
                    }
                } else {
                    writeFullFrame(currentLocals, currentStack, offsetDelta);
                }
                prevLocals = currentLocals;
                prevOffset = current.getStart();
            }
        }

        private int getWorstCaseWriteSize() {
            return (this.superBlocks.length - 1) * (7 + (ClassFileWriter.this.itsMaxLocals * 3) + (ClassFileWriter.this.itsMaxStack * 3));
        }

        private void writeSameFrame(int offsetDelta) {
            if (offsetDelta <= 63) {
                byte[] bArr = this.rawStackMap;
                int i = this.rawStackMapTop;
                this.rawStackMapTop = i + 1;
                bArr[i] = (byte) offsetDelta;
                return;
            }
            byte[] bArr2 = this.rawStackMap;
            int i2 = this.rawStackMapTop;
            this.rawStackMapTop = i2 + 1;
            bArr2[i2] = -5;
            this.rawStackMapTop = ClassFileWriter.putInt16(offsetDelta, this.rawStackMap, this.rawStackMapTop);
        }

        private void writeSameLocalsOneStackItemFrame(int[] stack, int offsetDelta) {
            if (offsetDelta <= 63) {
                byte[] bArr = this.rawStackMap;
                int i = this.rawStackMapTop;
                this.rawStackMapTop = i + 1;
                bArr[i] = (byte) (64 + offsetDelta);
            } else {
                byte[] bArr2 = this.rawStackMap;
                int i2 = this.rawStackMapTop;
                this.rawStackMapTop = i2 + 1;
                bArr2[i2] = -9;
                this.rawStackMapTop = ClassFileWriter.putInt16(offsetDelta, this.rawStackMap, this.rawStackMapTop);
            }
            writeType(stack[0]);
        }

        private void writeFullFrame(int[] locals, int[] stack, int offsetDelta) {
            byte[] bArr = this.rawStackMap;
            int i = this.rawStackMapTop;
            this.rawStackMapTop = i + 1;
            bArr[i] = -1;
            this.rawStackMapTop = ClassFileWriter.putInt16(offsetDelta, this.rawStackMap, this.rawStackMapTop);
            this.rawStackMapTop = ClassFileWriter.putInt16(locals.length, this.rawStackMap, this.rawStackMapTop);
            this.rawStackMapTop = writeTypes(locals);
            this.rawStackMapTop = ClassFileWriter.putInt16(stack.length, this.rawStackMap, this.rawStackMapTop);
            this.rawStackMapTop = writeTypes(stack);
        }

        private void writeAppendFrame(int[] locals, int localsDelta, int offsetDelta) {
            int start = locals.length - localsDelta;
            byte[] bArr = this.rawStackMap;
            int i = this.rawStackMapTop;
            this.rawStackMapTop = i + 1;
            bArr[i] = (byte) (251 + localsDelta);
            this.rawStackMapTop = ClassFileWriter.putInt16(offsetDelta, this.rawStackMap, this.rawStackMapTop);
            this.rawStackMapTop = writeTypes(locals, start);
        }

        private void writeChopFrame(int localsDelta, int offsetDelta) {
            byte[] bArr = this.rawStackMap;
            int i = this.rawStackMapTop;
            this.rawStackMapTop = i + 1;
            bArr[i] = (byte) (251 - localsDelta);
            this.rawStackMapTop = ClassFileWriter.putInt16(offsetDelta, this.rawStackMap, this.rawStackMapTop);
        }

        private int writeTypes(int[] types) {
            return writeTypes(types, 0);
        }

        private int writeTypes(int[] types, int start) {
            for (int i = start; i < types.length; i++) {
                this.rawStackMapTop = writeType(types[i]);
            }
            return this.rawStackMapTop;
        }

        private int writeType(int type) {
            int tag = type & 255;
            byte[] bArr = this.rawStackMap;
            int i = this.rawStackMapTop;
            this.rawStackMapTop = i + 1;
            bArr[i] = (byte) tag;
            if (tag == 7 || tag == 8) {
                this.rawStackMapTop = ClassFileWriter.putInt16(type >>> 8, this.rawStackMap, this.rawStackMapTop);
            }
            return this.rawStackMapTop;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static char arrayTypeToName(int type) {
        switch (type) {
            case 4:
                return 'Z';
            case 5:
                return 'C';
            case 6:
                return 'F';
            case 7:
                return 'D';
            case 8:
                return 'B';
            case 9:
                return 'S';
            case 10:
                return 'I';
            case 11:
                return 'J';
            default:
                throw new IllegalArgumentException("bad operand");
        }
    }

    private static String classDescriptorToInternalName(String descriptor) {
        return descriptor.substring(1, descriptor.length() - 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String descriptorToInternalName(String descriptor) {
        switch (descriptor.charAt(0)) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'V':
            case 'Z':
            case '[':
                return descriptor;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'W':
            case 'X':
            case 'Y':
            default:
                throw new IllegalArgumentException("bad descriptor:" + descriptor);
            case 'L':
                return classDescriptorToInternalName(descriptor);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] createInitialLocals() {
        int[] initialLocals = new int[this.itsMaxLocals];
        int localsTop = 0;
        if ((this.itsCurrentMethod.getFlags() & 8) == 0) {
            if ("<init>".equals(this.itsCurrentMethod.getName())) {
                localsTop = 0 + 1;
                initialLocals[0] = 6;
            } else {
                localsTop = 0 + 1;
                initialLocals[0] = TypeInfo.OBJECT(this.itsThisClassIndex);
            }
        }
        String type = this.itsCurrentMethod.getType();
        int lParenIndex = type.indexOf(40);
        int rParenIndex = type.indexOf(41);
        if (lParenIndex != 0 || rParenIndex < 0) {
            throw new IllegalArgumentException("bad method type");
        }
        int start = lParenIndex + 1;
        StringBuilder paramType = new StringBuilder();
        while (start < rParenIndex) {
            switch (type.charAt(start)) {
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'I':
                case 'J':
                case 'S':
                case 'Z':
                    paramType.append(type.charAt(start));
                    start++;
                    break;
                case 'L':
                    int end = type.indexOf(59, start) + 1;
                    String name = type.substring(start, end);
                    paramType.append(name);
                    start = end;
                    break;
                case '[':
                    paramType.append('[');
                    start++;
                    continue;
            }
            String internalType = descriptorToInternalName(paramType.toString());
            int typeInfo = TypeInfo.fromType(internalType, this.itsConstantPool);
            int i = localsTop;
            localsTop++;
            initialLocals[i] = typeInfo;
            if (TypeInfo.isTwoWords(typeInfo)) {
                localsTop++;
            }
            paramType.setLength(0);
        }
        return initialLocals;
    }

    public void write(OutputStream oStream) throws IOException {
        byte[] array = toByteArray();
        oStream.write(array);
    }

    private int getWriteSize() {
        if (this.itsSourceFileNameIndex != 0) {
            this.itsConstantPool.addUtf8("SourceFile");
        }
        int size = 0 + 8;
        int size2 = size + this.itsConstantPool.getWriteSize() + 2 + 2 + 2 + 2 + (2 * this.itsInterfaces.size()) + 2;
        for (int i = 0; i < this.itsFields.size(); i++) {
            size2 += ((ClassFileField) this.itsFields.get(i)).getWriteSize();
        }
        int size3 = size2 + 2;
        for (int i2 = 0; i2 < this.itsMethods.size(); i2++) {
            size3 += ((ClassFileMethod) this.itsMethods.get(i2)).getWriteSize();
        }
        int size4 = size3 + 2;
        if (this.itsSourceFileNameIndex != 0) {
            size4 = size4 + 2 + 4 + 2;
        }
        if (this.itsBootstrapMethods != null) {
            size4 = size4 + 2 + 4 + 2 + this.itsBootstrapMethodsLength;
        }
        return size4;
    }

    public byte[] toByteArray() {
        short bootstrapMethodsAttrNameIndex = 0;
        int attributeCount = 0;
        short sourceFileAttributeNameIndex = 0;
        if (this.itsBootstrapMethods != null) {
            attributeCount = 0 + 1;
            bootstrapMethodsAttrNameIndex = this.itsConstantPool.addUtf8("BootstrapMethods");
        }
        if (this.itsSourceFileNameIndex != 0) {
            attributeCount++;
            sourceFileAttributeNameIndex = this.itsConstantPool.addUtf8("SourceFile");
        }
        int dataSize = getWriteSize();
        byte[] data = new byte[dataSize];
        int offset = putInt32(FileHeaderConstant, data, 0);
        int offset2 = putInt16(this.itsInterfaces.size(), data, putInt16(this.itsSuperClassIndex, data, putInt16(this.itsThisClassIndex, data, putInt16(this.itsFlags, data, this.itsConstantPool.write(data, putInt16(MajorVersion, data, putInt16(MinorVersion, data, offset)))))));
        for (int i = 0; i < this.itsInterfaces.size(); i++) {
            int interfaceIndex = ((Short) this.itsInterfaces.get(i)).shortValue();
            offset2 = putInt16(interfaceIndex, data, offset2);
        }
        int offset3 = putInt16(this.itsFields.size(), data, offset2);
        for (int i2 = 0; i2 < this.itsFields.size(); i2++) {
            ClassFileField field = (ClassFileField) this.itsFields.get(i2);
            offset3 = field.write(data, offset3);
        }
        int offset4 = putInt16(this.itsMethods.size(), data, offset3);
        for (int i3 = 0; i3 < this.itsMethods.size(); i3++) {
            ClassFileMethod method = (ClassFileMethod) this.itsMethods.get(i3);
            offset4 = method.write(data, offset4);
        }
        int offset5 = putInt16(attributeCount, data, offset4);
        if (this.itsBootstrapMethods != null) {
            offset5 = putInt16(this.itsBootstrapMethods.size(), data, putInt32(this.itsBootstrapMethodsLength + 2, data, putInt16(bootstrapMethodsAttrNameIndex, data, offset5)));
            for (int i4 = 0; i4 < this.itsBootstrapMethods.size(); i4++) {
                BootstrapEntry entry = (BootstrapEntry) this.itsBootstrapMethods.get(i4);
                System.arraycopy(entry.code, 0, data, offset5, entry.code.length);
                offset5 += entry.code.length;
            }
        }
        if (this.itsSourceFileNameIndex != 0) {
            offset5 = putInt16(this.itsSourceFileNameIndex, data, putInt32(2, data, putInt16(sourceFileAttributeNameIndex, data, offset5)));
        }
        if (offset5 != dataSize) {
            throw new RuntimeException();
        }
        return data;
    }

    static int putInt64(long value, byte[] array, int offset) {
        return putInt32((int) value, array, putInt32((int) (value >>> 32), array, offset));
    }

    private static void badStack(int value) {
        String s;
        if (value < 0) {
            s = "Stack underflow: " + value;
        } else {
            s = "Too big stack: " + value;
        }
        throw new IllegalStateException(s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:34:0x01a1  */
    /* JADX WARN: Code duplicated, block: B:36:0x0220  */
    /* JADX WARN: Code duplicated, block: B:37:0x0225  */
    /* JADX WARN: Code duplicated, block: B:38:0x0228 A[FALL_THROUGH, PHI: r10
      0x0228: PHI (r10v4 'stackDiff' int) = (r10v2 'stackDiff' int), (r10v6 'stackDiff' int) binds: [B:35:0x01a8, B:37:0x0225] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:41:0x022f  */
    /* JADX WARN: Failed to find 'out' block for switch in B:14:0x003e. Please report as an issue. */
    public static int sizeOfParameters(String pString) {
        int index;
        int semicolon;
        int length = pString.length();
        int rightParenthesis = pString.lastIndexOf(41);
        if (3 <= length && pString.charAt(0) == '(' && 1 <= rightParenthesis && rightParenthesis + 1 < length) {
            boolean ok = true;
            int index2 = 1;
            int stackDiff = 0;
            int count = 0;
            while (index2 != rightParenthesis) {
                switch (pString.charAt(index2)) {
                    case 'B':
                    case 'C':
                    case 'F':
                    case 'I':
                    case 'S':
                    case 'Z':
                        stackDiff--;
                        count++;
                        index2++;
                        break;
                    case 'D':
                    case 'J':
                        stackDiff--;
                        stackDiff--;
                        count++;
                        index2++;
                        break;
                    case 'E':
                    case 'G':
                    case 'H':
                    case 'K':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    default:
                        ok = false;
                        break;
                    case 'L':
                        stackDiff--;
                        count++;
                        index = index2 + 1;
                        semicolon = pString.indexOf(59, index);
                        if (index + 1 <= semicolon || semicolon >= rightParenthesis) {
                            ok = false;
                        } else {
                            index2 = semicolon + 1;
                        }
                        break;
                    case '[':
                        index2++;
                        int iCharAt = pString.charAt(index2);
                        while (true) {
                            int c = iCharAt;
                            if (c == 91) {
                                index2++;
                                iCharAt = pString.charAt(index2);
                            } else {
                                switch (c) {
                                    case 66:
                                    case 67:
                                    case 68:
                                    case 70:
                                    case 73:
                                    case 74:
                                    case 83:
                                    case 90:
                                        stackDiff--;
                                        count++;
                                        index2++;
                                        continue;
                                    case 69:
                                    case 71:
                                    case 72:
                                    case 75:
                                    case 77:
                                    case 78:
                                    case 79:
                                    case 80:
                                    case 81:
                                    case 82:
                                    case 84:
                                    case 85:
                                    case 86:
                                    case 87:
                                    case 88:
                                    case 89:
                                    default:
                                        ok = false;
                                        break;
                                    case 76:
                                        break;
                                }
                                stackDiff--;
                                count++;
                                index = index2 + 1;
                                semicolon = pString.indexOf(59, index);
                                if (index + 1 <= semicolon) {
                                }
                                ok = false;
                            }
                            break;
                        }
                        break;
                }
                if (ok) {
                    switch (pString.charAt(rightParenthesis + 1)) {
                        case 'D':
                        case 'J':
                            stackDiff++;
                        case 'B':
                        case 'C':
                        case 'F':
                        case 'I':
                        case 'L':
                        case 'S':
                        case 'Z':
                        case '[':
                            stackDiff++;
                            break;
                        case 'E':
                        case 'G':
                        case 'H':
                        case 'K':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'T':
                        case 'U':
                        case 'W':
                        case 'X':
                        case 'Y':
                        default:
                            ok = false;
                            break;
                        case 'V':
                            break;
                    }
                    if (ok) {
                        return (count << 16) | (65535 & stackDiff);
                    }
                }
            }
            if (ok) {
                switch (pString.charAt(rightParenthesis + 1)) {
                    case 'D':
                    case 'J':
                        stackDiff++;
                    case 'B':
                    case 'C':
                    case 'F':
                    case 'I':
                    case 'L':
                    case 'S':
                    case 'Z':
                    case '[':
                        stackDiff++;
                        break;
                    case 'E':
                    case 'G':
                    case 'H':
                    case 'K':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'T':
                    case 'U':
                    case 'W':
                    case 'X':
                    case 'Y':
                    default:
                        ok = false;
                        break;
                    case 'V':
                        break;
                }
                if (ok) {
                    return (count << 16) | (65535 & stackDiff);
                }
            }
        }
        throw new IllegalArgumentException("Bad parameter signature: " + pString);
    }

    static int putInt16(int value, byte[] array, int offset) {
        array[offset + 0] = (byte) (value >>> 8);
        array[offset + 1] = (byte) value;
        return offset + 2;
    }

    static int putInt32(int value, byte[] array, int offset) {
        array[offset + 0] = (byte) (value >>> 24);
        array[offset + 1] = (byte) (value >>> 16);
        array[offset + 2] = (byte) (value >>> 8);
        array[offset + 3] = (byte) value;
        return offset + 4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int opcodeLength(int opcode, boolean wide) {
        switch (opcode) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 190:
            case 191:
            case 194:
            case 195:
            case 196:
            case 202:
            case 254:
            case 255:
                return 1;
            case 16:
            case 18:
            case 188:
                return 2;
            case 17:
            case 19:
            case 20:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 187:
            case 189:
            case 192:
            case 193:
            case 198:
            case 199:
                return 3;
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 169:
                return wide ? 3 : 2;
            case 132:
                return wide ? 5 : 3;
            case 170:
            case 171:
            case 203:
            case Barcode128.STARTB /* 204 */:
            case 205:
            case WinError.ERROR_FILENAME_EXCED_RANGE /* 206 */:
            case WinError.ERROR_RING2_STACK_IN_USE /* 207 */:
            case WinError.ERROR_META_EXPANSION_TOO_LONG /* 208 */:
            case WinError.ERROR_INVALID_SIGNAL_NUMBER /* 209 */:
            case WinError.ERROR_THREAD_1_INACTIVE /* 210 */:
            case 211:
            case WinError.ERROR_LOCKED /* 212 */:
            case 213:
            case WinError.ERROR_TOO_MANY_MODULES /* 214 */:
            case WinError.ERROR_NESTING_NOT_ALLOWED /* 215 */:
            case WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH /* 216 */:
            case WinError.ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY /* 217 */:
            case WinError.ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY /* 218 */:
            case 219:
            case WinError.ERROR_FILE_CHECKED_OUT /* 220 */:
            case WinError.ERROR_CHECKOUT_REQUIRED /* 221 */:
            case WinError.ERROR_BAD_FILE_TYPE /* 222 */:
            case WinError.ERROR_FILE_TOO_LARGE /* 223 */:
            case 224:
            case WinError.ERROR_VIRUS_INFECTED /* 225 */:
            case WinError.ERROR_VIRUS_DELETED /* 226 */:
            case 227:
            case 228:
            case WinError.ERROR_PIPE_LOCAL /* 229 */:
            case WinError.ERROR_BAD_PIPE /* 230 */:
            case WinError.ERROR_PIPE_BUSY /* 231 */:
            case WinError.ERROR_NO_DATA /* 232 */:
            case WinError.ERROR_PIPE_NOT_CONNECTED /* 233 */:
            case WinError.ERROR_MORE_DATA /* 234 */:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case DHCPOptions.OPTION_PROXY_AUTODISCOVERY /* 252 */:
            case 253:
            default:
                throw new IllegalArgumentException("Bad opcode: " + opcode);
            case 185:
            case 186:
            case 200:
            case 201:
                return 5;
            case 197:
                return 4;
        }
    }

    private static int opcodeCount(int opcode) {
        switch (opcode) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 190:
            case 191:
            case 194:
            case 195:
            case 196:
            case 202:
            case 254:
            case 255:
                return 0;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 187:
            case 188:
            case 189:
            case 192:
            case 193:
            case 198:
            case 199:
            case 200:
            case 201:
                return 1;
            case 132:
            case 197:
                return 2;
            case 170:
            case 171:
                return -1;
            case 186:
            case 203:
            case Barcode128.STARTB /* 204 */:
            case 205:
            case WinError.ERROR_FILENAME_EXCED_RANGE /* 206 */:
            case WinError.ERROR_RING2_STACK_IN_USE /* 207 */:
            case WinError.ERROR_META_EXPANSION_TOO_LONG /* 208 */:
            case WinError.ERROR_INVALID_SIGNAL_NUMBER /* 209 */:
            case WinError.ERROR_THREAD_1_INACTIVE /* 210 */:
            case 211:
            case WinError.ERROR_LOCKED /* 212 */:
            case 213:
            case WinError.ERROR_TOO_MANY_MODULES /* 214 */:
            case WinError.ERROR_NESTING_NOT_ALLOWED /* 215 */:
            case WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH /* 216 */:
            case WinError.ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY /* 217 */:
            case WinError.ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY /* 218 */:
            case 219:
            case WinError.ERROR_FILE_CHECKED_OUT /* 220 */:
            case WinError.ERROR_CHECKOUT_REQUIRED /* 221 */:
            case WinError.ERROR_BAD_FILE_TYPE /* 222 */:
            case WinError.ERROR_FILE_TOO_LARGE /* 223 */:
            case 224:
            case WinError.ERROR_VIRUS_INFECTED /* 225 */:
            case WinError.ERROR_VIRUS_DELETED /* 226 */:
            case 227:
            case 228:
            case WinError.ERROR_PIPE_LOCAL /* 229 */:
            case WinError.ERROR_BAD_PIPE /* 230 */:
            case WinError.ERROR_PIPE_BUSY /* 231 */:
            case WinError.ERROR_NO_DATA /* 232 */:
            case WinError.ERROR_PIPE_NOT_CONNECTED /* 233 */:
            case WinError.ERROR_MORE_DATA /* 234 */:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case DHCPOptions.OPTION_PROXY_AUTODISCOVERY /* 252 */:
            case 253:
            default:
                throw new IllegalArgumentException("Bad opcode: " + opcode);
        }
    }

    private static int stackChange(int opcode) {
        switch (opcode) {
            case 0:
            case 47:
            case 49:
            case 95:
            case 116:
            case 117:
            case 118:
            case 119:
            case 132:
            case 134:
            case 138:
            case 139:
            case 143:
            case 145:
            case 146:
            case 147:
            case 167:
            case 169:
            case 177:
            case 178:
            case 179:
            case 184:
            case 186:
            case 188:
            case 189:
            case 190:
            case 192:
            case 193:
            case 196:
            case 200:
            case 202:
            case 254:
            case 255:
                return 0;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 11:
            case 12:
            case 13:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 23:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 34:
            case 35:
            case 36:
            case 37:
            case 42:
            case 43:
            case 44:
            case 45:
            case 89:
            case 90:
            case 91:
            case 133:
            case 135:
            case 140:
            case 141:
            case 168:
            case 187:
            case 197:
            case 201:
                return 1;
            case 9:
            case 10:
            case 14:
            case 15:
            case 20:
            case 22:
            case 24:
            case 30:
            case 31:
            case 32:
            case 33:
            case 38:
            case 39:
            case 40:
            case 41:
            case 92:
            case 93:
            case 94:
                return 2;
            case 46:
            case 48:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 56:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 67:
            case 68:
            case 69:
            case 70:
            case 75:
            case 76:
            case 77:
            case 78:
            case 87:
            case 96:
            case 98:
            case 100:
            case 102:
            case 104:
            case 106:
            case 108:
            case 110:
            case 112:
            case 114:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 128:
            case 130:
            case 136:
            case 137:
            case 142:
            case 144:
            case 149:
            case 150:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 170:
            case 171:
            case 172:
            case 174:
            case 176:
            case 180:
            case 181:
            case 182:
            case 183:
            case 185:
            case 191:
            case 194:
            case 195:
            case 198:
            case 199:
                return -1;
            case 55:
            case 57:
            case 63:
            case 64:
            case 65:
            case 66:
            case 71:
            case 72:
            case 73:
            case 74:
            case 88:
            case 97:
            case 99:
            case 101:
            case 103:
            case 105:
            case 107:
            case 109:
            case 111:
            case 113:
            case 115:
            case 127:
            case 129:
            case 131:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 173:
            case 175:
                return -2;
            case 79:
            case 81:
            case 83:
            case 84:
            case 85:
            case 86:
            case 148:
            case 151:
            case 152:
                return -3;
            case 80:
            case 82:
                return -4;
            case 203:
            case Barcode128.STARTB /* 204 */:
            case 205:
            case WinError.ERROR_FILENAME_EXCED_RANGE /* 206 */:
            case WinError.ERROR_RING2_STACK_IN_USE /* 207 */:
            case WinError.ERROR_META_EXPANSION_TOO_LONG /* 208 */:
            case WinError.ERROR_INVALID_SIGNAL_NUMBER /* 209 */:
            case WinError.ERROR_THREAD_1_INACTIVE /* 210 */:
            case 211:
            case WinError.ERROR_LOCKED /* 212 */:
            case 213:
            case WinError.ERROR_TOO_MANY_MODULES /* 214 */:
            case WinError.ERROR_NESTING_NOT_ALLOWED /* 215 */:
            case WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH /* 216 */:
            case WinError.ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY /* 217 */:
            case WinError.ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY /* 218 */:
            case 219:
            case WinError.ERROR_FILE_CHECKED_OUT /* 220 */:
            case WinError.ERROR_CHECKOUT_REQUIRED /* 221 */:
            case WinError.ERROR_BAD_FILE_TYPE /* 222 */:
            case WinError.ERROR_FILE_TOO_LARGE /* 223 */:
            case 224:
            case WinError.ERROR_VIRUS_INFECTED /* 225 */:
            case WinError.ERROR_VIRUS_DELETED /* 226 */:
            case 227:
            case 228:
            case WinError.ERROR_PIPE_LOCAL /* 229 */:
            case WinError.ERROR_BAD_PIPE /* 230 */:
            case WinError.ERROR_PIPE_BUSY /* 231 */:
            case WinError.ERROR_NO_DATA /* 232 */:
            case WinError.ERROR_PIPE_NOT_CONNECTED /* 233 */:
            case WinError.ERROR_MORE_DATA /* 234 */:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case DHCPOptions.OPTION_PROXY_AUTODISCOVERY /* 252 */:
            case 253:
            default:
                throw new IllegalArgumentException("Bad opcode: " + opcode);
        }
    }

    private static String bytecodeStr(int code) {
        return "";
    }

    final char[] getCharBuffer(int minimalSize) {
        if (minimalSize > this.tmpCharBuffer.length) {
            int newSize = this.tmpCharBuffer.length * 2;
            if (minimalSize > newSize) {
                newSize = minimalSize;
            }
            this.tmpCharBuffer = new char[newSize];
        }
        return this.tmpCharBuffer;
    }

    private void addSuperBlockStart(int pc) {
        if (GenerateStackMap) {
            if (this.itsSuperBlockStarts == null) {
                this.itsSuperBlockStarts = new int[4];
            } else if (this.itsSuperBlockStarts.length == this.itsSuperBlockStartsTop) {
                int[] tmp = new int[this.itsSuperBlockStartsTop * 2];
                System.arraycopy(this.itsSuperBlockStarts, 0, tmp, 0, this.itsSuperBlockStartsTop);
                this.itsSuperBlockStarts = tmp;
            }
            int[] iArr = this.itsSuperBlockStarts;
            int i = this.itsSuperBlockStartsTop;
            this.itsSuperBlockStartsTop = i + 1;
            iArr[i] = pc;
        }
    }

    private void finalizeSuperBlockStarts() {
        if (GenerateStackMap) {
            for (int i = 0; i < this.itsExceptionTableTop; i++) {
                ExceptionTableEntry ete = this.itsExceptionTable[i];
                int handlerPC = getLabelPC(ete.itsHandlerLabel);
                addSuperBlockStart(handlerPC);
            }
            Arrays.sort(this.itsSuperBlockStarts, 0, this.itsSuperBlockStartsTop);
            int prev = this.itsSuperBlockStarts[0];
            int copyTo = 1;
            for (int i2 = 1; i2 < this.itsSuperBlockStartsTop; i2++) {
                int curr = this.itsSuperBlockStarts[i2];
                if (prev != curr) {
                    if (copyTo != i2) {
                        this.itsSuperBlockStarts[copyTo] = curr;
                    }
                    copyTo++;
                    prev = curr;
                }
            }
            this.itsSuperBlockStartsTop = copyTo;
            if (this.itsSuperBlockStarts[copyTo - 1] == this.itsCodeBufferTop) {
                this.itsSuperBlockStartsTop--;
            }
        }
    }

    static {
        InputStream is = null;
        try {
            InputStream is2 = ClassFileWriter.class.getResourceAsStream("ClassFileWriter.class");
            if (is2 == null) {
                is2 = ClassLoader.getSystemResourceAsStream("org/mozilla/classfile/ClassFileWriter.class");
            }
            byte[] header = new byte[8];
            int read = 0;
            while (read < 8) {
                int c = is2.read(header, read, 8 - read);
                if (c < 0) {
                    throw new IOException();
                }
                read += c;
            }
            int minor = (header[4] << 8) | (header[5] & 255);
            int major = (header[6] << 8) | (header[7] & 255);
            MinorVersion = minor;
            MajorVersion = major;
            GenerateStackMap = major >= 50;
            if (is2 != null) {
                try {
                    is2.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception e2) {
            MinorVersion = 0;
            MajorVersion = 48;
            GenerateStackMap = 48 >= 50;
            if (0 != 0) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            MinorVersion = 0;
            MajorVersion = 48;
            GenerateStackMap = 48 >= 50;
            if (0 != 0) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ClassFileWriter$BootstrapEntry.class */
    final class BootstrapEntry {
        final byte[] code;

        BootstrapEntry(MHandle bsm, Object... bsmArgs) {
            int length = 4 + (bsmArgs.length * 2);
            this.code = new byte[length];
            ClassFileWriter.putInt16(ClassFileWriter.this.itsConstantPool.addMethodHandle(bsm), this.code, 0);
            ClassFileWriter.putInt16(bsmArgs.length, this.code, 2);
            for (int i = 0; i < bsmArgs.length; i++) {
                ClassFileWriter.putInt16(ClassFileWriter.this.itsConstantPool.addConstant(bsmArgs[i]), this.code, 4 + (i * 2));
            }
        }

        public boolean equals(Object obj) {
            return (obj instanceof BootstrapEntry) && Arrays.equals(this.code, ((BootstrapEntry) obj).code);
        }

        public int hashCode() {
            return Arrays.hashCode(this.code) ^ (-1);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ClassFileWriter$MHandle.class */
    public static final class MHandle {
        final byte tag;
        final String owner;
        final String name;
        final String desc;

        public MHandle(byte tag, String owner, String name, String desc) {
            this.tag = tag;
            this.owner = owner;
            this.name = name;
            this.desc = desc;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MHandle)) {
                return false;
            }
            MHandle mh = (MHandle) obj;
            return this.tag == mh.tag && this.owner.equals(mh.owner) && this.name.equals(mh.name) && this.desc.equals(mh.desc);
        }

        public int hashCode() {
            return this.tag + (this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode());
        }

        public String toString() {
            return this.owner + '.' + this.name + this.desc + " (" + ((int) this.tag) + ')';
        }
    }
}
