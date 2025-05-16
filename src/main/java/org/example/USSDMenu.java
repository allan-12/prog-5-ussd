package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class USSDMenu {
    private final List<User> users;
    private final BufferedReader reader;
    private final ExecutorService executorService;

    public USSDMenu() {
        users = new ArrayList<>();
        reader = new BufferedReader(new InputStreamReader(System.in));
        executorService = Executors.newSingleThreadExecutor();
        users.add(new User("0321234567", 100000, "1234"));
        users.add(new User("0339876543", 50000, "5678"));
    }

    public void start() {
        System.out.println("Bienvenue sur la simulation Mvola ! Entrez #111# pour commencer.");
        String input = readInput();
        if (input == null) {
            expireApplication();
            return;
        }
        if (!input.equals("#111#")) {
            System.out.println("Code incorrect. Veuillez entrer #111#.");
            cleanup();
            return;
        }
        authenticateUser();
        cleanup();
    }

    private String readInput() {
        Future<String> future = executorService.submit(() -> {
            try {
                return reader.readLine();
            } catch (IOException e) {
                System.out.println("Erreur de lecture : " + e.getMessage());
                return "";
            }
        });

        try {
            return future.get(20, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("Temps d'attente dépassé (20 secondes).");
            return null;
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Erreur lors de la lecture de l'entrée : " + e.getMessage());
            return "";
        }
    }

    private void expireApplication() {
        System.out.println("L'application a expiré en raison d'une inactivité de 20 secondes.");
        cleanup();
        System.exit(0);
    }

    private void cleanup() {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Erreur lors de la fermeture du reader : " + e.getMessage());
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void authenticateUser() {
        System.out.println("Entrez votre numéro de téléphone (ex: 0321234567) :");
        String phoneNumber = readInput();
        if (phoneNumber == null) {
            expireApplication();
            return;
        }
        User currentUser = findUser(phoneNumber);
        if (currentUser == null) {
            System.out.println("Utilisateur non trouvé.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!currentUser.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        showMainMenu(currentUser);
    }

    private User findUser(String phoneNumber) {
        for (User user : users) {
            if (user.getPhoneNumber().equals(phoneNumber)) {
                return user;
            }
        }
        return null;
    }

    private void showMainMenu(User user) {
        while (true) {
            System.out.println("\n=== MVOLA ===");
            System.out.println("1. Acheter Crédit ou Offre Yas");
            System.out.println("2. Transfert d’argent");
            System.out.println("3. Mvola Crédit ou Épargne");
            System.out.println("4. Retrait d’argent");
            System.out.println("5. Paiement Factures & Partenaires");
            System.out.println("6. Mon compte");
            System.out.print("Choisissez une option (1-6) : ");

            String choice = readInput();
            if (choice == null) {
                expireApplication();
                return;
            }
            switch (choice) {
                case "1" -> buyCreditOrOffer(user);
                case "2" -> transferMoney(user);
                case "3" -> mvolaCreditOrSavings(user);
                case "4" -> withdrawMoney(user);
                case "5" -> payBill(user);
                case "6" -> checkAccount(user);
                default -> System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void buyCreditOrOffer(User user) {
        System.out.println("\n=== Acheter Crédit ou Offre Yas ===");
        System.out.println("1. Crédit pour mon numéro");
        System.out.println("2. Crédit pour autre numéro");
        System.out.println("3. Offre pour mon numéro");
        System.out.println("4. Offre pour autre numéro");
        System.out.print("Choisissez une option (1-4) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyCreditForOwnNumber(user);
            case "2" -> buyCreditForOtherNumber(user);
            case "3" -> buyOfferForOwnNumber(user);
            case "4" -> buyOfferForOtherNumber(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyCreditForOwnNumber(User user) {
        System.out.println("\n=== Crédit pour mon HOLÀ numéro ===");
        System.out.println("1. Recharge directement");
        System.out.println("2. Code de recharge");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> rechargeDirectly(user, user.getPhoneNumber());
            case "2" -> manageCodeRecharge(user, user.getPhoneNumber());
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyCreditForOtherNumber(User user) {
        System.out.println("Entrez le numéro destinataire :");
        String phoneNumber = readInput();
        if (phoneNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("\n=== Crédit pour autre numéro ===");
        System.out.println("1. Recharge directement");
        System.out.println("2. Code de recharge");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> rechargeDirectly(user, phoneNumber);
            case "2" -> manageCodeRecharge(user, phoneNumber);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void rechargeDirectly(User user, String phoneNumber) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.buyCredit(user, amount, phoneNumber, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void manageCodeRecharge(User user, String phoneNumber) {
        System.out.println("\n=== Code de recharge ===");
        System.out.println("1. Acheter code de recharge");
        System.out.println("2. Renvoyer dernier achat");
        System.out.println("3. Renvoyer mes codes recharge");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyRechargeCode(user, phoneNumber);
            case "2" -> resendLastPurchase(user);
            case "3" -> resendRechargeCodes(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyRechargeCode(User user, String phoneNumber) {
        System.out.println("\n=== Acheter code de recharge ===");
        System.out.println("1. Code Recharge 1000");
        System.out.println("2. Code Recharge 2000");
        System.out.println("3. Code Recharge 5000");
        System.out.println("4. Code Recharge 10000");
        System.out.println("5. Code Recharge 20000");
        System.out.println("6. Code Recharge 25000");
        System.out.print("Choisissez une option (1-6) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        double amount = switch (choice) {
            case "1" -> 1000;
            case "2" -> 2000;
            case "3" -> 5000;
            case "4" -> 10000;
            case "5" -> 20000;
            case "6" -> 25000;
            default -> -1;
        };
        if (amount == -1) {
            System.out.println("Option invalide. Veuillez réessayer.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.buyCredit(user, amount, phoneNumber, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void resendLastPurchase(User user) {
        System.out.println("Saisir votre référence de transaction :");
        String reference = readInput();
        if (reference == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Dernier achat renvoyé.");
    }

    private void resendRechargeCodes(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Codes recharge renvoyés.");
    }

    private void buyOfferForOwnNumber(User user) {
        System.out.println("Votre offre actuelle est TOKANA");
        System.out.println("\n=== Offre pour mon numéro ===");
        System.out.println("1. MORA (VOIX-SMS-INTERNET)");
        System.out.println("2. FIRST (VOIX-SMS-INTERNET)");
        System.out.println("3. YELLOW (SMS-INTERNET)");
        System.out.println("4. YAS Net (INTERNET)");
        System.out.print("Choisissez une option (1-4) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyMoraOffer(user, user.getPhoneNumber());
            case "2" -> buyFirstOffer(user, user.getPhoneNumber());
            case "3", "4" -> System.out.println("Option non implémentée.");
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyOfferForOtherNumber(User user) {
        System.out.println("Entrez le numéro destinataire :");
        String phoneNumber = readInput();
        if (phoneNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Votre offre actuelle est TOKANA");
        System.out.println("\n=== Offre pour autre numéro ===");
        System.out.println("1. MORA (VOIX-SMS-INTERNET)");
        System.out.println("2. FIRST (VOIX-SMS-INTERNET)");
        System.out.println("3. YELLOW (SMS-INTERNET)");
        System.out.println("4. YAS Net (INTERNET)");
        System.out.print("Choisissez une option (1-4) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyMoraOffer(user, phoneNumber);
            case "2" -> buyFirstOffer(user, phoneNumber);
            case "3", "4" -> System.out.println("Option non implémentée.");
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyMoraOffer(User user, String phoneNumber) {
        System.out.println("\n=== MORA (VOIX-SMS-INTERNET) ===");
        System.out.println("1. MORA 500 (500Ar)");
        System.out.println("2. MORA ONE (1000Ar)");
        System.out.println("3. MORA+ 2000 (2000Ar)");
        System.out.println("4. MORA+ 5000 (5000Ar)");
        System.out.println("5. MORA INTERNATION (5000Ar)");
        System.out.print("Choisissez une option (1-5) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        double amount = switch (choice) {
            case "1" -> 500;
            case "2" -> 1000;
            case "3" -> 2000;
            case "4", "5" -> 5000;
            default -> -1;
        };
        if (amount == -1) {
            System.out.println("Option invalide. Veuillez réessayer.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.buyCredit(user, amount, phoneNumber, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void buyFirstOffer(User user, String phoneNumber) {
        System.out.println("\n=== FIRST (VOIX-SMS-INTERNET) ===");
        System.out.println("1. FIRST PREMIUM (10000Ar)");
        System.out.println("2. FIRST PREMIUM+ (15000Ar)");
        System.out.println("3. FIRST PRESTIGE (30000Ar)");
        System.out.println("4. FIRST ROYAL (50000Ar)");
        System.out.print("Choisissez une option (1-4) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        double amount = switch (choice) {
            case "1" -> 10000;
            case "2" -> 15000;
            case "3" -> 30000;
            case "4" -> 50000;
            default -> -1;
        };
        if (amount == -1) {
            System.out.println("Option invalide. Veuillez réessayer.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.buyCredit(user, amount, phoneNumber, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void transferMoney(User user) {
        System.out.println("Entrez le numéro de téléphone destinataire :");
        String receiverNumber = readInput();
        if (receiverNumber == null) {
            expireApplication();
            return;
        }
        User receiver = findUser(receiverNumber);
        if (receiver == null) {
            System.out.println("Destinataire non trouvé.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez la description du transfert :");
        String description = readInput();
        if (description == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.transferMoney(user, receiver, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void mvolaCreditOrSavings(User user) {
        System.out.println("\n=== Mvola Crédit ou Épargne ===");
        System.out.println("1. Mvola Épargne");
        System.out.println("2. Mvola Crédit");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> mvolaSavings(user);
            case "2" -> mvolaCredit(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void mvolaSavings(User user) {
        System.out.println("\n=== Mvola Épargne ===");
        System.out.println("1. Transfert vers Mvola Épargne");
        System.out.println("2. Transfert vers compte Mvola");
        System.out.println("3. Consultation de solde");
        System.out.println("4. Simulateur Épargne");
        System.out.print("Choisissez une option (1-4) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> transferToSavings(user);
            case "2" -> transferToMvolaAccount(user);
            case "3" -> checkSavingsBalance(user);
            case "4" -> simulateSavings(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void transferToSavings(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
        if (confirmPin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(confirmPin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.saveMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void transferToMvolaAccount(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
        if (confirmPin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(confirmPin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.saveMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void checkSavingsBalance(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Solde épargne : " + user.getSavings() + " Ar");
    }

    private void simulateSavings(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
        if (confirmPin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(confirmPin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Simulation effectuée.");
    }

    private void mvolaCredit(User user) {
        System.out.println("\n=== Mvola Crédit ===");
        System.out.println("1. Mvola Avance");
        System.out.println("2. FAMENO");
        System.out.println("3. AVANCE MIKASA");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> mvolaAdvance(user);
            case "2" -> fameno(user);
            case "3" -> avanceMikasa(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void mvolaAdvance(User user) {
        System.out.println("\n=== Mvola Avance ===");
        System.out.println("1. Demander une avance");
        System.out.println("2. Rembourser une avance");
        System.out.println("3. Consulter une avance en cours");
        System.out.println("4. Répertoire Mvola Avance");
        System.out.print("Choisissez une option (1-4) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> requestAdvance(user);
            case "2" -> repayAdvance(user);
            case "3" -> checkAdvanceBalance(user);
            case "4" -> manageAdvanceDirectory(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void requestAdvance(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
        if (confirmPin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(confirmPin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.borrowMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Demande refusée.");
        }
    }

    private void repayAdvance(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
        if (confirmPin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(confirmPin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.borrowMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void checkAdvanceBalance(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Solde Mvola Avance : 0 Ar");
    }

    private void manageAdvanceDirectory(User user) {
        System.out.println("\n=== Répertoire Mvola Avance ===");
        System.out.println("1. Ajouter un numéro");
        System.out.println("2. Supprimer un numéro");
        System.out.println("3. Consulter répertoire");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> addNumberToDirectory(user);
            case "2" -> removeNumberFromDirectory(user);
            case "3" -> listDirectory(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void addNumberToDirectory(User user) {
        System.out.println("Entrez le numéro autorisé à rembourser vos avances :");
        String number = readInput();
        if (number == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Numéro ajouté.");
    }

    private void removeNumberFromDirectory(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Liste des numéros disponibles : [Aucun]");
        System.out.println("Entrez le numéro à supprimer :");
        String number = readInput();
        if (number == null) {
            expireApplication();
            return;
        }
        System.out.println("Numéro supprimé.");
    }

    private void listDirectory(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Liste des numéros disponibles : [Aucun]");
    }

    private void fameno(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Réussi");
    }

    private void avanceMikasa(User user) {
        System.out.println("\n=== AVANCE MIKASA ===");
        System.out.println("1. Demander une avance");
        System.out.println("2. Rembourser une avance");
        System.out.println("3. Consulter une avance");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> requestMikasaAdvance(user);
            case "2" -> repayMikasaAdvance(user);
            case "3" -> checkMikasaAdvance(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void requestMikasaAdvance(User user) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.borrowMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Demande refusée.");
        }
    }

    private void repayMikasaAdvance(User user) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.borrowMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void checkMikasaAdvance(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Solde AVANCE MIKASA : 0 Ar");
    }

    private void withdrawMoney(User user) {
        System.out.println("\n=== Retrait d’argent ===");
        System.out.println("1. Auprès d’un Agent Mvola");
        System.out.println("2. Auprès d’un DAB SGM");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> withdrawFromAgent(user);
            case "2" -> System.out.println("Option non implémentée.");
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void withdrawFromAgent(User user) {
        System.out.println("Entrez le numéro de téléphone de l’agent Mvola :");
        String agentNumber = readInput();
        if (agentNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.withdrawMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payBill(User user) {
        System.out.println("\n=== Paiement Factures & Partenaires ===");
        System.out.println("1. Accepter une demande d’argent");
        System.out.println("2. YAS ou MOOV");
        System.out.println("3. Électricité et eau");
        System.out.println("4. Assurances");
        System.out.println("5. TV & Loisirs");
        System.out.println("6. Transports Voyages");
        System.out.println("7. Impôts et cotisations sociales");
        System.out.println("8. Écoles et universités");
        System.out.println("9. Autres fournisseurs");
        System.out.print("Choisissez une option (1-9) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> acceptMoneyRequest(user);
            case "2" -> payYasOrMoov(user);
            case "3" -> payElectricityAndWater(user);
            case "4" -> payInsurance(user);
            case "5", "6", "7", "8", "9" -> System.out.println("Option non implémentée.");
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void acceptMoneyRequest(User user) {
        System.out.println("Entrez le numéro de l’expéditeur :");
        String senderNumber = readInput();
        if (senderNumber == null) {
            expireApplication();
            return;
        }
        User sender = findUser(senderNumber);
        if (sender == null) {
            System.out.println("Expéditeur non trouvé.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.transferMoney(sender, user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payYasOrMoov(User user) {
        System.out.println("Entrez le type (YAS ou MOOV) :");
        String type = readInput();
        if (type == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, type, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payElectricityAndWater(User user) {
        System.out.println("\n=== Électricité et eau ===");
        System.out.println("1. MBALIKA");
        System.out.println("2. Jirama");
        System.out.println("3. Baobab+");
        System.out.println("4. WELIGHT");
        System.out.println("5. HERI");
        System.out.println("6. Anka Madagascar");
        System.out.print("Choisissez une option (1-6) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> payMbalika(user);
            case "2" -> payJirama(user);
            case "3" -> payBaobabPlus(user);
            case "4" -> payWelight(user);
            case "5" -> payHeri(user);
            case "6" -> payAnkaMadagascar(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void payMbalika(User user) {
        System.out.println("\n=== MBALIKA ===");
        System.out.println("1. Achat de kit");
        System.out.println("2. Achat recharge");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyMbalikaKit(user);
            case "2" -> buyMbalikaRecharge(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyMbalikaKit(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Réussi");
    }

    private void buyMbalikaRecharge(User user) {
        System.out.println("Entrez le numéro du kit :");
        String kitNumber = readInput();
        if (kitNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "MBALIKA Recharge", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payJirama(User user) {
        System.out.println("\n=== Jirama ===");
        System.out.println("1. Compteur prépayé");
        System.out.println("2. Paiement de facture");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> payJiramaPrepaid(user);
            case "2" -> payJiramaBill(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void payJiramaPrepaid(User user) {
        System.out.println("\n=== Compteur prépayé ===");
        System.out.println("1. Acheter recharge");
        System.out.println("2. Renvoyer dernier code recharge");
        System.out.println("3. Régulariser arriéré");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyJiramaRecharge(user);
            case "2" -> resendLastJiramaRecharge(user);
            case "3" -> regularizeJiramaArrears(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyJiramaRecharge(User user) {
        System.out.println("Entrez la référence client (11 caractères) :");
        String clientReference = readInput();
        if (clientReference == null) {
            expireApplication();
            return;
        }
        if (clientReference.length() != 11) {
            System.out.println("Référence client invalide.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "Jirama Recharge", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void resendLastJiramaRecharge(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Dernier code recharge renvoyé.");
    }

    private void regularizeJiramaArrears(User user) {
        System.out.println("Entrez la référence client (11 caractères) :");
        String clientReference = readInput();
        if (clientReference == null) {
            expireApplication();
            return;
        }
        if (clientReference.length() != 11) {
            System.out.println("Référence client invalide.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "Jirama Arriéré", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payJiramaBill(User user) {
        System.out.println("\n=== Paiement de facture ===");
        System.out.println("1. S’inscrire au paiement de Jirama");
        System.out.println("2. Payer mes factures");
        System.out.println("3. Payer facture d’un tiers");
        System.out.println("4. Historique de paiement");
        System.out.println("5. Auto relevé");
        System.out.println("6. Reçu par email");
        System.out.print("Choisissez une option (1-6) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> subscribeToJiramaPayment(user);
            case "2" -> payOwnJiramaBills(user);
            case "3" -> payThirdPartyJiramaBill(user);
            case "4" -> viewJiramaPaymentHistory(user);
            case "5" -> autoJiramaReading(user);
            case "6" -> sendJiramaReceiptByEmail(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void subscribeToJiramaPayment(User user) {
        System.out.println("Entrez la référence client Jirama :");
        String clientReference = readInput();
        if (clientReference == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Inscription réussie.");
    }

    private void payOwnJiramaBills(User user) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "Jirama Facture", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payThirdPartyJiramaBill(User user) {
        System.out.println("Entrez la référence client Jirama :");
        String clientReference = readInput();
        if (clientReference == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "Jirama Facture Tiers", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void viewJiramaPaymentHistory(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Historique de paiement : [Aucun]");
    }

    private void autoJiramaReading(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Auto relevé effectué.");
    }

    private void sendJiramaReceiptByEmail(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Reçu envoyé par email.");
    }

    private void payBaobabPlus(User user) {
        System.out.println("\n=== Baobab+ ===");
        System.out.println("1. Achat recharge Baobab+");
        System.out.println("2. Renvoi de dernier recharge");
        System.out.println("3. Suppression numéro");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyBaobabPlusRecharge(user);
            case "2" -> resendLastBaobabPlusRecharge(user);
            case "3" -> deleteBaobabPlusNumber(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyBaobabPlusRecharge(User user) {
        System.out.println("Entrez le numéro de la lampe :");
        String lampNumber = readInput();
        if (lampNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "Baobab+ Recharge", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void resendLastBaobabPlusRecharge(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Dernier recharge renvoyé.");
    }

    private void deleteBaobabPlusNumber(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Numéro supprimé.");
    }

    private void payWelight(User user) {
        System.out.println("\n=== WELIGHT ===");
        System.out.println("1. Achat de recharge");
        System.out.println("2. Gestion de client");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyWelightRecharge(user);
            case "2" -> manageWelightClient(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyWelightRecharge(User user) {
        System.out.println("Entrez le numéro de compteur :");
        String meterNumber = readInput();
        if (meterNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "WELIGHT Recharge", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void manageWelightClient(User user) {
        System.out.println("\n=== Gestion de client ===");
        System.out.println("1. Souscription");
        System.out.println("2. Renvoi des codes recharges");
        System.out.println("3. Augmentation puissance compteur");
        System.out.println("4. Déplacement de compteur");
        System.out.println("5. Suppression de numéro de compteur");
        System.out.print("Choisissez une option (1-5) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> subscribeWelight(user);
            case "2" -> resendWelightRechargeCodes(user);
            case "3" -> increaseWelightMeterPower(user);
            case "4" -> relocateWelightMeter(user);
            case "5" -> deleteWelightMeterNumber(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void subscribeWelight(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Souscription réussie.");
    }

    private void resendWelightRechargeCodes(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Codes recharges renvoyés.");
    }

    private void increaseWelightMeterPower(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Puissance compteur augmentée.");
    }

    private void relocateWelightMeter(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Compteur déplacé.");
    }

    private void deleteWelightMeterNumber(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Numéro de compteur supprimé.");
    }

    private void payHeri(User user) {
        System.out.println("\n=== HERI ===");
        System.out.println("1. Achat recharge HERI");
        System.out.println("2. Renvoi de dernier recharge");
        System.out.println("3. Suppression numéro");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> buyHeriRecharge(user);
            case "2" -> resendLastHeriRecharge(user);
            case "3" -> deleteHeriNumber(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyHeriRecharge(User user) {
        System.out.println("Entrez le numéro de la lampe :");
        String lampNumber = readInput();
        if (lampNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "HERI Recharge", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void resendLastHeriRecharge(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Dernier recharge renvoyé.");
    }

    private void deleteHeriNumber(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Numéro supprimé.");
    }

    private void payAnkaMadagascar(User user) {
        System.out.println("Entrez le numéro de compteur Anka Madagascar :");
        String meterNumber = readInput();
        if (meterNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "Anka Madagascar Recharge", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payInsurance(User user) {
        System.out.println("\n=== Assurances ===");
        System.out.println("1. MVOLA ASSURE");
        System.out.println("2. MTOMADY");
        System.out.println("3. ALLIANZ");
        System.out.println("4. ARO");
        System.out.println("5. SANLAM");
        System.out.println("6. NY HAVANA");
        System.out.print("Choisissez une option (1-6) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> payMvolaAssure(user);
            case "2" -> payMtomady(user);
            case "3", "4", "5", "6" -> System.out.println("Option non implémentée.");
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void payMvolaAssure(User user) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, "MVOLA ASSURE", amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void payMtomady(User user) {
        System.out.println("\n=== MTOMADY ===");
        System.out.println("1. S’inscrire au service");
        System.out.println("2. Épargne pour d’autre");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> subscribeMtomady(user);
            case "2" -> saveForOtherMtomady(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void subscribeMtomady(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Inscription réussie.");
    }

    private void saveForOtherMtomady(User user) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        if (amountInput == null) {
            expireApplication();
            return;
        }
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.saveMoney(user, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void checkAccount(User user) {
        System.out.println("\n=== Mon compte ===");
        System.out.println("1. Consultation du solde");
        System.out.println("2. Consulter mes 3 dernières transactions");
        System.out.println("3. Mon répertoire Mvola");
        System.out.println("4. Mon numéro d’identification");
        System.out.println("5. Mon code secret");
        System.out.print("Choisissez une option (1-5) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> checkBalance(user);
            case "2" -> checkLastTransactions(user);
            case "3" -> manageMvolaDirectory(user);
            case "4" -> checkIdentification(user);
            case "5" -> managePin(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void checkBalance(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Votre solde actuel est de : " + user.getBalance() + " Ar");
    }

    private void checkLastTransactions(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Liste des 3 dernières transactions : [Aucune]");
    }

    private void manageMvolaDirectory(User user) {
        System.out.println("\n=== Mon répertoire Mvola ===");
        System.out.println("1. Ajouter un contact");
        System.out.println("2. Consulter un contact");
        System.out.println("3. Supprimer un contact");
        System.out.print("Choisissez une option (1-3) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> addMvolaContact(user);
            case "2" -> viewMvolaContact(user);
            case "3" -> deleteMvolaContact(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void addMvolaContact(User user) {
        System.out.println("Entrez le numéro du contact :");
        String contactNumber = readInput();
        if (contactNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Contact ajouté.");
    }

    private void viewMvolaContact(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Liste des contacts : [Aucun]");
    }

    private void deleteMvolaContact(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Liste des contacts disponibles : [Aucun]");
        System.out.println("Entrez le numéro à supprimer :");
        String contactNumber = readInput();
        if (contactNumber == null) {
            expireApplication();
            return;
        }
        System.out.println("Contact supprimé.");
    }

    private void checkIdentification(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (pin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Informations du compte : Numéro = " + user.getPhoneNumber());
    }

    private void managePin(User user) {
        System.out.println("\n=== Mon code secret ===");
        System.out.println("1. Changer mon code secret");
        System.out.println("2. Code secret oublié");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        if (choice == null) {
            expireApplication();
            return;
        }
        switch (choice) {
            case "1" -> changePin(user);
            case "2" -> recoverPin(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void changePin(User user) {
        System.out.println("Entrez votre ancien code secret :");
        String oldPin = readInput();
        if (oldPin == null) {
            expireApplication();
            return;
        }
        if (!user.verifyPin(oldPin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le nouveau code secret :");
        String newPin = readInput();
        if (newPin == null) {
            expireApplication();
            return;
        }
        System.out.println("Confirmez le nouveau code secret :");
        String confirmPin = readInput();
        if (confirmPin == null) {
            expireApplication();
            return;
        }
        if (!newPin.equals(confirmPin)) {
            System.out.println("Les codes secrets ne correspondent pas.");
            return;
        }
        user.changePin(newPin);
        System.out.println("Code secret changé avec succès.");
    }

    private void recoverPin(User user) {
        System.out.println("Entrez votre numéro de téléphone :");
        String phoneNumber = readInput();
        if (phoneNumber == null) {
            expireApplication();
            return;
        }
        if (!user.getPhoneNumber().equals(phoneNumber)) {
            System.out.println("Numéro incorrect.");
            return;
        }
        System.out.println("Un code de récupération a été envoyé.");
    }

    private double parseAmount(String amountInput) {
        try {
            return Double.parseDouble(amountInput);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
