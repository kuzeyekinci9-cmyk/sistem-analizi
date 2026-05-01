package com.sitemanagement.main;

import com.sitemanagement.enums.Role;
import com.sitemanagement.enums.TicketStatus;
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

        TextField txtUsername = new TextField(); txtUsername.setPromptText("Telefon Numarası");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Şifre");

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
                    currentUserId = 0; showDashboard(Role.ADMIN); return;
                }
                Resident user = residentManager.authenticateUser(phone, pass, selectedRole);
                if (user != null) {
                    loggedInUser = user; currentUserId = user.getId();
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

        if (role == Role.ADMIN) setupAdminMenu(sideMenu, contentArea);
        else setupResidentMenu(sideMenu, contentArea);

        Button btnLogout = new Button("Çıkış Yap");
        btnLogout.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> { loggedInUser = null; showLoginScreen(); });
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
            createCol("Dolu Mu?", "occupied"), // EKLENDİ: Dairenin dolu olup olmadığını görmek için
            createCol("Oturan Kişi", "residentName") // DÜZELTİLDİ: Artık tekil olarak sadece sahibini (muhatabı) gösteriyor
        );
        tableApt.setItems(FXCollections.observableArrayList(residentManager.getAllApartments()));
        tableApt.setPrefHeight(150);

        HBox formApt = new HBox(10);
        TextField txtBlock = new TextField(); txtBlock.setPromptText("Blok (Örn: A)");
        TextField txtFloor = new TextField(); txtFloor.setPromptText("Kat (Örn: 2)");
        TextField txtDoor = new TextField(); txtDoor.setPromptText("Kapı No (Örn: 5)");
        Button btnSaveApt = new Button("Yeni Daire Oluştur");
        btnSaveApt.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        btnSaveApt.setOnAction(e -> {
            try {
                if(residentManager.createApartment(txtBlock.getText(), Integer.parseInt(txtFloor.getText()), Integer.parseInt(txtDoor.getText()))) {
                    tableApt.setItems(FXCollections.observableArrayList(residentManager.getAllApartments()));
                    showAlert("Başarılı", "Daire başarıyla oluşturuldu.");
                    txtBlock.clear(); txtFloor.clear(); txtDoor.clear();
                }
            } catch(Exception ex) {
                showAlert("Hata", "Lütfen kat ve kapı numarasına sadece sayı giriniz.");
            }
        });
        formApt.getChildren().addAll(txtBlock, txtFloor, txtDoor, btnSaveApt);

        Label lblRes = new Label("Site Sakinleri");
        lblRes.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        TableView<Resident> tableRes = new TableView<>();
        tableRes.getColumns().addAll(createCol("ID", "id"), createCol("Ad Soyad", "fullName"), createCol("Telefon", "phone"));
        tableRes.setItems(FXCollections.observableArrayList(residentManager.getAllResidents()));
        tableRes.setPrefHeight(150);

        // --- YENİ EKLENEN KISIM: ATAMA VE SİLME BUTONLARI ---
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

        // YENİ BUTON: Seçili daireye seçili sakini atar
        Button btnAssign = new Button("Seçili Sakini Seçili Daireye Ata");
        btnAssign.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAssign.setOnAction(e -> {
            Resident selectedRes = tableRes.getSelectionModel().getSelectedItem();
            Apartment selectedApt = tableApt.getSelectionModel().getSelectedItem();
            
            if (selectedRes != null && selectedApt != null) {
                // "Zaten dolu" engelini KALDIRDIK. Artık aynı daireye birden fazla kişi atanabilir.
                
                if (residentManager.assignToApartment(selectedRes.getId(), selectedApt.getId())) {
                    showAlert("Başarılı", selectedRes.getFullName() + " isimli sakin " + 
                              selectedApt.getBlockName() + " Blok, No: " + selectedApt.getDoorNumber() + " adresine atandı.");
                    tableApt.setItems(FXCollections.observableArrayList(residentManager.getAllApartments()));
                } else {
                    showAlert("Hata", "Atama işlemi başarısız oldu.");
                }
            } else {
                showAlert("Bilgi", "Lütfen atama yapmak için üst tablodan bir 'Daire' ve alt tablodan bir 'Sakin' seçin.");
            }
        });

        actionBox.getChildren().addAll(btnDelete, btnAssign);
        // ----------------------------------------------------

        HBox formRes = new HBox(10);
        TextField txtName = new TextField(); txtName.setPromptText("Ad Soyad");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Telefon");
        TextField txtPass = new TextField(); txtPass.setPromptText("Şifre");
        Button btnSaveRes = new Button("Sakin Kaydet");
        btnSaveRes.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnSaveRes.setOnAction(e -> {
            if (residentManager.registerResident(new Resident(0, txtName.getText(), txtPhone.getText(), txtPass.getText(), Role.RESIDENT))) {
                tableRes.setItems(FXCollections.observableArrayList(residentManager.getAllResidents()));
                showAlert("Başarılı", "Sakin kaydedildi.");
                txtName.clear(); txtPhone.clear(); txtPass.clear(); // Temizleme eklendi
            }
        });
        formRes.getChildren().addAll(txtName, txtPhone, txtPass, btnSaveRes);

        // Arayüze actionBox'ı da dahil ediyoruz
        layout.getChildren().addAll(
            title, 
            lblApt, tableApt, formApt, 
            new Separator(), 
            lblRes, tableRes, formRes, actionBox 
        );
        
        return layout;
    }

    private VBox buildAdminTicketView() {
        VBox layout = new VBox(15);
        Label title = new Label("Arıza Talepleri ve Personel Atama");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<MaintenanceTicket> table = new TableView<>();
        table.getColumns().addAll(createCol("ID", "ticketId"), createCol("Başlık", "title"), createCol("Durum", "status"));
        table.setItems(FXCollections.observableArrayList(ticketManager.getTicketsByStatus(TicketStatus.OPEN)));

        HBox form = new HBox(10);
        ComboBox<TicketStatus> cmbStatus = new ComboBox<>(FXCollections.observableArrayList(TicketStatus.values()));
        cmbStatus.setPromptText("Yeni Durum");
        TextField txtStaffId = new TextField(); txtStaffId.setPromptText("Personel ID (Atama İçin)");
        Button btnUpdate = new Button("Durumu Güncelle / Ata");
        btnUpdate.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        
        btnUpdate.setOnAction(e -> {
            MaintenanceTicket selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if(!txtStaffId.getText().isEmpty()) {
                     ticketManager.assignTicketToStaff(selected.getTicketId(), Integer.parseInt(txtStaffId.getText()));
                     showAlert("Başarılı", "Personel atandı ve durum güncellendi.");
                } else if (cmbStatus.getValue() != null) {
                     ticketManager.updateTicketStatus(selected.getTicketId(), cmbStatus.getValue());
                     showAlert("Başarılı", "Arıza durumu güncellendi.");
                }
                table.setItems(FXCollections.observableArrayList(ticketManager.getTicketsByStatus(TicketStatus.OPEN)));
            } else {
                showAlert("Uyarı", "Lütfen tablodan bir arıza seçin.");
            }
        });

        form.getChildren().addAll(cmbStatus, txtStaffId, btnUpdate);
        layout.getChildren().addAll(title, table, new Label("Durum Değiştir / Personel Ata:"), form);
        return layout;
    }

    private VBox buildAdminCommView() {
        VBox layout = new VBox(15);
        Label title = new Label("İletişim ve Doküman Paylaşımı");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox formAnnounce = new HBox(10);
        TextField txtTitle = new TextField(); txtTitle.setPromptText("Duyuru Başlığı");
        TextField txtContent = new TextField(); txtContent.setPromptText("İçerik...");
        Button btnPublish = new Button("Duyuru Yayınla");
        btnPublish.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;");
        btnPublish.setOnAction(e -> {
            if (commManager.publishAnnouncement(txtTitle.getText(), txtContent.getText())) {
                showAlert("Başarılı", "Duyuru yayınlandı.");
                txtTitle.clear(); txtContent.clear();
            }
        });
        formAnnounce.getChildren().addAll(txtTitle, txtContent, btnPublish);

        HBox formPoll = new HBox(10);
        TextField txtPollQ = new TextField(); txtPollQ.setPromptText("Anket Sorusu");
        Button btnPoll = new Button("Anket Başlat");
        btnPoll.setOnAction(e -> {
            String[] options = {"Evet", "Hayır"};
            if(commManager.createPoll(txtPollQ.getText(), options)) {
                showAlert("Başarılı", "Anket sisteme eklendi.");
                txtPollQ.clear();
            }
        });
        formPoll.getChildren().addAll(txtPollQ, btnPoll);

        layout.getChildren().addAll(title, new Label("Yeni Duyuru:"), formAnnounce, new Separator(), new Label("Yeni Anket (Evet/Hayır):"), formPoll);
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
            createCol("Açıklama", "description")
        );
        reportTable.setItems(FXCollections.observableArrayList(financeManager.getSiteGeneralReport()));

        HBox form = new HBox(10);
        TextField txtResId = new TextField(); txtResId.setPromptText("Sakin ID");
        TextField txtAmount = new TextField(); txtAmount.setPromptText("Tutar (TL)");
        TextField txtDesc = new TextField(); txtDesc.setPromptText("Açıklama");
        Button btnAdd = new Button("Borç Yansıt");
        
        btnAdd.setOnAction(e -> {
            boolean ok = financeManager.addDebtToResident(Integer.parseInt(txtResId.getText()), new BigDecimal(txtAmount.getText()), txtDesc.getText());
            if(ok) {
                showAlert("Başarılı", "Borç başarıyla yansıtıldı.");
                reportTable.setItems(FXCollections.observableArrayList(financeManager.getSiteGeneralReport()));
            }
        });

        form.getChildren().addAll(txtResId, txtAmount, txtDesc, btnAdd);
        layout.getChildren().addAll(title, new Label("Ödenen/Borç Aidat Listesi"), reportTable, new Separator(), new Label("Toplu Aidat Yansıtma:"), form);
        return layout;
    }

    private VBox buildAdminParkingView() {
        VBox layout = new VBox(15);
        Label title = new Label("Otopark Yönetimi (Admin)");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        int currentCars = parkingManager.getCurrentOccupancy();
        ProgressBar occupancyBar = new ProgressBar((double) currentCars / 150);
        occupancyBar.setPrefWidth(400);
        occupancyBar.setStyle("-fx-accent: #3498db;");
        Label lblOccupancy = new Label("Anlık Otopark Doluluğu: " + currentCars + " / 150");
        lblOccupancy.setStyle("-fx-font-weight: bold;");
        
        TableView<ParkingManager.RegisteredVehicleInfo> tableVehicles = new TableView<>();
        tableVehicles.getColumns().addAll(
            createCol("Daire Bilgisi", "ownerInfo"),
            createCol("Kayıtlı Plakalar", "licensePlate")
        );
        tableVehicles.setItems(FXCollections.observableArrayList(parkingManager.getAllRegisteredVehiclesInfo()));
        tableVehicles.setPrefHeight(200);

        HBox formRegistry = new HBox(10);
        TextField txtAptId = new TextField(); txtAptId.setPromptText("Daire ID");
        TextField txtRegPlate = new TextField(); txtRegPlate.setPromptText("Sakin Plakası");
        Button btnRegister = new Button("Aracı Kaydet (Max 3)");
        btnRegister.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Button btnRefresh = new Button("Yenile");
        btnRefresh.setStyle("-fx-background-color: #f1c40f; -fx-font-weight: bold;");
        btnRefresh.setOnAction(e -> {
            int current = parkingManager.getCurrentOccupancy();
            occupancyBar.setProgress((double) current / 150);
            lblOccupancy.setText("Anlık Otopark Doluluğu: " + current + " / 150");
            tableVehicles.setItems(FXCollections.observableArrayList(parkingManager.getAllRegisteredVehiclesInfo()));
        });

        btnRegister.setOnAction(e -> {
            try {
                int aptId = Integer.parseInt(txtAptId.getText().trim());
                String plate = txtRegPlate.getText().trim();
                
                if (plate.isEmpty()) { showAlert("Uyarı", "Plaka boş olamaz."); return; }

                boolean isSuccess = parkingManager.registerVehicle(aptId, plate);
                
                if (isSuccess) {
                    showAlert("Başarılı", "Araç daireye başarıyla tanımlandı.");
                    txtAptId.clear(); txtRegPlate.clear();
                    btnRefresh.fire(); // Anlık listeyi yenile
                } else {
                    showAlert("Hata", "Kayıt başarısız! \nNedenler: \n- Bu daireye zaten maksimum (3) araç kayıtlı. \n- Daire ID yanlış.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Uyarı", "Lütfen Daire ID kısmına sadece rakam giriniz.");
            }
        });

        HBox formGuest = new HBox(10);
        TextField txtLogPlate = new TextField(); txtLogPlate.setPromptText("Misafir Plakası");
        Button btnEnter = new Button("Araç Giriş"); 
        Button btnExit = new Button("Araç Çıkış");
        
        btnEnter.setOnAction(e -> {
            if(parkingManager.getCurrentOccupancy() >= 150) {
                showAlert("Uyarı", "Otopark Dolu (150/150)!"); return;
            }
            if(parkingManager.logGuestEntry(txtLogPlate.getText(), 0)) {
                showAlert("Giriş Başarılı", "Araç içeri alındı.");
                btnRefresh.fire(); // Anlık doluluk grafiğini yenile
            }
        });

        btnExit.setOnAction(e -> {
            if(parkingManager.logExit(txtLogPlate.getText())) {
                showAlert("Çıkış Başarılı", "Araç çıkışı kaydedildi.");
                btnRefresh.fire(); // Anlık doluluk grafiğini yenile
            } else {
                showAlert("Hata", "Bu plaka kayıtlı değil.");
            }
        });
        
        formGuest.getChildren().addAll(txtLogPlate, btnEnter, btnExit);
        formRegistry.getChildren().addAll(txtAptId, txtRegPlate, btnRegister, btnRefresh);

        HBox occupancyBox = new HBox(15, lblOccupancy, occupancyBar);
        occupancyBox.setAlignment(Pos.CENTER_LEFT);

        layout.getChildren().addAll(
            title, 
            occupancyBox, 
            new Separator(), 
            new Label("Sisteme Kayıtlı Araçlar (Daireye Özel Max 3):"), tableVehicles, formRegistry, 
            new Separator(), 
            new Label("Manuel Giriş / Çıkış (Admin Log):"), formGuest
        );
        return layout;
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
        VBox layout = new VBox(15);
        Label title = new Label("Otopark Durumu & Misafir Araç");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Otopark Anlık Doluluk - Sakin izleyebilsin
        int currentCars = parkingManager.getCurrentOccupancy();
        ProgressBar occupancyBar = new ProgressBar((double) currentCars / 150);
        occupancyBar.setPrefWidth(400);
        occupancyBar.setStyle("-fx-accent: #2ecc71;");
        Label lblOccupancy = new Label("Anlık Otopark Doluluğu: " + currentCars + " / 150");
        lblOccupancy.setStyle("-fx-font-weight: bold;");

        Button btnRefresh = new Button("Anlık Doluluğu Yenile");
        btnRefresh.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRefresh.setOnAction(e -> {
            int current = parkingManager.getCurrentOccupancy();
            occupancyBar.setProgress((double) current / 150);
            lblOccupancy.setText("Anlık Otopark Doluluğu: " + current + " / 150");
        });

        HBox occupancyBox = new HBox(15, lblOccupancy, occupancyBar, btnRefresh);
        occupancyBox.setAlignment(Pos.CENTER_LEFT);

        // Kendi Araçları
        VBox myCarsBox = new VBox(10);
        int myAptId = 0; // Login olan sakinin dairesini bulup plakasını çekmemiz lazım.
        // Bu yüzden basit taslak liste için:
        // Admin panelinden id ile atandığı için residentManager veya DB'den apt bulunaibilir. Ancak şimdilik araçları görmek kısmı detaylı kod istemiyor gibi.
        // Şimdilik sakin ID'sinden apartman ID bulmak için sorgu atabiliriz. (DB tarafını düşünürsek)

        // Misafir Araç Kaydet
        HBox guestForm = new HBox(10);
        TextField txtGuestPlate = new TextField(); txtGuestPlate.setPromptText("Misafir Plakası");
        Button btnGuestReg = new Button("Misafir Aracı Otoparka Al");
        btnGuestReg.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");

        btnGuestReg.setOnAction(e -> {
            String plate = txtGuestPlate.getText().trim();
            if(plate.isEmpty()) { showAlert("Uyarı", "Plaka kısmı boş olamaz!"); return; }
            int occupancy = parkingManager.getCurrentOccupancy();
            if(occupancy >= 150) {
                showAlert("Otopark Dolu", "Şu anda otopark tam kapasitededir (150/150). \nMisafir aracı kabul edilemiyor!");
            } else {
                if(parkingManager.logGuestEntry(plate, currentUserId)) { // currentUserId referencing the apartment. Or just 0
                    showAlert("Başarılı", "Misafir araç " + plate + " sisteme işlendi ve kabul edildi.");
                    btnRefresh.fire(); // Panel yenile
                    txtGuestPlate.clear();
                } else {
                    showAlert("Hata", "Araç eklenirken bir hata oluştu.");
                }
            }
        });
        guestForm.getChildren().addAll(txtGuestPlate, btnGuestReg);

        layout.getChildren().addAll(
            title,
            occupancyBox,
            new Separator(),
            new Label("Misafir Araç İşlemleri:"),
            new Label("Eğer otopark dolu değilse, ziyaretçinize plaka üzerinden sistem girişi sağlayabilirsiniz."),
            guestForm
        );
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
        for(Poll p : activePolls) {
            Label lblQ = new Label("Soru: " + p.getQuestion());
            HBox voteOptions = new HBox(10);
            
            Button btnYes = new Button("Evet");
            Button btnNo = new Button("Hayır");
            
            // DÜZELTİLDİ: ID yerne metin tabanlı (String) oy veriliyor. Bu sayede hata ortadan kalktı!
            btnYes.setOnAction(e -> {
                if(commManager.castVote(p.getPollId(), currentUserId, "Evet")) showAlert("Başarılı", "Oyunuz kaydedildi.");
                else showAlert("Hata", "Zaten oy kullandınız veya bir sorun oluştu.");
            });
            btnNo.setOnAction(e -> {
                if(commManager.castVote(p.getPollId(), currentUserId, "Hayır")) showAlert("Başarılı", "Oyunuz kaydedildi.");
                else showAlert("Hata", "Zaten oy kullandınız veya bir sorun oluştu.");
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
        grid.setHgap(20); grid.setVgap(15);
        
        String fullName = (loggedInUser != null) ? loggedInUser.getFullName() : "Bilinmiyor";
        String phone = (loggedInUser != null) ? loggedInUser.getPhone() : "Bilinmiyor";
        
        grid.addRow(0, new Label("Ad Soyad:"), new Label(fullName));
        grid.addRow(1, new Label("Telefon:"), new Label(phone));
        grid.addRow(2, new Label("Sistem ID:"), new Label(String.valueOf(currentUserId)));
        
        layout.getChildren().addAll(title, grid);
        return layout;
    }

    private VBox buildResidentFinanceView() {
        VBox layout = new VBox(15);
        Label title = new Label("Hesap Dökümüm ve Borç Ödeme");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // YENİ EKLENDİ: Kalan borcu anlık göstermek için
        BigDecimal currentDebt = financeManager.calculateTotalDebt(currentUserId);
        Label lblDebt = new Label("Güncel Toplam Borcunuz: " + currentDebt + " TL");
        lblDebt.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        TableView<Transaction> table = new TableView<>();
        table.getColumns().addAll(createCol("İşlem ID", "transactionId"), createCol("Tutar", "amount"), createCol("Açıklama", "description"));
        table.setItems(FXCollections.observableArrayList(financeManager.getResidentLedger(currentUserId)));

        HBox paymentForm = new HBox(10);
        TextField txtPayAmount = new TextField(); txtPayAmount.setPromptText("Ödenecek Tutar");
        Button btnPay = new Button("Borç Öde");
        btnPay.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        btnPay.setOnAction(e -> {
            try {
                BigDecimal amount = new BigDecimal(txtPayAmount.getText());
                if(financeManager.logManualPayment(currentUserId, amount, "Sakin Uygulama Üzerinden Ödeme Yaptı")) {
                    showAlert("Başarılı", "Ödeme başarıyla alındı. Borcunuzdan düşüldü.");
                    table.setItems(FXCollections.observableArrayList(financeManager.getResidentLedger(currentUserId))); 
                    lblDebt.setText("Güncel Toplam Borcunuz: " + financeManager.calculateTotalDebt(currentUserId) + " TL");
                    txtPayAmount.clear();
                } else {
                    showAlert("Hata", "Ödeme başarısız! \nOlası nedenler: Mevcut borcunuzdan (" + financeManager.calculateTotalDebt(currentUserId) + " TL) daha fazla ödeme yapamazsınız.");
                }
            } catch(Exception ex) {
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
        table.getColumns().addAll(createCol("ID", "ticketId"), createCol("Başlık", "title"), createCol("Durum", "status"));
        table.setItems(FXCollections.observableArrayList(ticketManager.getTicketsByResident(currentUserId)));

        TextField txtTitle = new TextField(); txtTitle.setPromptText("Arıza Başlığı");
        TextArea txtDesc = new TextArea(); txtDesc.setPromptText("Detaylı Açıklama...");
        Button btnSubmit = new Button("Talebi Gönder");
        
        btnSubmit.setOnAction(e -> {
            if(ticketManager.createTicket(currentUserId, txtTitle.getText(), txtDesc.getText())) {
                showAlert("Başarılı", "Arıza talebiniz yönetime iletildi.");
                txtTitle.clear(); txtDesc.clear();
                table.setItems(FXCollections.observableArrayList(ticketManager.getTicketsByResident(currentUserId)));
            }
        });

        layout.getChildren().addAll(title, table, new Label("Yeni Arıza:"), txtTitle, txtDesc, btnSubmit);
        return layout;
    }

    private Button createMenuBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: center-left; -fx-cursor: hand;");
        return b;
    }

    private <S, T> TableColumn<S, T> createCol(String title, String prop) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        return col;
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setContentText(c); a.show();
    }

    public static void main(String[] args) { launch(args); }
}