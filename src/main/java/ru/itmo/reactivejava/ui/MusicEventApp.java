package ru.itmo.reactivejava.ui;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.domain.member.Member;
import ru.itmo.reactivejava.domain.member.MemberType;
import ru.itmo.reactivejava.shared.user.User;
import ru.itmo.reactivejava.ui.service.ReactiveEventService;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class MusicEventApp extends Application {

    private final ReactiveEventService eventService = new ReactiveEventService();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private ListView<Event> eventListView;
    private ObservableList<Event> eventItems;
    private ListView<String> notificationList;
    private ObservableList<String> notifications;
    private Label statsLabel;
    private Label selectedEventMembersLabel;
    private ListView<String> membersListView;
    private ComboBox<String> genreFilter;
    private MusicCompetitionGenre currentFilterGenre = null;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Музыкальные события");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createHeader());
        root.setCenter(createEventsPanel());
        root.setRight(createDetailsPanel());
        root.setBottom(createNotificationsPanel());

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        setupReactiveBindings();
        eventService.generateInitialEvents(5);
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Организация музыкальных мероприятий");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        statsLabel = new Label("События: 0 | Участников: 0");
        statsLabel.setFont(Font.font("System", 14));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, statsLabel);
        return header;
    }

    private VBox createEventsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);

        Button createEventBtn = new Button("Создать событие");
        createEventBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        createEventBtn.setOnAction(e -> showCreateEventDialog());

        Button generateBtn = new Button("Сгенерировать 3 события");
        generateBtn.setOnAction(e -> {
            for (int i = 0; i < 3; i++) {
                eventService.createEvent(null, null, null).subscribe();
            }
        });

        Label filterLabel = new Label("Фильтр по жанру:");
        genreFilter = new ComboBox<>();
        genreFilter.getItems().add("Все жанры");
        for (MusicCompetitionGenre genre : MusicCompetitionGenre.values()) {
            genreFilter.getItems().add(genre.toString());
        }
        genreFilter.setValue("Все жанры");
        genreFilter.setOnAction(e -> applyFilter());

        controls.getChildren().addAll(createEventBtn, generateBtn, new Separator(), filterLabel, genreFilter);

        Label eventsLabel = new Label("Список событий:");
        eventsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        eventItems = FXCollections.observableArrayList();
        eventListView = new ListView<>(eventItems);
        eventListView.setCellFactory(param -> new EventListCell());
        eventListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateSelectedEventDetails(newVal));

        eventListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Event selected = eventListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showEventDetailsDialog(selected);
                }
            }
        });

        VBox.setVgrow(eventListView, Priority.ALWAYS);
        panel.getChildren().addAll(controls, eventsLabel, eventListView);
        return panel;
    }

    private VBox createDetailsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");

        Label detailsTitle = new Label("Детали события");
        detailsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        selectedEventMembersLabel = new Label("Выберите событие для просмотра");
        selectedEventMembersLabel.setWrapText(true);

        Label membersTitle = new Label("Участники:");
        membersTitle.setFont(Font.font("System", FontWeight.BOLD, 12));

        membersListView = new ListView<>();
        membersListView.setPrefHeight(300);

        Button joinBtn = new Button("Присоединиться к событию");
        joinBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        joinBtn.setDisable(true);
        joinBtn.setOnAction(e -> showJoinEventDialog());

        eventListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> joinBtn.setDisable(newVal == null));

        panel.getChildren().addAll(detailsTitle, selectedEventMembersLabel, membersTitle, membersListView, joinBtn);
        return panel;
    }

    private VBox createNotificationsPanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10));
        panel.setPrefHeight(200);
        panel.setStyle("-fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");

        Label notifTitle = new Label("Уведомления:");
        notifTitle.setFont(Font.font("System", FontWeight.BOLD, 12));

        notifications = FXCollections.observableArrayList();
        notificationList = new ListView<>(notifications);
        notificationList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Создано")) {
                        setStyle("-fx-text-fill: green;");
                    } else if (item.contains("присоединился")) {
                        setStyle("-fx-text-fill: blue;");
                    }
                }
            }
        });
        VBox.setVgrow(notificationList, Priority.ALWAYS);

        Button clearBtn = new Button("Очистить");
        clearBtn.setOnAction(e -> notifications.clear());

        panel.getChildren().addAll(notifTitle, notificationList, clearBtn);
        return panel;
    }

    private void setupReactiveBindings() {
        var eventsDisposable = eventService.getEventsObservable()
                .observeOn(Schedulers.computation())
                .subscribe(events -> Platform.runLater(() -> {
                    Event selected = eventListView.getSelectionModel().getSelectedItem();

                    if (currentFilterGenre != null) {
                        var filtered = events.stream()
                                .filter(e -> e.getDescription().genre() == currentFilterGenre)
                                .toList();
                        eventItems.setAll(filtered);
                    } else {
                        eventItems.setAll(events);
                    }

                    if (selected != null) {
                        eventItems.stream()
                                .filter(e -> e.getId() == selected.getId())
                                .findFirst()
                                .ifPresent(e -> eventListView.getSelectionModel().select(e));
                    }
                    updateStats(events);
                }));
        disposables.add(eventsDisposable);

        var notifDisposable = eventService.getNotifications()
                .observeOn(Schedulers.io())
                .subscribe(notification -> Platform.runLater(() -> {
                    String time = java.time.LocalTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                    notifications.add(0, "[" + time + "] " + notification.message());
                    if (notifications.size() > 50) {
                        notifications.remove(notifications.size() - 1);
                    }
                }));
        disposables.add(notifDisposable);

        var memberDisposable = eventService.getMemberUpdates()
                .throttleLast(200, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(update -> Platform.runLater(() -> {
                    Event selected = eventListView.getSelectionModel().getSelectedItem();
                    if (selected != null && selected.getId() == update.event().getId()) {
                        updateSelectedEventDetails(update.event());
                    }
                }));
        disposables.add(memberDisposable);
    }

    private void updateStats(java.util.List<Event> events) {
        int totalMembers = events.stream().mapToInt(e -> e.getMembers().size()).sum();
        statsLabel.setText(String.format("События: %d | Участников: %d", events.size(), totalMembers));
    }

    private void updateSelectedEventDetails(Event event) {
        if (event == null) {
            selectedEventMembersLabel.setText("Выберите событие для просмотра");
            membersListView.getItems().clear();
            return;
        }

        String details = String.format("""
                Название: %s
                Дата: %s
                Жанр: %s
                Формат: %s
                Место: %s
                Вместимость: %d
                Участников: %d
                """,
                event.getName(),
                event.getDateTime().format(DATE_FORMATTER),
                event.getDescription().genre(),
                event.getDescription().format(),
                event.getPlacement().getPlacement(),
                event.getPlacement().getCapacity(),
                event.getMembers().size()
        );
        selectedEventMembersLabel.setText(details);

        ObservableList<String> memberStrings = FXCollections.observableArrayList();
        for (Member member : event.getMembers()) {
            String memberInfo = String.format("%s %s - %s",
                    member.getUser().getFirstName(),
                    member.getUser().getLastName(),
                    getMemberTypeName(member.getMemberType()));
            memberStrings.add(memberInfo);
        }
        membersListView.setItems(memberStrings);
    }

    private void applyFilter() {
        String selected = genreFilter.getValue();
        if ("Все жанры".equals(selected)) {
            currentFilterGenre = null;
        } else {
            for (MusicCompetitionGenre g : MusicCompetitionGenre.values()) {
                if (g.toString().equals(selected)) {
                    currentFilterGenre = g;
                    break;
                }
            }
        }

        var disposable = eventService.getEventsObservable()
                .take(1)
                .subscribe(events -> Platform.runLater(() -> {
                    if (currentFilterGenre != null) {
                        var filtered = events.stream()
                                .filter(e -> e.getDescription().genre() == currentFilterGenre)
                                .toList();
                        eventItems.setAll(filtered);
                    } else {
                        eventItems.setAll(events);
                    }
                }));
        disposables.add(disposable);
    }

    private void showCreateEventDialog() {
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Создать новое событие");
        dialog.setHeaderText("Введите данные события и ваши данные как организатора");

        ButtonType createButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Название события");

        ComboBox<MusicCompetitionGenre> genreBox = new ComboBox<>();
        genreBox.getItems().addAll(MusicCompetitionGenre.values());
        genreBox.setValue(MusicCompetitionGenre.ROCK_BATTLE);

        Separator separator = new Separator();

        Label orgLabel = new Label("Данные организатора:");
        orgLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Ваше имя");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Ваша фамилия");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Жанр:"), 0, 1);
        grid.add(genreBox, 1, 1);
        grid.add(separator, 0, 2, 2, 1);
        grid.add(orgLabel, 0, 3, 2, 1);
        grid.add(new Label("Имя:"), 0, 4);
        grid.add(firstNameField, 1, 4);
        grid.add(new Label("Фамилия:"), 0, 5);
        grid.add(lastNameField, 1, 5);
        grid.add(new Label("Email:"), 0, 6);
        grid.add(emailField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String firstName = firstNameField.getText().isEmpty() ? "Организатор" : firstNameField.getText();
                String lastName = lastNameField.getText().isEmpty() ? "" : lastNameField.getText();
                String email = emailField.getText().isEmpty() ? firstName.toLowerCase() + "@example.com" : emailField.getText();

                User organizer = new User(
                        (int) System.currentTimeMillis(),
                        firstName,
                        lastName,
                        30,
                        email
                );

                eventService.createEvent(
                        nameField.getText().isEmpty() ? null : nameField.getText(),
                        genreBox.getValue(),
                        organizer
                ).subscribe();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showJoinEventDialog() {
        Event selected = eventListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Присоединиться к событию");
        dialog.setHeaderText("Присоединиться к: " + selected.getName());

        ButtonType joinButtonType = new ButtonType("Присоединиться", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(joinButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Имя");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Фамилия");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Гость", "Исполнитель", "Партнер");
        roleBox.setValue("Гость");

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Фамилия:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Роль:"), 0, 2);
        grid.add(roleBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == joinButtonType) {
                String firstName = firstNameField.getText().isEmpty() ? "Аноним" : firstNameField.getText();
                String lastName = lastNameField.getText().isEmpty() ? "" : lastNameField.getText();

                User user = new User(
                        (int) System.currentTimeMillis(),
                        firstName,
                        lastName,
                        25,
                        firstName.toLowerCase() + "@example.com"
                );

                MemberType type = switch (roleBox.getValue()) {
                    case "Исполнитель" -> MemberType.PERFORMER;
                    case "Партнер" -> MemberType.PARTNER;
                    default -> MemberType.GUEST;
                };

                eventService.joinEvent(selected, user, type).subscribe();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showEventDetailsDialog(Event event) {
        Stage detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.setTitle(event.getName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label(event.getName());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(8);

        int row = 0;
        addInfoRow(infoGrid, row++, "Дата:", event.getDateTime().format(DATE_FORMATTER));
        addInfoRow(infoGrid, row++, "Жанр:", event.getDescription().genre().toString());
        addInfoRow(infoGrid, row++, "Формат:", event.getDescription().format().toString());
        addInfoRow(infoGrid, row++, "Уровень:", event.getDescription().participantsLevel().toString());
        addInfoRow(infoGrid, row++, "Место:", event.getPlacement().getPlacement());
        addInfoRow(infoGrid, row++, "Адрес:", event.getPlacement().getAddress());
        addInfoRow(infoGrid, row++, "Вместимость:", String.valueOf(event.getPlacement().getCapacity()));

        Label descLabel = new Label("Описание:");
        descLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        TextArea descArea = new TextArea(event.getDescription().description());
        descArea.setWrapText(true);
        descArea.setEditable(false);
        descArea.setPrefRowCount(3);

        Label membersLabel = new Label("Участники (" + event.getMembers().size() + "):");
        membersLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        ListView<String> membersList = new ListView<>();
        updateMembersList(membersList, event);
        membersList.setPrefHeight(150);

        var dialogDisposable = eventService.getMemberUpdates()
                .filter(update -> update.event().getId() == event.getId())
                .observeOn(Schedulers.io())
                .subscribe(update -> Platform.runLater(() -> {
                    membersLabel.setText("Участники (" + event.getMembers().size() + "):");
                    updateMembersList(membersList, event);
                }));

        Button closeBtn = new Button("Закрыть");
        closeBtn.setOnAction(e -> detailsStage.close());

        detailsStage.setOnHidden(e -> dialogDisposable.dispose());

        content.getChildren().addAll(titleLabel, infoGrid, descLabel, descArea, membersLabel, membersList, closeBtn);

        Scene scene = new Scene(content, 500, 550);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void updateMembersList(ListView<String> membersList, Event event) {
        membersList.getItems().clear();
        for (Member member : event.getMembers()) {
            membersList.getItems().add(String.format("%s %s - %s",
                    member.getUser().getFirstName(),
                    member.getUser().getLastName(),
                    getMemberTypeName(member.getMemberType())));
        }
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label valueNode = new Label(value);
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private String getMemberTypeName(MemberType type) {
        return switch (type) {
            case GUEST -> "Гость";
            case PERFORMER -> "Исполнитель";
            case ORGANIZER -> "Организатор";
            case PARTNER -> "Партнер";
        };
    }

    @Override
    public void stop() {
        disposables.dispose();
    }

    private class EventListCell extends ListCell<Event> {
        @Override
        protected void updateItem(Event event, boolean empty) {
            super.updateItem(event, empty);

            if (empty || event == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox container = new VBox(3);

                Label nameLabel = new Label(event.getName());
                nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

                Label infoLabel = new Label(String.format("%s | %s | Участников: %d",
                        event.getDescription().genre(),
                        event.getDateTime().format(DATE_FORMATTER),
                        event.getMembers().size()
                ));
                infoLabel.setTextFill(Color.GRAY);

                Label placeLabel = new Label("Место: " + event.getPlacement().getPlacement());
                placeLabel.setTextFill(Color.DARKBLUE);

                container.getChildren().addAll(nameLabel, infoLabel, placeLabel);
                setGraphic(container);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
