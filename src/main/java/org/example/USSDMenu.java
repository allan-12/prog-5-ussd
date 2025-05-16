package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class USSDMenu {
    private final List<User> users;
    private final BufferedReader reader;

    public USSDMenu() {
        users = new ArrayList<>();
        reader = new BufferedReader(new InputStreamReader(System.in));
        users.add(new User("0321234567", 100000, "1234"));
        users.add(new User("0339876543", 50000, "5678"));
    }

    public void start() {
        System.out.println("Bienvenue sur la simulation Mvola ! Entrez #111# pour commencer.");
        String input = readInput();
        if (!input.equals("#111#")) {
            System.out.println("Code incorrect. Veuillez entrer #111#.");
            return;
        }
        authenticateUser();
    }

    private String readInput() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
            return "";
        }
    }

    private void authenticateUser() {
        System.out.println("Entrez votre numéro de téléphone (ex: 0321234567) :");
        String phoneNumber = readInput();
        User currentUser = findUser(phoneNumber);
        if (currentUser == null) {
            System.out.println("Utilisateur non trouvé.");
            return;
        }
        System.out.println("Entrez votre PIN :");
        String pin = readInput();
        if (!currentUser.verifyPin(pin)) {
            System.out.println("PIN incorrect.");
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
            System.out.println("7. Recevoir de l’argent");
            System.out.println("8. Banques et Micro-Finances");
            System.out.print("Choisissez une option (1-8) : ");

            String choice = readInput();
            switch (choice) {
                case "1" -> buyCreditOrOffer(user);
                case "2" -> transferMoney(user);
                case "3" -> mvolaCreditOrSavings(user);
                case "4" -> withdrawMoney(user);
                case "5" -> payBill(user);
                case "6" -> checkAccount(user);
                case "7" -> receiveMoney(user);
                case "8" -> banksAndMicroFinance(user);
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
        switch (choice) {
            case "1" -> buyCreditForOwnNumber(user);
            case "2" -> buyCreditForOtherNumber(user);
            case "3" -> buyOfferForOwnNumber(user);
            case "4" -> buyOfferForOtherNumber(user);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyCreditForOwnNumber(User user) {
        System.out.println("\n=== Crédit pour mon numéro ===");
        System.out.println("1. Recharge directement");
        System.out.println("2. Code de recharge");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        switch (choice) {
            case "1" -> rechargeDirectly(user, user.getPhoneNumber());
            case "2" -> manageCodeRecharge(user, user.getPhoneNumber());
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void buyCreditForOtherNumber(User user) {
        System.out.println("Entrez le numéro destinataire :");
        String phoneNumber = readInput();
        System.out.println("\n=== Crédit pour autre numéro ===");
        System.out.println("1. Recharge directement");
        System.out.println("2. Code de recharge");
        System.out.print("Choisissez une option (1-2) : ");

        String choice = readInput();
        switch (choice) {
            case "1" -> rechargeDirectly(user, phoneNumber);
            case "2" -> manageCodeRecharge(user, phoneNumber);
            default -> System.out.println("Option invalide. Veuillez réessayer.");
        }
    }

    private void rechargeDirectly(User user, String phoneNumber) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Dernier achat renvoyé.");
    }

    private void resendRechargeCodes(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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
        System.out.println("Votre offre actuelle est TOKANA");
        System.out.println("\n=== Offre pour autre numéro ===");
        System.out.println("1. MORA (VOIX-SMS-INTERNET)");
        System.out.println("2. FIRST (VOIX-SMS-INTERNET)");
        System.out.println("3. YELLOW (SMS-INTERNET)");
        System.out.println("4. YAS Net (INTERNET)");
        System.out.print("Choisissez une option (1-4) : ");

        String choice = readInput();
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
        User receiver = findUser(receiverNumber);
        if (receiver == null) {
            System.out.println("Destinataire non trouvé.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez la description du transfert :");
        String description = readInput();
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
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
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
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
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Solde épargne : " + user.getSavings() + " Ar");
    }

    private void simulateSavings(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
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
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
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
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Confirmez votre code secret :");
        String confirmPin = readInput();
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
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Numéro ajouté.");
    }

    private void removeNumberFromDirectory(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Liste des numéros disponibles : [Aucun]");
        System.out.println("Entrez le numéro à supprimer :");
        String number = readInput();
        System.out.println("Numéro supprimé.");
    }

    private void listDirectory(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Liste des numéros disponibles : [Aucun]");
    }

    private void fameno(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Solde AVANCE MIKASA : 0 Ar");
    }

    private void withdrawMoney(User user) {
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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
        System.out.println("Entrez le type de facture (ex: Eau, Électricité) :");
        String billType = readInput();
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        if (Transaction.payBill(user, billType, amount, pin)) {
            System.out.println("Réussi");
        } else {
            System.out.println("Solde insuffisant.");
        }
    }

    private void checkAccount(User user) {
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
        if (!user.verifyPin(pin)) {
            System.out.println("Code secret incorrect.");
            return;
        }
        System.out.println("Solde : " + user.getBalance() + " Ar");
        System.out.println("Épargne : " + user.getSavings() + " Ar");
    }

    private void receiveMoney(User user) {
        System.out.println("Entrez le numéro de l’expéditeur :");
        String senderNumber = readInput();
        User sender = findUser(senderNumber);
        if (sender == null) {
            System.out.println("Expéditeur non trouvé.");
            return;
        }
        System.out.println("Entrez le montant (Ar) :");
        String amountInput = readInput();
        double amount = parseAmount(amountInput);
        if (amount <= 0) {
            System.out.println("Montant invalide.");
            return;
        }
        System.out.println("Entrez votre code secret :");
        String pin = readInput();
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

    private void banksAndMicroFinance(User user) {
        System.out.println("Option non implémentée.");
    }

    private double parseAmount(String amountInput) {
        try {
            return Double.parseDouble(amountInput);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
