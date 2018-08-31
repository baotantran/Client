import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        Client bao;
        bao = new Client("127.0.0.1");
        bao.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bao.startRunning();
    }
}
