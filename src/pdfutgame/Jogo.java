package pdfutgame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Jogo {

    private static final int MAX_COL = 50;
    private static final int MAX_LIN = 20;
    private static final char SIM_LIVRE = '.';
    private static final char SIM_BOLA = '@';
    private static final int NUN_PLAYERS = 2;

    private static final int PORTA = 8000;

    private static char campo[][] = new char[MAX_LIN][MAX_COL];

    private static char players[] = new char[NUN_PLAYERS * 2];

    private static DatagramSocket socket = null;
    private static DatagramPacket packet = null;

    private static void inicializaJogo() {
        for (int i = 0; i < MAX_LIN; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                campo[i][j] = SIM_LIVRE;
            }
        }

        // simobols iniciais dos times
        char vetPos[] = {'A', 'a'};

        int lin, col;

        // sortear posicao inicial dos jogadores
        for (int p = 0; p < players.length / 2; p++) {
            for (int i = 0; i < NUN_PLAYERS; i++) {  // time 1
                players[i] = (char) (vetPos[p] + i);
                do {
                    lin = (int) (Math.random() * MAX_LIN);
                    col = (int) (Math.random() * MAX_COL);
                } while (campo[lin][col] != SIM_LIVRE);
                campo[lin][col] = players[i];
            }
        }

        // posicao inicial da bola
        do {
            lin = (int) (Math.random() * MAX_LIN);
            col = (int) (Math.random() * MAX_COL);
        } while (campo[lin][col] != SIM_LIVRE);
        campo[lin][col] = SIM_BOLA;

        try {
            socket = new DatagramSocket(PORTA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printSomething(String print, int howMuch) {
        for (int i = 0; i < howMuch; i++) {
            System.out.print(print.equals("") ? "\n" : print);
        }
    }

    private static void mostraCampo() {
        printSomething("", 25);

        System.out.println();
        printSomething("a", MAX_COL);

        for (int i = 0; i < MAX_LIN; i++) {
            System.out.println();
            for (int j = 0; j < MAX_COL; j++) {
                System.out.print(campo[i][j]);
            }
        }
        System.out.println("");
        printSomething("A", MAX_COL);
    }

    public static boolean movePlayer(char player, String movimento) {

        int l = getLinPlayer(player);
        int c = getColPlayer(player);
        System.out.println("\nmovimento: " + movimento);

        // movimento para cima
        if (movimento.equalsIgnoreCase("w") && l >= 0) {
            System.out.println("entrou: W");

            if (campo[l - 1][c] == SIM_LIVRE) {
                // swap, troca um livre pelo simbolo
                char aux = campo[l][c];
                campo[l][c] = SIM_LIVRE;
                campo[l - 1][c] = aux;
                return true;
            }
            // colocar a logica de movimentar a bola
            if (campo[l - 1][c] == SIM_BOLA) {
                campo[l - 1][c] = SIM_LIVRE;
                checkGoal(l - 2);
                if (l - 2 >= 0) {
                    campo[l - 2][c] = SIM_BOLA;
                }
            }

            return true;
        }
        if (movimento.equalsIgnoreCase("s") && l >= 0) {
            System.out.println("entrou: S");
            if (campo[l + 1][c] == SIM_LIVRE) {
                // swap, troca um livre pelo simbolo
                char aux = campo[l][c];
                System.out.println(aux);
                campo[l][c] = SIM_LIVRE;
                campo[l + 1][c] = aux;
                return true;
            }
            // colocar a logica de movimentar a bola
            if (campo[l + 1][c] == SIM_BOLA) {
                campo[l + 1][c] = SIM_LIVRE;
                checkGoal(l + 2);
                campo[l + 2][c] = SIM_BOLA;
            }

            return true;
        }
        if (movimento.equalsIgnoreCase("a") && c >= 0) {
            System.out.println("entrou: A");
            if (campo[l][c - 1 >= 0 ? c - 1 : MAX_COL - 1] == SIM_LIVRE) {
                // swap, troca um livre pelo simbolo
                char aux = campo[l][c];
                System.out.println(aux);
                campo[l][c] = SIM_LIVRE;
                campo[l][c - 1 < 0 ? MAX_COL - 1 : c - 1] = aux;
                return true;
            }
            // colocar a logica de movimentar a bola
            if (campo[l][c - 1] == SIM_BOLA) {
                campo[l][c - 1] = SIM_LIVRE;
                campo[l][c - 2 >= 0 ? c - 2 : MAX_COL - 2] = SIM_BOLA;
            }

            return true;
        }
        if (movimento.equalsIgnoreCase("d") && c >= 0) {
            System.out.println("entrou: D");
            if (campo[l][c + 1 <= MAX_COL - 1 ? c + 1 : 0] == SIM_LIVRE) {
                // swap, troca um livre pelo simbolo
                char aux = campo[l][c];
                System.out.println(aux);
                campo[l][c] = SIM_LIVRE;
                campo[l][c + 1 > MAX_COL - 1 ? 0 : c + 1] = aux;
                return true;
            }
            // colocar a logica de movimentar a bola
            if (campo[l][c + 1] == SIM_BOLA) {
                campo[l][c + 1] = SIM_LIVRE;
                campo[l][c + 2 < MAX_COL ? c + 2 : 1] = SIM_BOLA;
            }

            return true;
        }
        return true;
    }

    private static void checkGoal(int l) {
        if (l < 0) {
            System.out.println("PARABÉNS!");
            System.out.println("O time minúsculo venceu!");
            System.exit(0);
        } else if (l > MAX_LIN - 1) {
            System.out.println("PARABÉNS!");
            System.out.println("O time maiúsculo venceu!");
            System.exit(0);
        }
    }

    public static void aguardaPlayer() {
        try {

            byte[] buffer = new byte[50];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            InetAddress client = packet.getAddress();
            int clientPort = packet.getPort();

            String efetiva = new String(buffer, 0, packet.getLength());

            movePlayer(efetiva.charAt(0), "" + efetiva.charAt(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getColPlayer(char player) {
        int pos = -1;
        for (int i = 0; i < MAX_LIN; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                if (campo[i][j] == player) {
                    pos = j;
                    i = MAX_LIN;  // matar o loop externo rapidamente ou usar return
                    break;
                }
            }
        }
        return pos;
    }

    public static int getLinPlayer(char player) {
        int pos = -1;
        for (int i = 0; i < MAX_LIN; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                if (campo[i][j] == player) {
                    pos = i;
                    j = MAX_COL;  // matar o loop externo rapidamente ou usar return
                    break;
                }
            }
        }
        return pos;
    }

    public static void getColBola() {
        // retorna a posicao do jogador no tabuleiro
    }

    public static void getLinBola() {
        // retorna a posicao do jogador no tabuleiro
    }

    public static void main(String[] args) {
        inicializaJogo();
        while (true) {
            mostraCampo();
            aguardaPlayer();
        }
    }
}
