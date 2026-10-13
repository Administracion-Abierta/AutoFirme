package org.mozilla.javascript;

/* JADX INFO: compiled from: JavaMembers.java */
/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/BeanProperty.class */
class BeanProperty {
    MemberBox getter;
    MemberBox setter;
    NativeJavaMethod setters;

    BeanProperty(MemberBox getter, MemberBox setter, NativeJavaMethod setters) {
        this.getter = getter;
        this.setter = setter;
        this.setters = setters;
    }
}
