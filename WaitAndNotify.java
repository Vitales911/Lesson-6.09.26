public class SequenceOutput {
    private static final Object lock = new Object();
    private static boolean isFirstThreadTurn = true;

    public static void main(String[] args) {
        Thread thread1 = new Thread(new PrinterTask("1", true));
        Thread thread2 = new Thread(new PrinterTask("2", false));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.err.println("The main flow was interrupted!");
            Thread.currentThread().interrupt();
        }
    }

    private static class PrinterTask implements Runnable {
        private final String textToPrint;
        private final boolean runOnTrue;

        public PrinterTask(String textToPrint, boolean runOnTrue) {
            this.textToPrint = textToPrint;
            this.runOnTrue = runOnTrue;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (lock) {
                    while (isFirstThreadTurn != runOnTrue) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    System.out.println(textToPrint);

                    isFirstThreadTurn = !runOnTrue;
                    lock.notifyAll();
                }
            }
        }
    }
}
