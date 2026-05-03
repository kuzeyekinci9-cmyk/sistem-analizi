package com.sitemanagement.main;

import com.sitemanagement.enums.Role;
import com.sitemanagement.enums.TicketStatus;
import com.sitemanagement.enums.TransactionType;
import com.sitemanagement.managers.*;
import com.sitemanagement.models.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.List;

public class MainApp extends Application {

    private Stage primaryStage;
    private StackPane mainContainer;

    private ResidentManager residentManager = new ResidentManager();
    private FinanceManager financeManager = new FinanceManager();
    private TicketManager ticketManager = new TicketManager();
    private ParkingManager parkingManager = new ParkingManager();
    private CommunicationManager commManager = new CommunicationManager();

    private Resident loggedInUser = null;
    private int currentUserId = 0;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainContainer = new StackPane();
        Scene scene = new Scene(mainContainer, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SyS - Akıllı Site Yönetim Sistemi");
        showLoginScreen();
        primaryStage.show();
    }

    private void showLoginScreen() {
        VBox loginBox = new VBox(20);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setStyle("-fx-background-color: #34495e;");

        VBox card = new VBox(15);
        card.setMaxSize(400, 350);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label lblTitle = new Label("Sisteme Giriş");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Telefon Numarası");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Şifre");

        ComboBox<Role> cmbRole = new ComboBox<>(FXCollections.observableArrayList(Role.ADMIN, Role.RESIDENT));
        cmbRole.setPromptText("Giriş Rolü Seçiniz");
        cmbRole.setMaxWidth(Double.MAX_VALUE);

        Button btnLogin = new Button("Giriş Yap");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        btnLogin.setOnAction(e -> {
            Role selectedRole = cmbRole.getValue();
            String phone = txtUsername.getText();
            String pass = txtPass.getText();

            if (selectedRole != null && !phone.isEmpty() && !pass.isEmpty()) {
                if (selectedRole == Role.ADMIN && phone.equals("admin") && pass.equals("admin")) {
                    currentUserId = 0;
                    showDashboard(Role.ADMIN);
                    return;
                }
                Resident user = residentManager.authenticateUser(phone, pass, selectedRole);
                if (user != null) {
                    loggedInUser = user;
                    currentUserId = user.getId();
                    showDashboard(selectedRole);
                } else {
                    showAlert("Hata", "Giriş bilgileri hatalı!");
                }
            }
        });

        card.getChildren().addAll(lblTitle, txtUsername, txtPass, cmbRole, btnLogin);
        loginBox.getChildren().add(card);
        mainContainer.getChildren().setAll(loginBox);
    }

    private void showDashboard(Role role) {
        BorderPane dashboard = new BorderPane();
        VBox sideMenu = new VBox(10);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(250);
        sideMenu.setStyle("-fx-background-color: #2c3e50;");

        StackPane contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #ecf0f1;");

        Label lblHeader = new Label(role == Role.ADMIN ? "YÖNETİCİ PANELİ" : "SAKİN PANELİ");
        lblHeader.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        sideMenu.getChildren().addAll(lblHeader, new Separator());

        if (role == Role.ADMIN)
            setupAdminMenu(sideMenu, contentArea);
        else
            setupResidentMenu(sideMenu, contentArea);

        Button btnLogout = new Button("Çıkış Yap");
        btnLogout.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            loggedInUser = null;
            showLoginScreen();
        });
        sideMenu.getChildren().add(new VBox(btnLogout));

        dashboard.setLeft(sideMenu);
        dashboard.setCenter(contentArea);
        mainContainer.getChildren().setAll(dashboard);
    }

    private void setupAdminMenu(VBox menu, StackPane content) {
        Button btnRes = createMenuBtn("🏠 Daire & Sakin Yönetimi");
        Button btnFin = createMenuBtn("💰 Aidat & Finans");
        Button btnPark = createMenuBtn("🚗 Otopark Yönetimi");
        Button btnTick = createMenuBtn("🛠️ Arıza Yönetimi");
        Button btnComm = createMenuBtn("📢 İletişim & Duyuru");

        btnRes.setOnAction(e -> content.getChildren().setAll(buildAdminResidentView()));
        btnFin.setOnAction(e -> content.getChildren().setAll(buildAdminFinanceView()));
        btnPark.setOnAction(e -> content.getChildren().setAll(buildAdminParkingView()));
        btnTick.setOnAction(e -> content.getChildren().setAll(buildAdminTicketView()));
        btnComm.setOnAction(e -> content.getChildren().setAll(buildAdminCommView()));

        menu.getChildren().addAll(btnRes, btnFin, btnPark, btnTick, btnComm);
        content.getChildren().setAll(buildAdminResidentView());
    }

    private VBox buildAdminResidentView() {
        VBox layout = new VBox(15);
        Label title = new Label("Sistem ve Daire Yönetimi");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label lblApt = new Label("Daireler");
        lblApt.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9;");

        TableView<Apartment> tableApt = new TableView<>();
        tableApt.getColumns().addAll(
                createCol("Daire ID", "id"),
                createCol("Blok", "blockName"),
                createCol("Kat", "floorNumber"),
                createCol("Kapı No", "doorNumber"),
                createCol("Dolu Mu?", "occupied"), // Dairenin dolu olup olmadığını görmek için
                createCol("Oturan Kişi", "residentName") // Artık tekil olarak sadece sahibini gösteriyor
        );
        tableApt.setItems(FXCollections.observableArrayList(residentManager.getAllApartments()));
        tableApt.setPrefHeight(150);

        HBox formApt = new HBox(10);
        TextField txtBlock = new TextField();
        txtBlock.setPromptText("Blok (Örn: A)");
        TextField txtFloor = new TextField();
        txtFloor.setPromptText("Kat (Örn: 2)");
        TextField txtDoor = new TextField();
        txtDoor.setPromptText("Kapı No (Örn: 5)");
        Button btnSaveApt = new Button("Yeni Daire Oluştur");
        btnSaveApt.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        btnSaveApt.setOnAction(e -> {
            try {
                if (residentManager.createApartment(txtBlock.getText(), Integer.parseInt(txtFloor.getText()),
                        Integer.parseInt(txtDoor.getText()))) {
                    tableApt.setItems(FXCollections.observableArrayList(residentManager.getAllApartments()));
                    showAlert("Başarılı", "Daire başarıyla oluşturuldu.");
                    txtBlock.clear();
                    txtFloor.clear();
                    txtDoor.clear();
                }
            } catch (Exception ex) {
                showAlert("Hata", "Lütfen kat ve kapı numarasına sadece sayı giriniz.");
            }
        });
        formApt.getChildren().addAll(txtBlock, txtFloor, txtDoor, btnSaveApt);

        Label lblRes = new Label("Site Sakinleri");
        lblRes.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        TableView<Resident> tableRes = new TableView<>();
        tableRes.getColumns().addAll(createCol("ID", "id"), createCol("Ad Soyad", "fullName"),
                createCol("Telefon", "phone"));
        tableRes.setItems(FXCollections.observableArrayList(residentManager.getAllResidents()));
        tableRes.setPrefHeight(150);

        HBox actionBox = new HBox(10);

        Button btnDelete = new Button("Seçili Sakini Sil");
        btnDelete.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            Resident selected = tableRes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (residentManager.removeResident(selected.getId())) {
                    tableRes.setItems(FXCollections.observableArrayList(residentManager.getAllResidents()));
                    showAlert("Başarılı", "Sakin sistemden silindi.");
                }
            } else {
                showAlert("Uyarı", "Lütfen tablodan silinecek kişiyi seçin.");
            }
        });

        // Seçili daireye seçili sakini atar
        Button btnAssign = new Button("Seçili Sakini Seçili Daireye Ata");
        btnAssign.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAssign.setOnAction(e -> {
            Resident selectedRes = tableRes.getSelectionModel().getSelectedItem();
            Apartment selectedApt = tableApt.getSelectionModel().getSelectedItem();

            if (selectedRes != null && selectedApt != null) {
                if (residentManager.assignToApartment(selectedRes.getId(), selectedApt.getId())) {
                    showAlert("Başarılı", selectedRes.getFullName() + " isimli sakin " +
                            selectedApt.getBlockName() + " Blok, No: " + selectedApt.getDoorNumber()
                            + " adresine atandı.");
                    tableApt.setItems(FXCollections.observableArrayList(residentManager.getAllApartments()));
                } else {
                    showAlert("Hata", "Atama işlemi başarısız oldu.");
                }
            } else {
                showAlert("Bilgi",
                        "Lütfen atama yapmak için üst tablodan bir 'Daire' ve alt tablodan bir 'Sakin' seçin.");
            }
        });

        actionBox.getChildren().addAll(btnDelete, btnAssign);
        // ----------------------------------------------------

        HBox formRes = new HBox(10);
        TextField txtName = new TextField();
        txtName.setPromptText("Ad Soyad");
        TextField txtPhone = new TextField();
        txtPhone.setPromptText("Telefon");
        TextField txtPass = new TextField();
        txtPass.setPromptText("Şifre");
        Button btnSaveRes = new Button("Sakin Kaydet");
        btnSaveRes.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnSaveRes.setOnAction(e -> {
            if (residentManager.registerResident(
                    new Resident(0, txtName.getText(), txtPhone.getText(), txtPass.getText(), Role.RESIDENT))) {
                tableRes.setItems(FXCollections.observableArrayList(residentManager.getAllResidents()));
                showAlert("Başarılı", "Sakin kaydedildi.");
                txtName.clear();
                txtPhone.clear();
                txtPass.clear(); // Temizleme
            }
        });
        formRes.getChildren().addAll(txtName, txtPhone, txtPass, btnSaveRes);

        layout.getChildren().addAll(
                title,
                lblApt, tableApt, formApt,
                new Separator(),
                lblRes, tableRes, formRes, actionBox);

        return layout;
    }

    private VBox buildAdminTicketView() {
        VBox layout = new VBox(15);
        Label title = new Label("Arıza Talepleri Yönetimi");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<MaintenanceTicket> table = new TableView<>();
        table.getColumns().addAll(createCol("ID", "ticketId"), createCol("Başlık", "title"),
                createCol("Durum", "status"));
        table.setItems(FXCollections.observableArrayList(ticketManager.getAllTickets()));

        HBox form = new HBox(10);
        ComboBox<TicketStatus> cmbStatus = new ComboBox<>(FXCollections.observableArrayList(TicketStatus.values()));
        cmbStatus.setPromptText("Yeni Durum");
        Button btnUpdate = new Button("Durumu Güncelle");
        btnUpdate.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");

        btnUpdate.setOnAction(e -> {
            MaintenanceTicket selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (cmbStatus.getValue() != null) {
                    ticketManager.updateTicketStatus(selected.getTicketId(), cmbStatus.getValue());
                    showAlert("Başarılı", "Arıza durumu güncellendi.");
                }
                table.setItems(FXCollections.observableArrayList(ticketManager.getAllTickets()));
            } else {
                showAlert("Uyarı", "Lütfen tablodan bir arıza seçin.");
            }
        });

        form.getChildren().addAll(cmbStatus, btnUpdate);
        layout.getChildren().addAll(title, table, new Label("Durum Değiştir:"), form);
        return layout;
    }

    private VBox buildAdminCommView() {
        VBox layout = new VBox(15);
        Label title = new Label("İletişim ve Doküman Paylaşımı");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox formAnnounce = new HBox(10);
        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Duyuru Başlığı");
        TextField txtContent = new TextField();
        txtContent.setPromptText("İçerik...");
        Button btnPublish = new Button("Duyuru Yayınla");
        btnPublish.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;");
        btnPublish.setOnAction(e -> {
            if (commManager.publishAnnouncement(txtTitle.getText(), txtContent.getText())) {
                showAlert("Başarılı", "Duyuru yayınlandı.");
                txtTitle.clear();
                txtContent.clear();
            }
        });
        formAnnounce.getChildren().addAll(txtTitle, txtContent, btnPublish);

        HBox formPoll = new HBox(10);
        TextField txtPollQ = new TextField();
        txtPollQ.setPromptText("Anket Sorusu");
        Button btnPoll = new Button("Anket Başlat");
        btnPoll.setOnAction(e -> {
            String[] options = { "Evet", "Hayır" };
            if (commManager.createPoll(txtPollQ.getText(), options)) {
                showAlert("Başarılı", "Anket sisteme eklendi.");
                txtPollQ.clear();
            }
        });
        formPoll.getChildren().addAll(txtPollQ, btnPoll);

        layout.getChildren().addAll(title, new Label("Yeni Duyuru:"), formAnnounce, new Separator(),
                new Label("Yeni Anket (Evet/Hayır):"), formPoll);
        return layout;
    }

    private VBox buildAdminFinanceView() {
        VBox layout = new VBox(15);
        Label title = new Label("Finans ve Raporlama");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Transaction> reportTable = new TableView<>();
        reportTable.getColumns().addAll(
                createCol("İşlem ID", "transactionId"),
                createCol("Sakin ID", "residentId"),
                createCol("Tutar", "amount"),
                createCol("Tür", "type"),
                createCol("Açıklama", "description"));
        reportTable.setItems(FXCollections.observableArrayList(financeManager.getSiteGeneralReport()));

        HBox form = new HBox(10);
        
        // Kullanıcıya ipucu verdik: "Toplu için boş bırak"
        TextField txtResId = new TextField();
        txtResId.setPromptText("Sakin ID (Toplu için boş bırak)"); 
        txtResId.setPrefWidth(180);
        
        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Tutar (TL)");
        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Açıklama");

        ComboBox<TransactionType> cmbDebtType = new ComboBox<>(
                FXCollections.observableArrayList(TransactionType.DUE, TransactionType.EXTRA_FEE));
        cmbDebtType.setPromptText("Tür");
        cmbDebtType.setValue(TransactionType.DUE); // Varsayılan aidat

        Button btnAdd = new Button("Borç Yansıt");
        btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        btnAdd.setOnAction(e -> {
            try {
                String idText = txtResId.getText().trim();
                BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
                TransactionType type = cmbDebtType.getValue();
                String desc = txtDesc.getText().trim();

                boolean ok = false;

                
                if (idText.isEmpty()) {
                    // ID kutusu boşsa Toplu metodu çalıştır
                    ok = financeManager.addBulkDebtToAllResidents(amount, type, desc);
                    if (ok) showAlert("Başarılı", "Tüm site sakinlerine toplu borç başarıyla yansıtıldı.");
                } else {
                    // ID kutusu doluysa BİREYSEL metodu çalıştır
                    int resId = Integer.parseInt(idText);
                    ok = financeManager.addDebtToResident(resId, amount, type, desc);
                    if (ok) showAlert("Başarılı", "ID'si " + resId + " olan sakine özel borç yansıtıldı.");
                }

                if (ok) {
                    reportTable.setItems(FXCollections.observableArrayList(financeManager.getSiteGeneralReport()));
                    txtResId.clear();
                    txtAmount.clear();
                    txtDesc.clear();
                } else {
                    showAlert("Hata", "İşlem sırasında bir sorun oluştu. Geçersiz bir ID girmiş olabilirsiniz.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Hata", "Lütfen tutar ve ID (eğer giriyorsanız) kısımlarına sadece geçerli sayılar giriniz.");
            }
        });

        form.getChildren().addAll(txtResId, txtAmount, txtDesc, cmbDebtType, btnAdd);
        
        layout.getChildren().addAll(
                title, 
                new Label("Ödenen/Borç Aidat Listesi"), reportTable, 
                new Separator(),
                new Label("Borç Yansıtma (Tüm siteye yansıtmak için Sakin ID kısmını boş bırakın):"), form
        );
        
        return layout;
    }

    private VBox buildAdminParkingView() {
        VBox layout = new VBox(15);
        Label title = new Label("Otopark Yönetimi");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // --- ÜST: Otopark Doluluk Grafiği ---
        VBox graphBox = new VBox(5);
        Label lblGraphTitle = new Label("Otopark Doluluk Grafiği");
        lblGraphTitle.setStyle("-fx-font-weight: bold;");

        int currentCars = parkingManager.getCurrentOccupancy();
        ProgressBar occupancyBar = new ProgressBar((double) currentCars / 150);
        occupancyBar.setPrefWidth(Double.MAX_VALUE);
        occupancyBar.setStyle("-fx-accent: #34495e;");
        Label lblOccupancy = new Label(currentCars + " / 150 Araç İçeride");

        graphBox.getChildren().addAll(lblGraphTitle, occupancyBar, lblOccupancy);

        // --- ORTA: Kayıtlı Araç Tablosu ---
        TableView<ParkingManager.RegisteredVehicleInfo> tableVehicles = new TableView<>();
        tableVehicles.getColumns().addAll(
                createCol("Daire Bilgisi", "ownerInfo"),
                createCol("Kayıtlı Plakalar", "licensePlate"));
        tableVehicles.setItems(FXCollections.observableArrayList(parkingManager.getAllRegisteredVehiclesInfo()));
        tableVehicles.setPrefHeight(200);

        // --- FORMLAR: Kayıt ve Misafir ---
        HBox formRegistry = new HBox(10);
        formRegistry.setAlignment(Pos.CENTER_LEFT);
        TextField txtAptId = new TextField();
        txtAptId.setPromptText("Daire ID");
        txtAptId.setPrefWidth(100);
        TextField txtRegPlate = new TextField();
        txtRegPlate.setPromptText("Plaka");
        Button btnRegister = new Button("Kaydet");
        btnRegister.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        Button btnRefresh = new Button("Yenile");

        btnRegister.setOnAction(e -> {
            try {
                int aptId = Integer.parseInt(txtAptId.getText().trim());
                String plate = txtRegPlate.getText().trim().toUpperCase();
                if (parkingManager.registerVehicle(aptId, plate)) {
                    showAlert("Başarılı", "Araç kaydedildi.");
                    btnRefresh.fire();
                    txtAptId.clear();
                    txtRegPlate.clear();
                } else {
                    showAlert("Hata", "Kayıt başarısız! (Maksimum 3 araç sınırı veya hatalı ID)");
                }
            } catch (Exception ex) {
                showAlert("Hata", "Geçersiz giriş!");
            }
        });

        formRegistry.getChildren().addAll(new Label("Daire:"), txtAptId, new Label("Plaka:"), txtRegPlate, btnRegister,
                btnRefresh);

        // --- ALT: Misafir Araç Bölümü ---
        VBox guestBox = new VBox(10);
        guestBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");
        Label lblGuestTitle = new Label("Misafir Araç");
        lblGuestTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        HBox guestForm = new HBox(10);
        guestForm.setAlignment(Pos.CENTER_LEFT);
        TextField txtLogPlate = new TextField();
        txtLogPlate.setPromptText("Misafir Araç Bilgisi (Plaka)");
        txtLogPlate.setPrefWidth(200);

        Label lblSmartInfo = new Label(""); // Akıllı tanıma bilgisi
        lblSmartInfo.setStyle("-fx-text-fill: #2980b9; -fx-font-style: italic;");

        // Akıllı Plaka Tanıma (Text Değiştiğinde Çalışır)
        txtLogPlate.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 5) {
                String owner = parkingManager.findOwnerByPlate(newVal.toUpperCase());
                if (!owner.contains("Bilinmiyor")) {
                    lblSmartInfo.setText("Sakin Aracı: " + owner);
                } else {
                    lblSmartInfo.setText("Yabancı / Misafir Araç");
                }
            } else {
                lblSmartInfo.setText("");
            }
        });

        Button btnEnter = new Button("Araç Girişi");
        btnEnter.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        Button btnExit = new Button("Araç Çıkışı");
        btnExit.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        btnEnter.setOnAction(e -> {
            if (parkingManager.logGuestEntry(txtLogPlate.getText().toUpperCase(), 0)) {
                showAlert("Başarılı", "Giriş kaydedildi.");
                btnRefresh.fire();
            } else {
                showAlert("Hata", "Giriş yapılamadı! (Otopark dolu olabilir)");
            }
        });

        btnExit.setOnAction(e -> {
            if (parkingManager.logExit(txtLogPlate.getText().toUpperCase())) {
                showAlert("Başarılı", "Çıkış kaydedildi.");
                btnRefresh.fire();
            } else {
                showAlert("Hata", "Aktif giriş kaydı bulunamadı.");
            }
        });

        guestForm.getChildren().addAll(txtLogPlate, btnEnter, btnExit, lblSmartInfo);
        guestBox.getChildren().addAll(lblGuestTitle, guestForm);

        // --- LOGLAR: Otopark Geçmişi ---
        VBox logBox = new VBox(10);
        logBox.setStyle("-fx-padding: 15 0 0 0;");
        Label lblLogTitle = new Label("Otopark Giriş-Çıkış Kayıtları (Loglar)");
        lblLogTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TableView<VehicleLog> tableLogs = new TableView<>();
        tableLogs.getColumns().add(this.<VehicleLog, String>createCol("Plaka", "licensePlate"));
        tableLogs.getColumns().add(this.<VehicleLog, java.time.LocalDateTime>createCol("Giriş Zamanı", "entryTime"));
        tableLogs.getColumns().add(this.<VehicleLog, java.time.LocalDateTime>createCol("Çıkış Zamanı", "exitTime"));
        tableLogs.setItems(FXCollections.observableArrayList(parkingManager.getAllLogs()));
        tableLogs.setPrefHeight(250);

        logBox.getChildren().addAll(lblLogTitle, tableLogs);

        btnRefresh.setOnAction(e -> {
            int current = parkingManager.getCurrentOccupancy();
            occupancyBar.setProgress((double) current / 150);
            lblOccupancy.setText(current + " / 150 Araç İçeride");
            tableVehicles.setItems(FXCollections.observableArrayList(parkingManager.getAllRegisteredVehiclesInfo()));
            tableLogs.setItems(FXCollections.observableArrayList(parkingManager.getAllLogs()));
        });

        layout.getChildren().addAll(title, graphBox, tableVehicles, formRegistry, guestBox, logBox);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new javafx.geometry.Insets(10));
        scrollPane.setStyle("-fx-background-color: transparent;");

        return new VBox(scrollPane);
    }

    private void setupResidentMenu(VBox menu, StackPane content) {
        Button btnMyProfile = createMenuBtn("👤 Profilim");
        Button btnFin = createMenuBtn("💳 Borçlarım ve Ödeme");
        Button btnTick = createMenuBtn("⚠️ Arıza Bildir");
        Button btnComm = createMenuBtn("📢 Duyurular ve Anketler");
        Button btnOtopark = createMenuBtn("🚗 Otopark Durumu"); // Yeni Eklendi

        btnMyProfile.setOnAction(e -> content.getChildren().setAll(buildResidentProfileView()));
        btnFin.setOnAction(e -> content.getChildren().setAll(buildResidentFinanceView()));
        btnTick.setOnAction(e -> content.getChildren().setAll(buildResidentTicketView()));
        btnComm.setOnAction(e -> content.getChildren().setAll(buildResidentCommView()));
        btnOtopark.setOnAction(e -> content.getChildren().setAll(buildResidentParkingView())); // Yeni Eklendi

        menu.getChildren().addAll(btnMyProfile, btnFin, btnTick, btnComm, btnOtopark); // Yeni Buton eklendi
        content.getChildren().setAll(buildResidentProfileView());
    }

    private VBox buildResidentParkingView() {
        VBox layout = new VBox(20);
        layout.setPadding(new javafx.geometry.Insets(20));

        Label title = new Label("Otopark Durumu");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        int apartmentId = parkingManager.getApartmentIdByResidentId(currentUserId);
        List<com.sitemanagement.models.Vehicle> myVehicles = parkingManager.getVehiclesByApartment(apartmentId);
        String plates = myVehicles.stream().map(v -> v.getLicensePlate()).reduce((a, b) -> a + ", " + b)
                .orElse("Kayıtlı aracınız bulunmuyor.");
        Label lblMyCars = new Label("Kayıtlı Araçlarınız: " + plates);
        lblMyCars.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 5 0;");

        int currentCars = parkingManager.getCurrentOccupancy();
        double progress = (currentCars > 0) ? Math.max((double) currentCars / 150, 0.04) : 0;

        ProgressBar occupancyBar = new ProgressBar(progress);
        occupancyBar.setPrefWidth(400);
        occupancyBar.setStyle("-fx-accent: #2ecc71;");
        Label lblOccupancy = new Label("Otopark Doluluğu: " + currentCars + " / 150");
        lblOccupancy.setStyle("-fx-font-weight: bold;");

        Button btnRefresh = new Button("🔄 Yenile");
        btnRefresh.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRefresh.setOnAction(e -> {
            int current = parkingManager.getCurrentOccupancy();
            double p = (current > 0) ? Math.max((double) current / 150, 0.04) : 0;
            occupancyBar.setProgress(p);
            lblOccupancy.setText("Otopark Doluluğu: " + current + " / 150");
        });

        HBox occupancyBox = new HBox(15, occupancyBar, lblOccupancy, btnRefresh);
        occupancyBox.setAlignment(Pos.CENTER_LEFT);

        VBox guestSection = new VBox(10);
        Label lblGuestTitle = new Label("Ziyaretçi Araç Girişi");
        lblGuestTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblInfo = new Label(
                "Sadece misafir plakası giriniz. Sakin araçları güvenlik tarafından otomatik tanınır.");
        lblInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

        HBox guestForm = new HBox(10);
        TextField txtGuestPlate = new TextField();
        txtGuestPlate.setPromptText("Misafir Plakası");
        txtGuestPlate.setPrefWidth(200);

        Button btnGuestReg = new Button("Aracı Otoparka Al");
        btnGuestReg.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");

        btnGuestReg.setOnAction(e -> {
            String plate = txtGuestPlate.getText().trim().toUpperCase();
            if (plate.isEmpty()) {
                showAlert("Uyarı", "Plaka kısmı boş olamaz!");
                return;
            }
            if (parkingManager.isResident(plate)) {
                showAlert("Hata", "Bu bir sakin aracıdır! Lütfen sadece misafir araç girişi yapınız.");
                return;
            }
            if (parkingManager.logGuestEntry(plate, currentUserId)) {
                showAlert("Başarılı", "Misafir araç " + plate + " sisteme işlendi.");
                btnRefresh.fire();
                txtGuestPlate.clear();
            } else {
                showAlert("Hata", "Araç zaten içeride veya bir hata oluştu.");
            }
        });

        guestForm.getChildren().addAll(txtGuestPlate, btnGuestReg);
        guestSection.getChildren().addAll(lblGuestTitle, lblInfo, guestForm);

        layout.getChildren().addAll(title, lblMyCars, new Separator(), occupancyBox, guestSection);
        return layout;
    }

    private VBox buildResidentCommView() {
        VBox layout = new VBox(15);
        Label title = new Label("Site Duyuruları ve Anketler");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Announcement> table = new TableView<>();
        table.getColumns().addAll(createCol("Başlık", "title"), createCol("Tarih", "publishDate"));
        table.setItems(FXCollections.observableArrayList(commManager.getAllAnnouncements()));

        VBox pollBox = new VBox(10);
        List<Poll> activePolls = commManager.getActivePolls();
        for (Poll p : activePolls) {
            Label lblQ = new Label("Soru: " + p.getQuestion());
            HBox voteOptions = new HBox(10);

            Button btnYes = new Button("Evet");
            Button btnNo = new Button("Hayır");

            btnYes.setOnAction(e -> {
                if (commManager.castVote(p.getPollId(), currentUserId, "Evet"))
                    showAlert("Başarılı", "Oyunuz kaydedildi.");
                else
                    showAlert("Hata", "Zaten oy kullandınız veya bir sorun oluştu.");
            });
            btnNo.setOnAction(e -> {
                if (commManager.castVote(p.getPollId(), currentUserId, "Hayır"))
                    showAlert("Başarılı", "Oyunuz kaydedildi.");
                else
                    showAlert("Hata", "Zaten oy kullandınız veya bir sorun oluştu.");
            });
            voteOptions.getChildren().addAll(btnYes, btnNo);
            pollBox.getChildren().addAll(lblQ, voteOptions, new Separator());
        }

        layout.getChildren().addAll(title, table, new Label("Aktif Anketler:"), pollBox);
        return layout;
    }

    private VBox buildResidentProfileView() {
        VBox layout = new VBox(20);
        Label title = new Label("Daire ve Profil Bilgilerim");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        String fullName = (loggedInUser != null) ? loggedInUser.getFullName() : "Bilinmiyor";
        String phone = (loggedInUser != null) ? loggedInUser.getPhone() : "Bilinmiyor";

        grid.addRow(0, new Label("Ad Soyad:"), new Label(fullName));
        grid.addRow(1, new Label("Telefon:"), new Label(phone));
        grid.addRow(2, new Label("Sakin ID:"), new Label(String.valueOf(currentUserId)));
        
        int aptId = parkingManager.getApartmentIdByResidentId(currentUserId);
        grid.addRow(3, new Label("Daire ID:"), new Label(aptId == -1 ? "Atanmamış" : String.valueOf(aptId)));

        layout.getChildren().addAll(title, grid);
        return layout;
    }

    private VBox buildResidentFinanceView() {
        VBox layout = new VBox(15);
        Label title = new Label("Hesap Dökümüm ve Borç Ödeme");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BigDecimal currentDebt = financeManager.calculateTotalDebt(currentUserId);
        Label lblDebt = new Label("Güncel Toplam Borcunuz: " + currentDebt + " TL");
        lblDebt.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        TableView<Transaction> table = new TableView<>();
        table.getColumns().addAll(createCol("İşlem ID", "transactionId"), createCol("Tutar", "amount"),
                createCol("Açıklama", "description"));
        table.setItems(FXCollections.observableArrayList(financeManager.getResidentLedger(currentUserId)));

        HBox paymentForm = new HBox(10);
        TextField txtPayAmount = new TextField();
        txtPayAmount.setPromptText("Ödenecek Tutar");
        Button btnPay = new Button("Borç Öde");
        btnPay.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

        btnPay.setOnAction(e -> {
            try {
                BigDecimal amount = new BigDecimal(txtPayAmount.getText());
                if (financeManager.logManualPayment(currentUserId, amount, "Sakin Uygulama Üzerinden Ödeme Yaptı")) {
                    showAlert("Başarılı", "Ödeme başarıyla alındı. Borcunuzdan düşüldü.");
                    table.setItems(FXCollections.observableArrayList(financeManager.getResidentLedger(currentUserId)));
                    lblDebt.setText(
                            "Güncel Toplam Borcunuz: " + financeManager.calculateTotalDebt(currentUserId) + " TL");
                    txtPayAmount.clear();
                } else {
                    showAlert("Hata", "Ödeme başarısız! \nOlası nedenler: Mevcut borcunuzdan ("
                            + financeManager.calculateTotalDebt(currentUserId) + " TL) daha fazla ödeme yapamazsınız.");
                }
            } catch (Exception ex) {
                showAlert("Hata", "Lütfen geçerli bir tutar girin.");
            }
        });
        paymentForm.getChildren().addAll(txtPayAmount, btnPay);

        layout.getChildren().addAll(title, lblDebt, table, new Label("Hızlı Ödeme İşlemi:"), paymentForm);
        return layout;
    }

    private VBox buildResidentTicketView() {
        VBox layout = new VBox(15);
        Label title = new Label("Arıza Bildirimi ve Geçmişim");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<MaintenanceTicket> table = new TableView<>();
        table.getColumns().addAll(createCol("ID", "ticketId"), createCol("Başlık", "title"),
                createCol("Durum", "status"));
        table.setItems(FXCollections.observableArrayList(ticketManager.getTicketsByResident(currentUserId)));

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Arıza Başlığı");
        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Detaylı Açıklama...");
        Button btnSubmit = new Button("Talebi Gönder");

        btnSubmit.setOnAction(e -> {
            if (ticketManager.createTicket(currentUserId, txtTitle.getText(), txtDesc.getText())) {
                showAlert("Başarılı", "Arıza talebiniz yönetime iletildi.");
                txtTitle.clear();
                txtDesc.clear();
                table.setItems(FXCollections.observableArrayList(ticketManager.getTicketsByResident(currentUserId)));
            }
        });

        layout.getChildren().addAll(title, table, new Label("Yeni Arıza:"), txtTitle, txtDesc, btnSubmit);
        return layout;
    }

    private Button createMenuBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: center-left; -fx-cursor: hand;");
        return b;
    }

    private <S, T> TableColumn<S, T> createCol(String title, String prop) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<S, T>(prop));
        return col;
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setContentText(c);
        a.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}