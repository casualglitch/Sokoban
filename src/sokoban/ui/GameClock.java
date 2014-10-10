import javax.swing.JLabel;
 
public class GameClock implements Runnable {
 
    private JLabel time;
 
    public GameClock(JLabel time) {
        this.time = time;
    }
 
    public void run() {
        while (true) {
            try {
                time.setText(zero(0) + ":" + zero(0) + ":" + zero(0));
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
 
    //Sets the zeroes needed within our hh/mm/ss clock.
    public String zero(int num) {
        String number = (num < 10) ? ("0" + num) : ("" + num);
        return number;
    }
}