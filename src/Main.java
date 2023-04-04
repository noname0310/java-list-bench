import list.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.Callable;

@SuppressWarnings({"unused", "RedundantIfStatement"})
final class ListTestRunner<T extends List<?>> {
    private final Class<T> listClass;
    private String testSubtitle;
    private boolean isBackwardIterationSupported;
    private Callable<T> customConstructor;

    public ListTestRunner(Class<T> listClass) {
        this.listClass = listClass;
        testSubtitle = null;
        isBackwardIterationSupported = true;
        customConstructor = null;
    }

    public ListTestRunner<T> subtitle(String value) {
        this.testSubtitle = value;
        return this;
    }

    public ListTestRunner<T> backwardIterationSupported(boolean value) {
        this.isBackwardIterationSupported = value;
        return this;
    }

    public ListTestRunner<T> withCustomConstructor(Callable<T> callable) {
        this.customConstructor = callable;
        return this;
    }

    public void runTests() {
        System.out.println("Running tests for " + listClass.getSimpleName() + (testSubtitle == null ? "" : " (" + testSubtitle + ")"));
        long startTime = System.currentTimeMillis();
        int failedTests = 0;
        Method[] methods = ListTestRunner.class.getMethods();
        methods = Arrays.stream(methods).sorted(Comparator.comparing(Method::getName)).toArray(Method[]::new);
        for (var method : methods) {
            if (method.getReturnType() == boolean.class && method.getName().endsWith("Test")) {
                boolean testResult = runSingleTest(method.getName(), () -> (boolean) method.invoke(this));
                if (!testResult) failedTests += 1;
            }
        }
        long endTime = System.currentTimeMillis();
        if (failedTests == 0) {
            System.out.println("Tests passed in " + (endTime - startTime) + "ms");
        } else {
            System.out.println(failedTests + " tests failed in " + (endTime - startTime) + "ms");
        }
        System.out.println();
    }

    private boolean runSingleTest(String testName, Callable<Boolean> callable) {
        System.out.print(testName + ": ");
        long startTime = System.currentTimeMillis();
        boolean passed;
        try {
            passed = callable.call();
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            System.out.println();
            System.out.println("Exception: " + targetException.getClass().getSimpleName() + ": " + targetException.getMessage());
            StackTraceElement[] stackTrace = targetException.getStackTrace();
            for (var stackTraceElement : stackTrace) {
                System.out.println("\tat " + stackTraceElement);
            }
            passed = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();
        System.out.println((passed ? "passed" : "failed") + " in " + (endTime - startTime) + "ms");
        return passed;
    }

    @SuppressWarnings("unchecked")
    private <E> List<E> makeList() {
        if (this.customConstructor != null) {
            try {
                return (List<E>) this.customConstructor.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return (List<E>) this.listClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e
        ) {
            throw new RuntimeException(e);
        }
    }

    public boolean clearTest() {
        List<Integer> list = makeList();

        list.clear();
        if (list.length() != 0) return false;

        list.append(1);
        list.clear();
        if (list.length() != 0) return false;

        list.append(1);
        list.append(2);
        list.clear();
        if (list.length() != 0) return false;
        return true;
    }

    public boolean insertTest() {
        List<Integer> list = makeList();

        list.insert(0, 1);
        if (list.length() != 1) return false;
        if (list.getValue(0) != 1) return false;

        list.insert(0, 2);
        if (list.length() != 2) return false;
        if (list.getValue(0) != 2) return false;
        if (list.getValue(1) != 1) return false;

        list.insert(1, 3);
        if (list.length() != 3) return false;
        if (list.getValue(0) != 2) return false;
        if (list.getValue(1) != 3) return false;
        if (list.getValue(2) != 1) return false;

        try {
            list.insert(-1, 4);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.insert(4, 5);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        return true;
    }

    public boolean appendTest() {
        List<Integer> list = makeList();

        list.append(1);
        if (list.length() != 1) return false;
        if (list.getValue(0) != 1) return false;

        list.append(2);
        if (list.length() != 2) return false;
        if (list.getValue(0) != 1) return false;
        if (list.getValue(1) != 2) return false;

        return true;
    }

    public boolean updateTest() {
        List<Integer> list = makeList();

        list.append(1);
        list.update(0, 2);
        if (list.length() != 1) return false;
        if (list.getValue(0) != 2) return false;

        list.append(3);
        list.update(1, 4);
        if (list.length() != 2) return false;
        if (list.getValue(0) != 2) return false;

        try {
            list.update(-1, 5);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.update(2, 5);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        return true;
    }

    public boolean getValueTest() {
        List<Integer> list = makeList();

        list.append(1);
        if (list.getValue(0) != 1) return false;

        list.append(2);
        if (list.getValue(0) != 1) return false;
        if (list.getValue(1) != 2) return false;

        try {
            list.getValue(-1);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.getValue(2);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        
        return true;
    }

    public boolean removeTest() {
        List<Integer> list = makeList();

        list.append(1);
        list.append(2);
        list.append(3);

        int removed = list.remove(1);
        if (removed != 2) return false;
        if (list.length() != 2) return false;
        if (list.getValue(0) != 1) return false;
        if (list.getValue(1) != 3) return false;

        list.remove(0);
        if (list.length() != 1) return false;
        if (list.getValue(0) != 3) return false;

        list.remove(0);
        if (list.length() != 0) return false;

        try {
            list.remove(-1);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(0);
            return false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        return true;
    }

    public boolean listIteratorTest() {
        List<Integer> list = makeList();

        ListIterator<Integer> iterator = list.listIterator();
        if (iterator.hasNext()) return false;
        if (this.isBackwardIterationSupported) {
            if (iterator.hasPrevious()) return false;
        }

        list.append(1);
        list.append(2);
        list.append(3);

        iterator = list.listIterator();
        if (!iterator.hasNext()) return false;
        if (iterator.next() != 1) return false;
        if (!iterator.hasNext()) return false;
        if (iterator.next() != 2) return false;
        if (!iterator.hasNext()) return false;
        if (iterator.next() != 3) return false;
        if (iterator.hasNext()) return false;

        if (this.isBackwardIterationSupported) {
            if (!iterator.hasPrevious()) return false;
            if (iterator.previous() != 2) return false;
            if (!iterator.hasPrevious()) return false;
            if (iterator.previous() != 1) return false;
            if (iterator.hasPrevious()) return false;
        }

        return true;
    }
}

class ListBenchmarkRunner<T extends List<?>> {
    private static final int TEST_SIZE = 100000;
    private static final int TEST_ITERATIONS = 10;

    private final Callable<T> listConstructor;
    private String testSubtitle;

    public ListBenchmarkRunner(Callable<T> listConstructor) {
        this.listConstructor = listConstructor;
    }

    public ListBenchmarkRunner<T> subtitle(String value) {
        this.testSubtitle = value;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        List<Integer> list;
        try {
            list = (List<Integer>) listConstructor.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Benchmarking " + list.getClass().getSimpleName() + (testSubtitle == null ? "" : " (" + testSubtitle + ")"));

        long meanTime = 0;
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            meanTime += test(list);
        }
        meanTime /= 10;

        System.out.println("Mean time: " + meanTime + " ms (" + TEST_SIZE + " iterations)");
    }

    public long test(List<Integer> list) {
        list.clear();

        long start = System.currentTimeMillis();

        Random random = new Random(0);

        for (int i = 0; i < TEST_SIZE; i++) {
            for (int j = 0; j < 10000; j++) {
                list.append(random.nextInt());
            }

            for (int j = 0; j < 10000; j++) {
                list.remove(0);
            }
        }

        long end = System.currentTimeMillis();

        return end - start;
    }
}

public class Main {
    public static void main(String[] args) {
        new ListTestRunner<>(ArrayList.class).runTests();

        new ListTestRunner<>(LinkedList.class)
                .backwardIterationSupported(false)
                .runTests();

        new ListTestRunner<>(LinkedList.class)
                .subtitle("use freelist")
                .backwardIterationSupported(false)
                .withCustomConstructor(() -> new LinkedList<>(new LinkedList.FreeListNodePool<>()))
                .runTests();

        new ListTestRunner<>(DoublyLinkedList.class).runTests();

        new ListBenchmarkRunner<>(() -> new LinkedList<>(new LinkedList.CtorNodePool<>())).run();

        new ListBenchmarkRunner<>(() -> new LinkedList<>(new LinkedList.FreeListNodePool<>()))
                .subtitle("use freelist")
                .run();
    }
}
