import java.lang.Integer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import clojure.lang.Numbers;

class JavaVersion2_other {
    public static Number foo2_inner(int n) {
	long i;
	i = 0;
	while (true) {
	    if (i < n) {
		i = Numbers.inc(i);
	    } else {
		break;
	    }
	}
	return Numbers.num(i);
    }
    public static Number foo2(Object n) {
	int intn = (int) ((long) n);
	return foo2_inner(intn);
    }
}

class JavaVersion2 {
    static RuntimeMXBean mxb;
    public static long uptime() {
	return mxb.getUptime();
    }
    public static void main(String[] args) {
	int i;
	long start, end;
	Number ret;
	double elapsed;

	mxb = ManagementFactory.getRuntimeMXBean();

	for (i = 0; i < 10; i++) {
	    start = System.nanoTime();
	    ret = JavaVersion2_other.foo2(Long.valueOf(100000000));
	    end = System.nanoTime();
	    elapsed = ((double) (end - start)) / 1000000.0;
	    System.out.println("ret=" + ret);
	    System.out.println(uptime() + " : Trial " + (i+1) +
			       " Elapsed time: " + elapsed + " msecs");
	}
    }
}
