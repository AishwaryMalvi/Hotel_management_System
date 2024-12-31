import java.sql.*;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/hoteldb";
    private static final String username = "root";
    private static final String password = "Malvi@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("\n WElCOME");
                System.out.println("Hotel Reservation System");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve the Room");
                System.out.println("2. view Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Room Number");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose the Option");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        reserveRoom(connection, sc);
                        break;
                    case 2:
                        viewReservatin(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateRoomNumber(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter Guest Name");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter Room Number");
            int getNumber = sc.nextInt();
            System.out.println("Enter Contact Number");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservation (guest_name, room_number, contact_number) " + "VALUES ('" + guestName + "', " + getNumber + ", '" + contactNumber + "')";
            try (Statement statement = connection.createStatement()) {
                int afftectedRoom = statement.executeUpdate(sql);

                if (afftectedRoom > 0) {
                    System.out.println("Reservation done successfully");
                } else {
                    System.out.println("Reservation not done");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservatin(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservation";

        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (result.next()) {
                int reservation_id = result.getInt("reservation_id");
                String guest_name = result.getString("guest_name");
                int room_number = result.getInt("room_number");
                String contact_number = result.getString("contact_number");
                String reservation_date = result.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n", reservation_id, guest_name, room_number, contact_number, reservation_date);
            }
            System.out.println("+---------------------------------------------------------------------------------------------------+");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void getRoomNumber(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter Reservation ID");
            int reservation_id = sc.nextInt();
            System.out.println("Enter Guest Name");
            String guest_name = sc.next();

            String sql = "SELECT room_number FROM reservation WHERE reservation_id = " + reservation_id + " AND guest_name =  '" + guest_name + "';";
            try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {

                if (result.next()) {
                    int room_number = result.getInt("room_number");
                    System.out.println();
                    System.out.println("Room Number for reservation ID " + reservation_id + " AND Guest Name " + guest_name + " is : " + room_number);
                } else {
                    System.out.println("Reservation Not found for the given ID and guest Name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void updateRoomNumber(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter Reservation ID to Update: ");
            int reservation_id = sc.nextInt();
            sc.nextLine();

            if (!reservationExits(connection, reservation_id)) {
                System.out.println("Reservation Not found");
                return;
            }

            System.out.println("Enter New Guest Name : ");
            String guest_name = sc.nextLine();
            System.out.println("Enter New Number : ");
            int room_number = sc.nextInt();
            System.out.println("Enter New Contact Number : ");
            String contact_number = sc.next();

            String sql = "UPDATE reservation SET guest_name= '" + guest_name + "', room_number= " + room_number + ", contact_number= '" + contact_number + "' WHERE reservation_id= " + reservation_id;

            try (Statement statement = connection.createStatement()) {
                int affectedrows = statement.executeUpdate(sql);

                if (affectedrows > 0) {
                    System.out.println("Reservation Update successfully");
                } else {
                    System.out.println("Reservation Not Updated");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc) {
        System.out.println("Enter Reservation ID to Delete: ");
        int reservation_id = sc.nextInt();
        sc.nextLine();

        if (!reservationExits(connection, reservation_id)) {
            System.out.println("Reservation Not found");
            return;
        }

        String sql = "DELETE FROM reservation where reservation_id = " + reservation_id;
        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows > 0) {
                System.out.println("Reservation Deleted Successfully");
            } else {
                System.out.println("Reservation Not Deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExits(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId;
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static void exit() throws InterruptedException {
        System.out.println("Existing System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("Thank you For using Hotel Management System");
    }


}