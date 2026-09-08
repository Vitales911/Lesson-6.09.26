import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankLivelock {
    static class Account {
        String name;
        int balance;
        Lock lock = new ReentrantLock();
        
        Account(String name, int balance) {
            this.name = name;
            this.balance = balance;
        }
        
        boolean tryTransfer(Account to, int amount) {
            boolean myLock = lock.tryLock();
            boolean toLock = to.lock.tryLock();
            
            if (myLock && toLock) {
                try {
                    if (balance >= amount) {
                        balance -= amount;
                        to.balance += amount;
                        System.out.println(name + " -> " + to.name + 
                            ": " + amount + " (balance: " + balance + ")");
                        return true;
                    }
                } finally {
                    lock.unlock();
                    to.lock.unlock();
                }
            } else {
                if (myLock) lock.unlock();
                if (toLock) to.lock.unlock();
                System.out.println(name + " уступает " + to.name);
            }
            return false;
        }
    }
    
    public static void main(String[] args) {
        Account a = new Account("A", 1000);
        Account b = new Account("B", 1000);
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                a.tryTransfer(b, 10);
                try { Thread.sleep(10); } catch (Exception e) {}
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                b.tryTransfer(a, 10);
                try { Thread.sleep(10); } catch (Exception e) {}
            }
        });
        
        t1.start();
        t2.start();
    }
}