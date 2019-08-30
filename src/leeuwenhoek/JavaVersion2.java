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
    public static RuntimeMXBean mxb;
    public static long uptime() {
	return mxb.getUptime();
    }
    public static void do10times() {
	long n = 10;
	long i;
	long start, end;
	long start_uptime, end_uptime;
	Object ret;
	double elapsed;

	for (i = 0; i < n; i++) {
	    start_uptime = uptime();
	    start = System.nanoTime();
	    ret = JavaVersion2_other.foo2(Long.valueOf(100000000));
	    end = System.nanoTime();
	    end_uptime = uptime();
	    System.out.print(start_uptime + " - " + end_uptime +
			     " : Trial " + (i+1));
	    elapsed = Numbers.divide(Numbers.minus(end, start), 1000000.0);
	    System.out.println(" Elapsed time: " + elapsed + " msecs");
	    System.out.println("ret=" + ret);
	}
    }
    public static void main(String[] args) {
	mxb = ManagementFactory.getRuntimeMXBean();
	do10times();
    }
}
