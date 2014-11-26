/*
The output should be:
0
0
0
0
0
10
0
1
2
3
4
5
6
999999999
7
8
9
10
11
12
31
32
33
*/

class Main {
    public static void main(String[] aa){
        boolean b;
        A a;
        C cc;
        a = new A();
        b = a.init();
        b = a.test_while();
        b = a.test_if_main();
        b = new B().init_and_test_array_alloc();
        // seperation mark
        System.out.println(999999999);
        cc = new C();
        b = cc.c_init();


        
    }
}

class A {
    int[] a;
    int[] b;
    boolean c;
    boolean d;
    int e;

    public boolean init()
    {
        a = new int[10];
        b = new int[this.get_int(5)];
        c = false;
        d = false;
        e = 1984;
        return true;
    }


    // an insane initialization
    public boolean test_while()
    {
        int a;
        a = 5;
        while(a < 10)
        {
            while(a < 10)
            {
                while(a < 10)
                {
                    while((a < 10) && (a < 15))
                    {
                        a = this.get_int(a + 1);
                        if((this.get_if_self()).test_if_main())
                        {
                            a = this.get_int(a);
                        }
                        else
                        {
                            a = this.get_int(((a - 1) + 1) * 1) ;
                        }
                    }
                }
            }
        }
        System.out.println(a); // print 3
        return true;
    }

    public A get_if_self()
    {
        return this;
    }
    
    public boolean test_if_main()
    {
        if ( !(!(!(new A().test_if_0())) && (!(this.test_if2()))))
        {
            System.out.println(0); // should print this;
        }
        else
        {
            System.out.println(1); // 
        }
        return ((((true && false) && true) && false) && true);
    }

    public boolean test_if_0()
    {
        boolean b;
        b = false;
        b = ((this.test_if1()) && (new A().test_if2())) && (new A().test_if1()); // will be false
        return b;
    }

    public boolean test_if1()
    {
        return (false && true) && true; // return false
    }

    public boolean test_if2()
    {
        return ((false && true) && false) && (this.test_if1()); // return false
    }


    public int get_int(int a)
    {
        return a;
    }

    public boolean set_e()
    {
        e = 1;
        return true;
    }

    public int get_e()
    {
        return e;
    }

    public boolean another_A_init()
    {
        a = (new B()).get_arr_3();
        return true;
    }

    public boolean iter_A_a()
    {
        System.out.println(a[0]);
        System.out.println(a[1]);
        System.out.println(a[2]);
        return true;
    }
}

class B extends A {
    int[] a;
    int[] b;
    boolean c;
    boolean d;
    int e;

    // check array allocation
    public boolean init_and_test_array_alloc()
    {
        a = new B().get_arr_1();
        b = this.get_arr_2();
        System.out.println(a[0]);
        System.out.println(a[1]);
        System.out.println(a[2]);
        System.out.println(b[0]);
        System.out.println(b[1]);
        System.out.println(b[2]);
        return false;
    }




    // no! you cannot get an array easily!
    public int[] get_arr_1(){
        int[] a1;
        a1 = new int[3];
        a1[0] = 1;
        a1[1] = 2;
        a1[2] = 3;
        return a1;

    }

    public int[] get_arr_2(){
        int[] a1;
        a1 = new int[4];
        a1[0] = 4;
        a1[1] = 5;
        a1[2] = 6;
        a1[3] = 61;
        return a1;

    }

    public int[] get_arr_3(){
        int[] a1;
        a1 = new int[5];
        a1[0] = 7;
        a1[1] = 8;
        a1[2] = 9;
        a1[3] = 91;
        a1[4] = 92;
        return a1;

    }

    public int[] get_arr_4(){
        int[] a1;
        a1 = new int[6];
        a1[0] = 10;
        a1[1] = 11;
        a1[2] = 12;
        a1[3] = 121;
        a1[4] = 122;
        return a1;

    }

    public boolean another_B_init()
    {
        
        a = this.get_arr_4();


        return true;
    }

    public boolean iter_B_a()
    {
        System.out.println(a[0]);
        System.out.println(a[1]);
        System.out.println(a[2]);
        return true;
    }

    public boolean set_e()
    {
        e = 2;
        return true;
    }

    public int get_e()
    {
        return e;
    }

}

class C extends B {
    int[] a;
    int[] b;
    int e;
    boolean temp;

    public boolean c_init()
    {
        a = (new C()).get_arr_1();
        b = this.get_arr_1();
        temp = this.another_A_init();
        temp = this.another_B_init();
        temp = this.iter_A_a();
        temp = this.iter_B_a();
        temp = this.iter_C_a();
        return false;
    }

    public boolean iter_C_a()
    {
        System.out.println(a[0]);
        System.out.println(a[1]);
        System.out.println(a[2]);
        return false;
    }

    public boolean set_e()
    {
        e = 3;
        return true;
    }

    public int get_e()
    {
        return e;
    }

    public int[] get_arr_1(){
        int[] a1;
        a1 = new int[3];
        a1[0] = 31;
        a1[1] = 32;
        a1[2] = 33;
        return a1;

    }

}















