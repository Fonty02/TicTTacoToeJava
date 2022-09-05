package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;


public class ServerThread extends Thread {
    private Socket dataSocket;
    private DataInputStream in;
    private DataOutputStream out;

    private int randomNum;
    Scanner sc;

    private static char map[][]={{'v','v','v'},{'v','v','v'},{'v','v','v'}};


    public ServerThread(Socket data) {
        this.dataSocket = data;
        try {
            in = new DataInputStream(dataSocket.getInputStream());
            out = new DataOutputStream(dataSocket.getOutputStream());
            System.out.println("SERVER: AVVERSARIO TROVATO");
            sc=new Scanner(System.in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        int row,column;
        boolean vittoria=false;
        boolean sconfitta=false;
        boolean pareggio=false;
        while(!vittoria && !sconfitta && !pareggio) {
            try {
                System.out.println("SERVER: IL MIO SIMBOLO E' o");
                System.out.println("SERVER: MIO TURNO");
                //GIOCA IL SERVER
                seeMap();
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
                map[row-1][column-1] = 'o';
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
                // GIOCA IL CLIENT
                if (!vittoria && !pareggio) {
                    sconfitta = in.readBoolean();
                    pareggio = in.readBoolean();
                    System.out.println("AO");
                    if (!sconfitta && !pareggio) {
                        System.out.println("LETTO");
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                map[i][j] = in.readChar();
                            }
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
            if (map[i][0]==map[i][1] && map[i][1]==map[i][2] && map[i][2]=='o') win=true;
        }
        for (int i=0;i<3;i++)
        {
            if (map[0][i]==map[1][i] && map[1][i]==map[2][i] && map[2][i]=='o') win=true;
        }
        if (map[0][0]==map[1][1] && map[1][1]==map[2][2] && map[2][2]=='o') win=true;
        if (map[0][2]==map[1][1] && map[1][1]==map[2][0] && map[2][0]=='o') win=true;
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

