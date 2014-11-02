class Main {
    public static void main(String[] a){
        System.out.println(new A().run());
    }
}

// TE

class A extends B {
    public int run() {
        int x;
        x = 1;
        return x;
    }
}

class B extends C {
    public int run() {
        int x;
        x = 1;
        return x;
    }
}

class C extends D {
    public int run() {
        int x;
        x = 1;
        return x;
    }
}

class D extends A {
    public int run() {
        int x;
        x = 1;
        return x;
    }
}

