package com.library.notification.consumer;

import com.library.notification.event.LoanEvent;
import com.library.notification.model.Notification;
import com.library.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanEventConsumer {

    private final NotificationRepository notificationRepository;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @RabbitListener(queues = "library.loans.queue")
    public void handleLoanEvent(LoanEvent event) {
        log.info("Received loan event: {} for user {}", event.getEventType(), event.getUserId());

        Notification notification = switch (event.getEventType()) {
            case "LOAN_CREATED" -> Notification.builder()
                    .userId(event.getUserId())
                    .userEmail(event.getUserEmail())
                    .type(Notification.NotificationType.LOAN_CREATED)
                    .title("Книга выдана")
                    .message(String.format(
                            "Вы успешно взяли книгу «%s». Срок возврата: %s.",
                            event.getBookTitle(),
                            event.getDueDate().format(DATE_FMT)))
                    .build();

            case "LOAN_RETURNED" -> Notification.builder()
                    .userId(event.getUserId())
                    .userEmail(event.getUserEmail())
                    .type(Notification.NotificationType.LOAN_RETURNED)
                    .title("Книга возвращена")
                    .message(String.format(
                            "Спасибо! Книга «%s» успешно возвращена в библиотеку.",
                            event.getBookTitle()))
                    .build();

            case "LOAN_OVERDUE" -> Notification.builder()
                    .userId(event.getUserId())
                    .userEmail(event.getUserEmail())
                    .type(Notification.NotificationType.LOAN_OVERDUE)
                    .title("Просрочка возврата книги!")
                    .message(String.format(
                            "Уважаемый %s, срок возврата книги «%s» истёк %s. Пожалуйста, верните книгу как можно скорее.",
                            event.getUserFullName(),
                            event.getBookTitle(),
                            event.getDueDate().format(DATE_FMT)))
                    .build();

            default -> {
                log.warn("Unknown event type: {}", event.getEventType());
                yield null;
            }
        };

        if (notification != null) {
            notificationRepository.save(notification);
            log.info("Notification saved for user {}: {}", event.getUserId(), notification.getTitle());
        }
    }
}
