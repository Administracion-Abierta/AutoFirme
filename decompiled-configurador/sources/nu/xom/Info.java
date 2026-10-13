package nu.xom;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Info.class */
class Info {
    Info() {
    }

    public static void main(String[] strArr) {
        String line;
        try {
            line = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("nu/xom/version.txt"), "UTF-8")).readLine();
        } catch (Exception e) {
            line = "1.2.4b1 or later";
        }
        System.out.println(new StringBuffer().append("This is XOM ").append(line).append(", a new XML Object Model.").toString());
        System.out.println("Copyright 2002-2009 Elliotte Rusty Harold");
        System.out.println("http://www.xom.nu/");
        System.out.println();
        System.out.println("XOM is a class library intended to be used with other programs.");
        System.out.println("By itself, it doesn't really do anything.");
        System.out.println("For more information see http://www.xom.nu/");
        System.out.println();
        System.out.println("This library is free software; you can redistribute it and/or modify it");
        System.out.println("under the terms of version 2.1 of the GNU Lesser General Public License");
        System.out.println("as published by the Free Software Foundation.");
        System.out.println();
        System.out.println("This library is distributed in the hope that it will be useful,");
        System.out.println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
        System.out.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. ");
        System.out.println("See the GNU Lesser General Public License for more details.");
        System.out.println();
        System.out.println("You should have received a copy of the GNU Lesser General");
        System.out.println("Public License along with this library; if not, write to the");
        System.out.println();
        System.out.println("Free Software Foundation, Inc.");
        System.out.println("59 Temple Place");
        System.out.println("Suite 330,");
        System.out.println("Boston, MA  02111-1307");
        System.out.println("USA");
    }
}
