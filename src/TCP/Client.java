package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket dataSocket;
    private DataInputStream in;
    private DataOutputStream out;

    private int myTurn;
    private static char map[][] = {{'v', 'v', 'v'}, {'v', 'v', 'v'}, {'v', 'v', 'v'}};

    Scanner sc;


    public Client() {
        sc = new Scanner(System.in);
    }

    public void connect(String hostname, int port) {
        try {
            dataSocket = new Socket(hostname, port);
            in = new DataInputStream(dataSocket.getInputStream());
            out = new DataOutputStream(dataSocket.getOutputStream());
            System.out.println("IL MIO SIMBOLO E' x");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void execute() {
        boolean vittoria = false;
        boolean sconfitta = false;
        boolean pareggio=false;
        int row,column;
        while (!vittoria && !sconfitta && !pareggio) {
            try {
                //GIOCA IL SERVER
                sconfitta=in.readBoolean();
                pareggio=in.readBoolean();
                if (!sconfitta && !pareggio) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            map[i][j] = in.readChar();
                        }
                    }
                    seeMap();

                    //GIOCA IL CLIENT
                    do {
                        do {
                            System.out.print("INSERISCI UNA RIGA ->");
                            row = sc.nextInt();
                            if (row < 1 || row > 3) System.out.println("INSERIRE SCELTA VALIDA");
                        } while (row < 1 || row > 3);
                        do {
                            System.out.print("INSERISCI UNA COLONNA ->");
                            column = sc.nextInt();
                            if (column < 1 || column > 3) System.out.println("INSERIRE SCELTA VALIDA");
                        } while (column < 1 || column > 3);
                        if (!sceltaValida(row, column)) System.out.println("POSTO GIA OCCUPATO");
                    } while (!sceltaValida(row, column));
                    map[row-1][column-1] = 'x';
                    vittoria=checkWin();
                    if (!vittoria) pareggio=checkPareggio();
                    out.writeBoolean(vittoria);
                    out.writeBoolean(pareggio);
                    if (!vittoria && !pareggio) {
                        System.out.println("TURNO AVVERSARIO");
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) out.writeChar(map[i][j]);
                        }
                    }
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (vittoria) System.out.println("HO VINTO");
        else if (sconfitta) System.out.println("HO PERSO");
        else System.out.println("PAREGGIO");
    }

    public void close() {
        try {
            dataSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.connect("127.0.0.1", 14302);
        c.execute();
        c.close();
    }


    public boolean checkPareggio()
    {
        int count=0;
        for (int i=0;i<3;i++)
        {
            for (int j=0;j<3;j++)
            {
                if (map[i][j]!='v') count++;
            }
        }
        if (count==9) return true;
        else return false;
    }

    public boolean checkWin()
    {
        boolean win=false;
        for (int i=0;i<3;i++)
        {
            if (map[i][0]==map[i][1] && map[i][1]==map[i][2] && map[i][2]=='x') win=true;
        }
        for (int i=0;i<3;i++)
        {
            if (map[0][i]==map[1][i] && map[1][i]==map[2][i] && map[2][i]=='x') win=true;
        }
        if (map[0][0]==map[1][1] && map[1][1]==map[2][2] && map[2][2]=='x') win=true;
        if (map[0][2]==map[1][1] && map[1][1]==map[2][0] && map[2][0]=='x') win=true;
        return win;
    }
    private void seeMap()
    {
        for (int i=0;i<3;i++)
        {
            for (int j=0;j<3;j++)
            {
                System.out.print(map[i][j]+" ");
            }
            System.out.println();
        }
    }

    private boolean sceltaValida(int row,int col)
    {
        row--;
        col--;
        if (map[row][col]=='v') return true;
        else return false;
    }
}
