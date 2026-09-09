public class BankDeadlock {
    static class Account {
        private int balance;
        private final int id;

        public Account(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }

        public void withdraw(int amount) { balance -= amount; }
        public void deposit(int amount) { balance += amount; }
    }

    public static void transfer(Account from, Account to, int amount) {
        synchronized (from) {
            System.out.println(Thread.currentThread().getName() + 
                " locked account " + from.id);
            
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            synchronized (to) {
                System.out.println(Thread.currentThread().getName() + 
                    " locked account " + to.id);
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    public static void main(String[] args) {
        Account acc1 = new Account(1, 1000);
        Account acc2 = new Account(2, 1000);

        Thread t1 = new Thread(() -> transfer(acc1, acc2, 100), "T1");
        
        Thread t2 = new Thread(() -> transfer(acc2, acc1, 200), "T2");

        t1.start();
        t2.start();
    }
}