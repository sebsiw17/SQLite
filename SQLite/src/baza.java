import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class baza {

    Connection connection = null;

    //Połączenie z bazą danych
    baza(){
        try {
            // Załadowanie sterownika JDBC dla MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try{
            // Logowanie do bazy danych
            String url = "jdbc:mysql://localhost:3306/amidy";
            String username = "root";
            String password = "Student123!";

            // Nawiązanie połączenia z bazą danych
            connection = DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Operacje na bazie danych zależne od użytkownika
    void wybieranie(int numer) throws SQLException {

        switch (numer){
            case 1:
                // Scanner do pobierania danych
                Scanner scanner= new Scanner(System.in);

                String query="CREATE TABLE ";

                int wiersze;
                String nazwa_tabeli,nazwa_zmiennej;

                // Pobranie nazwy tabeli
                System.out.println("podaj nazwe tabeli");
                nazwa_tabeli=scanner.nextLine().toString();

                query+=nazwa_tabeli+" ( ";

                // Pobranie liczby kolumn
                System.out.println("podaj ile chcesz miec wierszy w tabeli");
                wiersze=scanner.nextInt();

                for (int i=0;i<wiersze;i++){

                    // Pobranie nazwy kolumny
                    System.out.println("podaj nazwe zmiennej");
                    nazwa_zmiennej= scanner.next();

                    query+=nazwa_zmiennej+" ";

                    // Pobranie typu danych dla kolumny
                    System.out.println("Wybierz typ danej");
                    System.out.println("1:INT");
                    System.out.println("2:VARCHAR");
                    System.out.println("3:DATE");

                    switch (scanner.nextInt()){
                        case 1:
                            query+="INT";
                            // Sprawdzenie, czy INT ma być autoinkrementowane
                            System.out.println("czy ma byc AI?\n 1:tak \n 2:nie");
                            int auto=scanner.nextInt();
                            if (auto==1){
                                query+=" NOT NULL AUTO_INCREMENT PRIMARY KEY";
                            }
                            break;
                        case 2:
                            // Pobranie maksymalnej długości dla VARCHAR
                            System.out.println("podaj maksymalna ilosc znaków");
                            query+="VARCHAR("+scanner.nextInt()+")";
                            break;
                        case 3:
                            query+="DATE";
                    }
                    if (i==wiersze-1){
                        query+=");";
                    } else {
                        query+=" , ";
                    }
                }

                String url = "jdbc:mysql://localhost:3306/amidy";
                String username = "root";
                String password = "Student123!";
                try (Connection connection = DriverManager.getConnection(url, username, password);
                     Statement stm = connection.createStatement();
                ){
                    // Wykonanie zapytania SQL tworzącego tabelę
                    stm.executeUpdate(query);
                    System.out.println("Polecenie ukończone powodzeniem");
                } catch (SQLException e) {
                    System.out.println("Błąd SQL");
                    throw new RuntimeException(e);
                }

                break;

            case 2:
                Scanner scanner2= new Scanner(System.in);
                String query_koncowe,nazwa_tablicy;
                String m="wybierz tablice: \n";
                String field="";
                int sprawdzanie=0;

                Statement statement_2 = connection.createStatement();

                // Pobranie listy tabel z bazy danych
                ResultSet resultSet= statement_2.executeQuery("SHOW TABLES;");

                while (resultSet.next()){
                    m+= resultSet.getString("Tables_in_amidy")+"\n";
                }

                System.out.println(m);

                nazwa_tablicy=scanner2.next();
                // Pobranie struktury wybranej tabeli
                ResultSet resultSet_2= statement_2.executeQuery("DESCRIBE "+nazwa_tablicy+" ;");
                query_koncowe="INSERT INTO "+nazwa_tablicy+" VALUES (";

                while (resultSet_2.next()){
                    field=resultSet_2.getString("Field");
                    System.out.println("podaj wartosc dla "+field);
                    String zmienna= scanner2.next();
                    if (sprawdzanie==0){
                        query_koncowe+=" \" "+zmienna+" \" ";
                    } else {
                        query_koncowe += " , \" " + zmienna + " \" ";
                    }
                    sprawdzanie++;
                }
                query_koncowe+=" ) ;";

                // Wykonanie zapytania SQL wstawiającego wartości do tabeli
                statement_2.execute(query_koncowe);

                break;

            case 3:
                String wybierz_tablice="wybierz tabele: \n";
                Scanner scanner3= new Scanner(System.in);
                String tablica_nazwa,koncowa_dana="",probna="";
                System.out.println("tablice");
                Statement statement_3 = connection.createStatement();

                // Pobranie listy tabel z bazy danych
                ResultSet resultSet_3= statement_3.executeQuery("SHOW TABLES;");

                while (resultSet_3.next()){
                    wybierz_tablice+= resultSet_3.getString("Tables_in_amidy")+"\n";
                }
                System.out.println(wybierz_tablice);
                tablica_nazwa=scanner3.next();

                // Pobranie wszystkich danych z wybranej tabeli
                ResultSet resultSet_4=statement_3.executeQuery("SELECT * FROM "+tablica_nazwa+" ;");
                ResultSetMetaData metaData= (ResultSetMetaData) resultSet_4.getMetaData();
                int columny=metaData.getColumnCount();

                String zadanie[]= new String[columny];

                // Pobranie nazw kolumn i utworzenie nagłówka
                for (int i=1;i<=columny;i++){
                    zadanie[i-1]=metaData.getColumnName(i);
                    koncowa_dana+=zadanie[i-1]+" \t";
                }

                koncowa_dana+="\n";

                // Pobranie danych dla każdego wiersza i dodanie ich do wyniku
                while (resultSet_4.next()){
                    for (int i=1;i<=columny;i++){
                        koncowa_dana+=resultSet_4.getString(zadanie[i-1])+"\t";
                    }
                    koncowa_dana+="\n";
                }
                System.out.println(probna);
                System.out.println(koncowa_dana);
                break;

            case 4:
                // Zakończenie programu
                System.exit(0);
                break;
            default:
                System.out.println("Podano niewłaściwa dane :[");
                break;
        }

    }

}
