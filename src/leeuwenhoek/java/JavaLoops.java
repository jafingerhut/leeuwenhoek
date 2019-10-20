package leeuwenhoek.java;

public class JavaLoops {
    public static long LoopEmptyNest1 (long n) {
	long i;
	for (i = 0; i < n; i++) {
	}
	return i;
    }
    public static long LoopEmptyNest2 (long n) {
	long i, j;
	for (i = 0; i < n; i++) {
	    for (j = 0; j < n; j++) {
	    }
	}
	return i;
    }
    public static long LoopSumNest1 (long n) {
	long sum = 0;
	for (long i = 0; i < n; i++) {
	    sum += i;
	}
	return sum;
    }
    public static long LoopSumNest2 (long n) {
	long sum = 0;
	for (long i = 0; i < n; i++) {
	    sum += i;
	    for (long j = 0; j < n; j++) {
		sum += j;
	    }
	}
	return sum;
    }
}
