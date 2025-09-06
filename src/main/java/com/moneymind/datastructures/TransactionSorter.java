package main.java.com.moneymind.datastructures;

import main.java.com.moneymind.model.Transaction;
import java.util.*;

/**
 * Advanced sorting algorithms for Transaction data
 * Implements merge sort and other efficient sorting techniques
 */
public class TransactionSorter {

    public enum SortBy {
        DATE, AMOUNT, DESCRIPTION, CATEGORY, TYPE, CREATED_AT
    }

    public enum SortOrder {
        ASCENDING, DESCENDING
    }

    // Main sorting method
    public static void sort(List<Transaction> transactions, SortBy sortBy, SortOrder order) {
        Comparator<Transaction> comparator = getComparator(sortBy);
        if (order == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        // Use merge sort for stable sorting
        mergeSort(transactions, comparator);
    }

    // Multi-criteria sorting
    public static void sort(List<Transaction> transactions,
                            List<SortCriteria> criteriaList) {
        Comparator<Transaction> comparator = null;

        for (SortCriteria criteria : criteriaList) {
            Comparator<Transaction> currentComparator = getComparator(criteria.getSortBy());
            if (criteria.getOrder() == SortOrder.DESCENDING) {
                currentComparator = currentComparator.reversed();
            }

            if (comparator == null) {
                comparator = currentComparator;
            } else {
                comparator = comparator.thenComparing(currentComparator);
            }
        }

        if (comparator != null) {
            mergeSort(transactions, comparator);
        }
    }

    // Quick sort implementation for performance comparison
    public static void quickSort(List<Transaction> transactions, SortBy sortBy, SortOrder order) {
        Comparator<Transaction> comparator = getComparator(sortBy);
        if (order == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }
        quickSortHelper(transactions, 0, transactions.size() - 1, comparator);
    }

    // Merge sort implementation (stable sort)
    private static void mergeSort(List<Transaction> transactions, Comparator<Transaction> comparator) {
        if (transactions.size() <= 1) return;

        List<Transaction> temp = new ArrayList<>(transactions);
        mergeSortHelper(transactions, temp, 0, transactions.size() - 1, comparator);
    }

    private static void mergeSortHelper(List<Transaction> transactions, List<Transaction> temp,
                                        int left, int right, Comparator<Transaction> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSortHelper(transactions, temp, left, mid, comparator);
            mergeSortHelper(transactions, temp, mid + 1, right, comparator);
            merge(transactions, temp, left, mid, right, comparator);
        }
    }

    private static void merge(List<Transaction> transactions, List<Transaction> temp,
                              int left, int mid, int right, Comparator<Transaction> comparator) {
        // Copy data to temp array
        for (int i = left; i <= right; i++) {
            temp.set(i, transactions.get(i));
        }

        int i = left, j = mid + 1, k = left;

        // Merge the temp arrays back
        while (i <= mid && j <= right) {
            if (comparator.compare(temp.get(i), temp.get(j)) <= 0) {
                transactions.set(k++, temp.get(i++));
            } else {
                transactions.set(k++, temp.get(j++));
            }
        }

        // Copy remaining elements
        while (i <= mid) {
            transactions.set(k++, temp.get(i++));
        }
        while (j <= right) {
            transactions.set(k++, temp.get(j++));
        }
    }

    // Quick sort helper methods
    private static void quickSortHelper(List<Transaction> transactions, int low, int high,
                                        Comparator<Transaction> comparator) {
        if (low < high) {
            int pi = partition(transactions, low, high, comparator);
            quickSortHelper(transactions, low, pi - 1, comparator);
            quickSortHelper(transactions, pi + 1, high, comparator);
        }
    }

    private static int partition(List<Transaction> transactions, int low, int high,
                                 Comparator<Transaction> comparator) {
        Transaction pivot = transactions.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(transactions.get(j), pivot) <= 0) {
                i++;
                Collections.swap(transactions, i, j);
            }
        }

        Collections.swap(transactions, i + 1, high);
        return i + 1;
    }

    // Binary search for sorted lists
    public static int binarySearch(List<Transaction> sortedTransactions,
                                   Transaction target, SortBy sortBy) {
        Comparator<Transaction> comparator = getComparator(sortBy);
        return Collections.binarySearch(sortedTransactions, target, comparator);
    }

    // Get appropriate comparator based on sort criteria
    private static Comparator<Transaction> getComparator(SortBy sortBy) {
        switch (sortBy) {
            case DATE:
                return Comparator.comparing(Transaction::getTransactionDate);
            case AMOUNT:
                return Comparator.comparing(Transaction::getAmount);
            case DESCRIPTION:
                return Comparator.comparing(Transaction::getDescription,
                        String.CASE_INSENSITIVE_ORDER);
            case CATEGORY:
                return Comparator.comparing(Transaction::getCategoryName,
                        String.CASE_INSENSITIVE_ORDER);
            case TYPE:
                return Comparator.comparing(Transaction::getType);
            case CREATED_AT:
                return Comparator.comparing(Transaction::getCreatedAt);
            default:
                return Comparator.comparing(Transaction::getTransactionDate);
        }
    }

    // Utility methods for common sorting patterns
    public static List<Transaction> sortByDateDescending(List<Transaction> transactions) {
        List<Transaction> sorted = new ArrayList<>(transactions);
        sort(sorted, SortBy.DATE, SortOrder.DESCENDING);
        return sorted;
    }

    public static List<Transaction> sortByAmountDescending(List<Transaction> transactions) {
        List<Transaction> sorted = new ArrayList<>(transactions);
        sort(sorted, SortBy.AMOUNT, SortOrder.DESCENDING);
        return sorted;
    }

    public static List<Transaction> sortByCategory(List<Transaction> transactions) {
        List<Transaction> sorted = new ArrayList<>(transactions);
        sort(sorted, SortBy.CATEGORY, SortOrder.ASCENDING);
        return sorted;
    }

    // Performance measurement
    public static long measureSortTime(List<Transaction> transactions,
                                       SortBy sortBy, SortOrder order) {
        List<Transaction> copy = new ArrayList<>(transactions);
        long startTime = System.nanoTime();
        sort(copy, sortBy, order);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    // Helper class for multi-criteria sorting
    public static class SortCriteria {
        private SortBy sortBy;
        private SortOrder order;

        public SortCriteria(SortBy sortBy, SortOrder order) {
            this.sortBy = sortBy;
            this.order = order;
        }

        public SortBy getSortBy() { return sortBy; }
        public SortOrder getOrder() { return order; }

        // Common sorting combinations
        public static List<SortCriteria> dateAmountDesc() {
            return Arrays.asList(
                    new SortCriteria(SortBy.DATE, SortOrder.DESCENDING),
                    new SortCriteria(SortBy.AMOUNT, SortOrder.DESCENDING)
            );
        }

        public static List<SortCriteria> categoryDateDesc() {
            return Arrays.asList(
                    new SortCriteria(SortBy.CATEGORY, SortOrder.ASCENDING),
                    new SortCriteria(SortBy.DATE, SortOrder.DESCENDING)
            );
        }

        public static List<SortCriteria> typeAmountDesc() {
            return Arrays.asList(
                    new SortCriteria(SortBy.TYPE, SortOrder.ASCENDING),
                    new SortCriteria(SortBy.AMOUNT, SortOrder.DESCENDING)
            );
        }
    }
}
