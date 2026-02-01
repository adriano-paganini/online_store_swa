# Design Pattern Bericht


## 1. Observer Pattern (2-stufige Event-/Listener-Architektur)

### Beschreibung
Das Observer Pattern ist das zentrale Architekturmuster des Notification-Systems. Es implementiert eine zweistufige Event-/Listener-Architektur, die eine klare Trennung zwischen Domain-Logik und technischer Zustellung ermöglicht.

### Implementierung

#### Stufe 1: Beobachtung von Domain-Events (fachliche Ereignisse)

**Publisher:**
- **ProductService**: Publiziert `ProductEvent<?>` bei Produktänderungen (Preis, Name, Beschreibung, Rabatt, Restock)
- **OrderService**: Publiziert `OrderCompletionEvent` nach erfolgreicher Zahlung

**Observer (Listener Stufe 1):**
- **SubscriptionNotificationListener** (`src/main/java/at/qe/skeleton/listeners/SubscriptionNotificationListener.java`)
  - Beobachtet `ProductEvent<?>`
  - Ermittelt betroffene Subscriptions
  - Erstellt Notification-DB-Einträge
  - Publiziert danach kanal-spezifische Delivery-Events (Stufe 2)

- **OrderCompletionEventListener** (`src/main/java/at/qe/skeleton/listeners/OrderCompletionEventListener.java`)
  - Beobachtet `OrderCompletionEvent`
  - Erstellt Notification-DB-Eintrag
  - Publiziert danach Delivery-Event (Stufe 2)

**Technische Details:**
- `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`: Listener reagieren nur bei erfolgreichem DB-Commit
- `@Async`: Verarbeitung ist asynchron und entkoppelt vom Request

#### Stufe 2: Beobachtung von Delivery-Events (technische Zustellung)

**Publisher:**
- Listener aus Stufe 1 publizieren:
  - `EmailNotificationEvent`
  - `SmsNotificationEvent`
  (abhängig vom NotificationType / Strategy)

**Observer (Listener Stufe 2):**
- **EmailNotificationEventListener** (`src/main/java/at/qe/skeleton/listeners/EmailNotificationEventListener.java`)
  - Beobachtet `EmailNotificationEvent<?>`
  - Delegiert an `EmailNotificationService`

- **SmsNotificationEventListener** (`src/main/java/at/qe/skeleton/listeners/SmsNotificationEventListener.java`)
  - Beobachtet `SmsNotificationEvent<?>`
  - Delegiert an `SmsNotificationService`

### Vorteile
- **Stufe 1** beantwortet: „Wer muss informiert werden?"
- **Stufe 2** beantwortet: „Wie wird die Notification zugestellt?"
- Klare Trennung zwischen Domain-Logik und technischer Zustellung
- Erweiterbar um neue Event-Typen oder Notification-Kanäle ohne bestehende Publisher zu ändern
- Lose Kopplung zwischen Komponenten
- Asynchrone Verarbeitung verbessert Performance

### Code-Beispiele

```java
// Publisher: OrderService publiziert OrderCompletionEvent
applicationEventPublisher.publishEvent(new OrderCompletionEvent(fullOrder));

// Stufe 1 Listener: OrderCompletionEventListener
@Async
@Transactional
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleOrderCompleteEvent(OrderCompletionEvent event) {
    Notification notification = notificationService.createNotification(
        order.getUser().getId(),
        NotificationType.EMAIL,
        event
    );
    applicationEventPublisher.publishEvent(new EmailNotificationEvent<>(notification, event));
}

// Stufe 2 Listener: EmailNotificationEventListener
@Async
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleEmailNotificationEvent(EmailNotificationEvent<?> event) {
    emailNotificationService.sendEmail(event);
}
```

---

## 2. Repository Pattern

### Beschreibung
Das Repository Pattern abstrahiert die Datenzugriffsschicht und bietet eine einheitliche Schnittstelle für Datenbankoperationen.

### Implementierung

**Abstraktes Repository:**
- `AbstractRepository<T, ID>` (`src/main/java/at/qe/skeleton/repositories/AbstractRepository.java`)
  - Generische Basis-Interface für alle Repositories
  - Definiert Standard-Methoden: `save`, `findById`, `findAll`, `delete`

**Konkrete Repositories:**
- `UserxRepository` erweitert `AbstractRepository<Userx, Long>`
- `OrderRepository`, `ProductRepository`, `NotificationRepository`, etc.
- Jedes Repository kann zusätzliche spezifische Query-Methoden definieren

### Code-Beispiel

```java
@NoRepositoryBean
public interface AbstractRepository<T, ID extends Serializable> extends Repository<T, ID> {
    void delete(T entity);
    List<T> findAll();
    Optional<T> findById(ID id);
    <S extends T> S save(S entity);
}

public interface UserxRepository extends AbstractRepository<Userx, Long> {
    Optional<Userx> findFirstByUsername(String username);
    Page<Userx> findByRolesContaining(@Param("role") UserxRole role, Pageable pageable);
    // ... weitere spezifische Methoden
}
```

### Vorteile
- Abstraktion der Datenzugriffsschicht
- Einheitliche Schnittstelle für alle Datenbankoperationen
- Einfaches Testen durch Mocking der Repository-Interfaces
- Zentrale Verwaltung von Datenzugriffslogik

---

## 3. DTO/Mapper Pattern

### Beschreibung
Das DTO (Data Transfer Object) Pattern trennt die interne Domain-Modell-Struktur von der API-Schnittstelle. Mapper übernehmen die Konvertierung zwischen Entities und DTOs.

### Implementierung

**DTO-Interface:**
- `DTOMapper<E, D>` (`src/main/java/at/qe/skeleton/mappers/DTOMapper.java`)
  - Generisches Interface für alle Mapper
  - Definiert `mapTo` (Entity → DTO) und `mapFrom` (DTO → Entity)

**Konkrete Mapper:**
- `UserxMapper`: Konvertiert zwischen `Userx` Entity und `UserxDTO`
- `UserxMeMapper`: Spezielle Mapper für User-Profile
- `AddressMapper`: Mapper für Adress-Objekte
- `NotificationResponseMapper`: Mapper für Notification-Responses

**DTOs:**
- `UserxDTO`, `UserxMeDTO`, `ProductDTO`, `OrderDTO`, etc.
- Alle DTOs sind Records (seit Java 14) für Immutability

### Code-Beispiel

```java
public interface DTOMapper<E, D> {
    D mapTo(E entity);
    E mapFrom(D dto);
}

@Service
public class UserxMapper {
    public UserxDTO mapTo(Userx user) {
        if (user == null) return null;
        return new UserxDTO(
            user.getId(),
            user.getCreateDate(),
            user.getUsername(),
            // ... weitere Felder
        );
    }
}
```

### Vorteile
- Trennung von internem Domain-Modell und API-Kontrakt
- Versionierung der API ohne Änderung der Domain-Entities
- Kontrolle über exponierte Daten
- Verbesserte Sicherheit durch gezielte Datenexposition

---

## 4. Dependency Injection Pattern

### Beschreibung
Das Dependency Injection Pattern wird durch Spring Framework implementiert. Abhängigkeiten werden über Konstruktoren injiziert, was Testbarkeit und lose Kopplung fördert.

### Implementierung

**Konstruktor-basierte Dependency Injection:**
- Alle Services, Controller und Komponenten verwenden Konstruktor-Injection
- `@Autowired` Annotation markiert Konstruktoren (optional seit Spring 4.3)
- Spring Container verwaltet den Lebenszyklus und die Abhängigkeiten

### Code-Beispiele

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            ProductService productService,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.applicationEventPublisher = applicationEventPublisher;
    }
}

@Component
public class SubscriptionNotificationListener {
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SubscriptionNotificationListener(
            SubscriptionRepository subscriptionRepository,
            NotificationService notificationService,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
```

### Vorteile
- Lose Kopplung zwischen Komponenten
- Einfaches Testen durch Mocking von Abhängigkeiten
- Zentrale Verwaltung von Abhängigkeiten durch Spring Container
- Verbesserte Wartbarkeit und Erweiterbarkeit

---

## 5. Service Layer Pattern

### Beschreibung
Das Service Layer Pattern organisiert die Business-Logik in separaten Service-Klassen, die zwischen Controllern und Repositories vermitteln.

### Implementierung

**Service-Schicht:**
- `ProductService`: Business-Logik für Produkte
- `OrderService`: Business-Logik für Bestellungen
- `UserxService`: Business-Logik für Benutzer
- `NotificationService`: Business-Logik für Notifications
- `EmailNotificationService`, `SmsNotificationService`: Spezialisierte Services

**Architektur:**
```
Controller → Service → Repository → Database
```

### Code-Beispiel

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    
    @Transactional
    public Order createOrder(OrderCreateDTO orderCreateDTO) {
        // Business-Logik für Order-Erstellung
        // Validierung, Berechnungen, Event-Publishing
    }
}
```

### Vorteile
- Klare Trennung von Concerns
- Wiederverwendbare Business-Logik
- Einfaches Testen der Business-Logik
- Zentrale Verwaltung von Transaktionen

---

## 6. Factory Method Pattern

### Beschreibung
Das Factory Method Pattern wird im `NotificationType` Enum verwendet, um die Erstellung von Event-Objekten zu kapseln. Jeder Notification-Typ (EMAIL, SMS) hat eine eigene Factory-Methode, die das entsprechende Event-Objekt erstellt.

### Implementierung

**Creator (Factory):**
- `NotificationType` Enum (`src/main/java/at/qe/skeleton/model/NotificationType.java`)
  - Definiert die Factory-Methode `createEvent()`
  - Jede Enum-Konstante (EMAIL, SMS) ist eine konkrete Factory

**Product:**
- `EmailNotificationEvent` und `SmsNotificationEvent` sind die konkreten Produkte
- Beide implementieren das `NotificationEvent<?>` Interface

### Code-Beispiel

```java
public enum NotificationType {
    // Jede Konstante ist eine konkrete Factory
    EMAIL(EmailNotificationEvent::new),
    SMS(SmsNotificationEvent::new);

    private final BiFunction<Notification, Payload<?>, NotificationEvent<?>> eventConstructor;

    NotificationType(BiFunction<...> eventConstructor) {
        this.eventConstructor = eventConstructor;
    }

    // Factory Method
    public NotificationEvent<?> createEvent(Notification notification, 
                                           Payload<? extends PayloadInterface> payload) {
        return eventConstructor.apply(notification, payload);
    }
}

// Verwendung
NotificationType channel = NotificationType.EMAIL;
NotificationEvent<?> event = channel.createEvent(notification, payload);
```

### Vorteile
- Lose Kopplung zwischen Event-Erstellung und Verwendung
- Einfache Erweiterbarkeit um neue Notification-Typen
- Kapselung der Objekterstellung
- Type-sichere Implementierung

**Referenz:** [04_DesignPatterns.pdf](file://04_DesignPatterns.pdf) - Factory Method Pattern

---

## 8. Decorator Pattern

### Beschreibung
Das Decorator Pattern wird durch die `Payload` Klasse implementiert, die andere Objekte wrappt und deren Funktionalität erweitert, ohne die ursprüngliche Klasse zu modifizieren.

### Implementierung

**Component:**
- `PayloadInterface`: Definiert die gemeinsame Schnittstelle

**ConcreteComponent:**
- `Order`, `Product`, `Subscription` etc. implementieren `PayloadInterface`

**Decorator:**
- `Payload<T>` wrappt ein Objekt vom Typ `T` und delegiert Methodenaufrufe

### Code-Beispiel

```java
// Component Interface
public interface PayloadInterface {
    String getPayloadSubjectLine();
}

// ConcreteComponent
public class Order implements PayloadInterface {
    @Override
    public String getPayloadSubjectLine() {
        return "Order " + orderNumber + " completed";
    }
}

// Decorator
public class Payload<T extends PayloadInterface> implements PayloadInterface {
    T payloadInfo;  // Wrapped object

    public Payload(T payloadInfo) {
        this.payloadInfo = payloadInfo;
    }

    @Override
    public String getPayloadSubjectLine() {
        // Delegation an das wrapped object
        return payloadInfo.getPayloadSubjectLine();
    }
}

// Verwendung
Order order = new Order(...);
Payload<Order> payload = new Payload<>(order);
String subject = payload.getPayloadSubjectLine(); // Delegiert an Order
```

### Vorteile
- Erweiterung von Funktionalität zur Laufzeit
- Keine Modifikation der ursprünglichen Klassen
- Flexible Kombination von Decorators
- Single Responsibility Principle

**Referenz:** [04_DesignPatterns.pdf](file://04_DesignPatterns.pdf) - Decorator Pattern

---

## 9. Iterator Pattern

### Beschreibung
Das Iterator Pattern wird implizit durch Java Streams und Collections verwendet, um über Sammlungen zu iterieren, ohne deren interne Struktur zu kennen.

### Implementierung

**Iterator:**
- Java Stream API (`java.util.stream.Stream`)
- Collections implementieren `Iterable<T>`

**Concrete Iterators:**
- `.stream()`, `.forEach()`, `.map()`, `.filter()`, `.collect()`

### Code-Beispiele

```java
// Iterator Pattern durch Streams
List<Subscription> subscriptions = subscriptionRepository
    .findByProductAndType(productId, type)
    .stream()                    // Iterator erzeugen
    .distinct()                  // Operation auf Iterator
    .toList();                   // Ergebnis sammeln

// Weitere Beispiele
cart.getItems().stream()
    .map(cartItem -> {           // Transformation
        // ...
        return orderItem;
    })
    .collect(Collectors.toList());

orders.forEach(order -> {        // Iteration
    orderLifecycleService.applyResolvedStatus(order, now);
});
```

### Vorteile
- Einheitliche Iteration über verschiedene Sammlungen
- Kapselung der internen Struktur
- Funktionale Programmierung durch Streams
- Wiederverwendbare Iterationslogik

**Referenz:** [04_DesignPatterns.pdf](file://04_DesignPatterns.pdf) - Iterator Pattern

---

## 10. Facade Pattern

### Beschreibung
Das Facade Pattern wird durch die Service-Klassen implementiert, die komplexe Subsysteme (Repositories, andere Services, Event-Publisher) hinter einer vereinfachten Schnittstelle verbergen.

### Implementierung

**Facade:**
- `OrderService`: Vereinfacht die komplexe Order-Verarbeitung
- `ProductService`: Vereinfacht Produkt-Operationen
- `CartService`: Vereinfacht Warenkorb-Operationen

**Subsystem:**
- Repositories, Event-Publisher, andere Services, Mapper

### Code-Beispiel

```java
@Service
public class OrderService {
    // Komplexes Subsystem
    private final OrderRepository orderRepository;
    private final OrderLifecycleService orderLifecycleService;
    private final CartService cartService;
    private final ProductService productService;
    private final ApplicationEventPublisher applicationEventPublisher;

    // Vereinfachte Schnittstelle
    @Transactional
    public Order createOrder(OrderCreateDTO orderCreateDTO) {
        // Komplexe Logik wird hinter einfacher Methode verborgen:
        // - Cart-Validierung
        // - Adress-Snapshot-Erstellung
        // - OrderItem-Erstellung
        // - Stock-Anpassung
        // - Event-Publishing
        // - etc.
    }
}
```

### Vorteile
- Vereinfachte Schnittstelle für komplexe Subsysteme
- Reduzierte Abhängigkeiten für Clients
- Bessere Wartbarkeit durch Kapselung
- Einfacheres Testen

**Referenz:** [04_DesignPatterns.pdf](file://04_DesignPatterns.pdf) - Facade Pattern

---

## 11. Singleton Pattern (implizit durch Spring)

### Beschreibung
Das Singleton Pattern wird implizit durch Spring Framework implementiert. Alle mit `@Service`, `@Component`, `@Repository` annotierten Klassen werden standardmäßig als Singletons verwaltet.

### Implementierung

**Spring Container als Singleton Manager:**
- Spring erstellt pro Bean-Typ genau eine Instanz
- Diese Instanz wird bei Bedarf injiziert
- Thread-safe durch Spring Container

### Code-Beispiel

```java
@Service  // Spring erstellt eine Singleton-Instanz
public class OrderService {
    // Diese Instanz wird von Spring verwaltet
    // und bei Bedarf injiziert
}

@Service  // Eine weitere Singleton-Instanz
public class ProductService {
    // ...
}

// Spring Container verwaltet beide als Singletons
// und injiziert sie bei Bedarf
```

### Vorteile
- Garantiert eine einzige Instanz pro Bean
- Thread-safe durch Spring Container
- Zentrale Verwaltung durch IoC Container
- Einfache Konfiguration durch Annotationen

**Hinweis:** Im klassischen Sinne ist dies kein explizites Singleton Pattern (keine private Konstruktoren, kein `getInstance()`), sondern wird durch den Spring Container implizit bereitgestellt.

**Referenz:** [04_DesignPatterns.pdf](file://04_DesignPatterns.pdf) - Singleton Pattern

---

## Zusammenfassung

Das System verwendet eine Vielzahl etablierter Design Patterns, die zusammen eine saubere, wartbare und erweiterbare Architektur bilden:

### Verhaltensmuster (Behavioral Patterns):
1. **Observer Pattern (2-stufig)**: Zentrale Architektur für Event-basierte Kommunikation
2. **Iterator Pattern**: Iteration über Collections durch Streams

### Erzeugungsmuster (Creational Patterns):
3. **Factory Method Pattern**: Erstellung von Event-Objekten durch NotificationType (zweifach implementiert)
4. **Singleton Pattern** (implizit): Spring Bean-Verwaltung

### Strukturmuster (Structural Patterns):
5. **Decorator Pattern**: Payload-Klasse und OrderCompletionEvent erweitern Funktionalität
6. **Repository Pattern**: Abstraktion der Datenzugriffsschicht
7. **Facade Pattern**: Services vereinfachen komplexe Subsysteme
8. **DTO/Mapper Pattern**: Trennung von Domain-Modell und API

### Architekturmuster:
9. **Dependency Injection**: Lose Kopplung und Testbarkeit durch Spring
10. **Service Layer Pattern**: Organisation der Business-Logik

Diese Patterns arbeiten zusammen, um eine robuste, skalierbare und wartbare Anwendung zu schaffen, die den Prinzipien des Clean Code und SOLID-Prinzipien folgt. Die Implementierung folgt den Best Practices, die in der Vorlesung "Softwarearchitektur" (04_DesignPatterns.pdf) behandelt werden.

---

## Referenzen

- `Patterns-Adriano.txt`: Beschreibung der 2-stufigen Observer-Pattern-Architektur
- `04_DesignPatterns.pdf`: VO Softwarearchitektur - Design Patterns (Universität Innsbruck, Alexandra Jäger, Wintersemester 25/26)
- Spring Framework Dokumentation: Dependency Injection, Event Handling
- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1995). Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)
- Refactoring Guru: https://refactoring.guru/design-patterns

---

*Bericht erstellt am: $(date)*
*Codebase-Analyse: g4t1 Projekt*
