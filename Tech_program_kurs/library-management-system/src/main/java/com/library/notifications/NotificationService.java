package com.library.notifications;

import com.library.loans.Loan;
import com.library.books.Book;
import com.library.users.User;
import java.util.ArrayList;
import java.util.List;

public class NotificationService implements NotificationSubject {
    private List<NotificationObserver> observers;

    public NotificationService() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (NotificationObserver observer : observers) {
            observer.update(message);
        }
    }

    public void sendOverdueNotification(Loan loan) {
        String message = String.format(
                "Уважаемый %s, ваша книга '%s' просрочена. Пожалуйста, верните ее как можно скорее.",
                loan.getUser().getName(),
                loan.getBookCopy().getBook().getTitle()
        );
        notifyObservers(message);
    }

    public void sendNewArrivalNotification(Book book) {
        String message = String.format(
                "Новая книга доступна: '%s' от %s",
                book.getTitle(),
                book.getAuthor()
        );
        notifyObservers(message);
    }

    public void sendReturnReminder(Loan loan) {
        String message = String.format(
                "Напоминание: книга '%s' должна быть возвращена до %s",
                loan.getBookCopy().getBook().getTitle(),
                loan.getDueDate()
        );
        notifyObservers(message);
    }

    // Новый метод для отправки SMS уведомлений
    public void sendSMSNotification(User user, String message) {
        System.out.println("📱 Отправка SMS на номер: " + user.getPhone());
        System.out.println("Получатель: " + user.getName());
        System.out.println("Сообщение: " + message);
        System.out.println("✅ SMS отправлено успешно!");

        // В реальной системе здесь был бы вызов SMS API
        // Для демонстрации просто выводим в консоль

        String logMessage = String.format(
                "SMS отправлено пользователю %s (ID: %s) на номер %s: %s",
                user.getName(),
                user.getUserId(),
                user.getPhone(),
                message.length() > 50 ? message.substring(0, 50) + "..." : message
        );

    }

    // Метод для массовой рассылки
    public void sendBulkSMS(List<User> users, String message) {
        System.out.println("📨 Массовая рассылка SMS для " + users.size() + " пользователей:");

        for (User user : users) {
            sendSMSNotification(user, message);
        }

        System.out.println("✅ Массовая рассылка завершена!");
    }
}