

import model.*;
import service.ClinicService;
import util.InputValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ClinicService service = new ClinicService();

    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    public static void main(String[] args) {


        service.registerDoctor(new Doctor("D1", "Dr. Alice", "General"));
        service.registerDoctor(new Doctor("D2", "Dr. Bob", "Cardiology"));

        boolean running = true;

        System.out.println("Welcome to the Healthcare Management System.");

        while (running) {
            System.out.println("\n=== HEALTHCARE SYSTEM ===");
            System.out.println("1. Register Patient");
            System.out.println("2. Register Doctor");
            System.out.println("3. View Doctors");
            System.out.println("4. Book Appointment");
            System.out.println("5. View Appointments");
            System.out.println("6. Add Medical Record");
            System.out.println("7. View Medical Records");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        registerPatient();
                        break;
                    case "2":
                        registerDoctor();
                        break;
                    case "3":
                        viewDoctors();
                        break;
                    case "4":
                        bookAppointment();
                        break;
                    case "5":
                        viewAppointments();
                        break;
                    case "6":
                        addMedicalRecord();
                        break;
                    case "7":
                        viewRecords();
                        break;
                    case "8":
                        running = false;
                        System.out.println("System closed.");
                        break;
                    default:
                        System.out.println("Invalid option. Choose a number from 1 to 8.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    private static void registerPatient() {
        String id = readRequiredId("Enter Patient ID: ");
        String name = readRequiredName("Enter Patient Name: ");
        service.registerPatient(new Patient(id, name));
        System.out.println("Patient registered successfully.");
    }

    private static void registerDoctor() {
        String id = readRequiredId("Enter Doctor ID: ");
        String name = readRequiredName("Enter Doctor Name: ");
        String specialization = readRequiredSpecialization("Enter Specialization: ");

        service.registerDoctor(new Doctor(id, name, specialization));
        System.out.println("Doctor registered successfully.");
    }

    private static void viewDoctors() {
        List<Doctor> doctors = service.getAllDoctors();

        if (doctors.isEmpty()) {
            System.out.println("No doctors registered.");
            return;
        }

        System.out.println("\nAvailable Doctors:");
        for (Doctor doctor : doctors) {
            System.out.println(doctor.getId() + " | " + doctor.getName()
                    + " | " + doctor.getSpecialization());
        }
    }


    private static void bookAppointment() {

        if (service.getAllDoctors().isEmpty()) {
            System.out.println("No doctors available. Register a doctor first.");
            return;
        }

        String patientId = readRequiredId("Enter Patient ID: ");

        if (service.getPatientById(patientId) == null) {
            System.out.println("Patient not found. Register the patient first.");
            return;
        }

        viewDoctors();

        String doctorId = readRequiredId("Enter Doctor ID: ");

        Doctor doctor = service.getDoctorById(doctorId);

        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        String inputDate = readRequired("Enter Appointment Date and Hour (format: yyyy-MM-dd HH, e.g., 2025-01-15 14): ");

        LocalDateTime dateTime;

        try {

            dateTime = LocalDateTime.parse(inputDate + ":00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use: yyyy-MM-dd HH");
            return;
        }

        String appointmentId = "A" + System.currentTimeMillis();

        try {
            service.bookAppointment(appointmentId, patientId, doctor, dateTime);
            System.out.println("Appointment booked successfully.");
        } catch (Exception e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }

    private static void viewAppointments() {
        List<Appointment> appointments = service.getAllAppointments();

        if (appointments.isEmpty()) {
            System.out.println("No appointments booked.");
            return;
        }

        System.out.println("\nScheduled Appointments:");
        for (Appointment appointment : appointments) {
            System.out.println(appointment.getId() + " | Patient: "
                    + appointment.getPatient().getName() + " (" + appointment.getPatient().getId() + ")"
                    + " | Doctor: " + appointment.getDoctor().getName()
                    + " | Date: " + appointment.getDateTime().format(INPUT_FORMAT));
        }
    }


    private static void addMedicalRecord() {
        String patientId = readRequiredId("Enter Patient ID: ");

        if (service.getPatientById(patientId) == null) {
            System.out.println("Patient not found. Register the patient first.");
            return;
        }

        String data = readRequired("Enter Record Description: ");

        service.addMedicalRecord(patientId,
                new MedicalRecords<>("R" + System.currentTimeMillis(), data));

        System.out.println("Medical record added.");
    }


    private static void viewRecords() {
        String patientId = readRequiredId("Enter Patient ID: ");

        var records = service.getPatientRecords(patientId);

        if (records.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (var r : records) {
            System.out.println("Record ID: " + r.getId()
                    + " | Data: " + r.getData()
                    + " | Date: " + r.getCreatedAt().format(INPUT_FORMAT));
        }
    }

    private static String readRequired(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        return value;
    }


    private static String readRequiredName(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String value = scanner.nextLine().trim();
                return InputValidator.validateName(value, "Name");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    private static String readRequiredId(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String value = scanner.nextLine().trim();

                return InputValidator.validateId(value, "ID");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    private static String readRequiredSpecialization(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String value = scanner.nextLine().trim();

                return InputValidator.validateSpecialization(value, "Specialization");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}