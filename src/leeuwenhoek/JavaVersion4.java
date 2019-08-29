import java.lang.Integer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.java.api.Clojure;

final class JavaVersion4_other extends AFunction {
    // This version of method foo2 looks to me identical to the byte
    // code produced by the Clojure 1.10.1 compiler for function foo2.

    // However, it is ~600 msec slow for trial 1 with default Tiered
    // compilation enabled, rather than ~50 msec fast for trial 1 like
    // it is when running in Clojure.

    // Perhaps the difference is in how Clojure calls the method,
    // vs. how Java calls the method here?
    public static Object invokeStatic(final Object n) {
	int intn = RT.intCast(n);
	long i;
	i = (long) RT.intCast((long) 0);
	while (true) {
	    if (i < intn) {
		i = Numbers.inc(i);
	    } else {
		break;
	    }
	}
	return Numbers.num(i);
    }

    @Override
    public Object invoke(final Object n) {
        return invokeStatic(n);
    }
}

class JavaVersion4 {
    public static final Var afn_obj;
    static {
	afn_obj = RT.var("user", "foo2");
    }
    public static RuntimeMXBean mxb;
    public static long uptime() {
	return mxb.getUptime();
    }
    public static void do10times() {
	long n = 10;
	long i;
	long start, end;
	Object ret;
	double elapsed;

	for (i = 0; i < n; i++) {
	    System.out.print(uptime() + " : Trial " + (i+1));
	    start = System.nanoTime();
	    ret = ((IFn) afn_obj.getRawRoot()).invoke(Long.valueOf(100000000));
	    end = System.nanoTime();
	    elapsed = Numbers.divide(Numbers.minus(end, start), 1000000.0);
	    System.out.println(" Elapsed time: " + elapsed + " msecs");
	    System.out.println("ret=" + ret);
	}
    }
    public static void main(String[] args) {
	mxb = ManagementFactory.getRuntimeMXBean();

	// Initialize Var #'user/foo2
	IFn eval = RT.var("clojure.core", "eval");
	eval.invoke(Clojure.read("(def foo2 nil)"));
	Var foo2 = RT.var("user", "foo2");
	IFn alter_var_root = RT.var("clojure.core", "alter-var-root");
	IFn constantly = RT.var("clojure.core", "constantly");
	Object constantly_JavaVersion4_other = constantly.invoke(new JavaVersion4_other());
	alter_var_root.invoke(foo2, constantly_JavaVersion4_other);

	do10times();
    }
}
