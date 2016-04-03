/**
 * Adapted from classes and methods seen in Algorithms, 4th Edition, by Robert Sedgewick and Kevin Wayne
 */
import java.util.PriorityQueue;
import java.util.Comparator;

public class MaxSizeHeap {
    Itemset[] pq;
    int N;
    int maxSize;

    MaxSizeHeap(int i) {
        pq = new Itemset[i+2];
        N = 0;
        maxSize = i+1;
    }

    public void sort () {
        for (int k = N/2; k >= 1; k--)
            sink(k);
        while (N > 1) {
            exch(1, N);
            N--;
            sink(1);

        }
    }

    private void swim (int k) {
        while (k > 1 && !less(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    private void sink (int k) {
        while (2*k <= N) {
            int j = 2 * k;
            if (j < N && !less(j, j + 1)) j++;
            if (less(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    public void insert(Itemset x) {
        pq[++N] = x;
        swim(N);
        //automatically balances
        if (N >= maxSize) {
            delMin();
        }
    }

    public void insertSet(Itemset x) throws java.io.IOException {
        String val = x.getValue();
        for (int i = 0; i < pq.length; i++) {
            if (pq[i] != null) {
                if (pq[i].getValue().equals(val)) {
                    return;
                }
            }
        }
        insert(x);
    }

    private boolean less (int i, int j) {
        double quantity = pq[i].getSupport() - pq[j].getSupport();
        if (quantity < 0) {
            return true;
        }
        return false;
    }

    public Itemset getMin() {
        return pq[1];
    }

    public Itemset delMin() {
        Itemset min = pq[1];
        exch(1, N--);
        sink(1);
        pq[N + 1] = null;
        return min;
    }

    public int getSize() {
        return N;
    }

    private void exch (int i, int j) {
        Itemset t = pq[i]; pq[i] = pq[j]; pq[j] = t;
    }
}
