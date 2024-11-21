/*
*  2024년 클라우드 컴퓨팅 Term Project
*  작성자: 2019038074 이우창
*  작성일: 2024-11-21 ~
*/

package aws;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;

        while (true) {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones        ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("                                 99. quit                   ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");

            if (menu.hasNextInt()) {
                number = menu.nextInt();
            } else {
                System.out.println("You must enter an integer!");
                break;
            }

            String instance_id = "";

            switch (number) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 99:
                    System.out.println("bye!");
                    menu.close();
                    id_string.close();
                    return;
                default:
                    System.out.println("No such menu!");
            }
        }
    }
}
