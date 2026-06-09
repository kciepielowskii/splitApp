# SplitApp 💸

Aplikacja do dzielenia wydatków między znajomymi. Idealna na wspólne wyjazdy, np. wakacje w Chorwacji!

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Security + JWT
- PostgreSQL
- Spring Data JPA
- Swagger / OpenAPI
- Docker

## Funkcjonalności

- Rejestracja i logowanie użytkowników (JWT)
- Tworzenie grup i dodawanie członków
- Dodawanie wydatków z podziałem na osoby
- Algorytm minimalizujący liczbę przelewów (greedy)
- Interfejs kto komu jest winien i ile
- REST API z dokumentacją Swagger
- Testy jednostkowe i integracyjne

## Uruchomienie z Dockerem

```bash
docker-compose up --build
```

Aplikacja dostępna na: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Uruchomienie lokalne

1. Stwórz bazę danych PostgreSQL:
```sql
CREATE DATABASE splitapp;
```

2. Skopiuj `application-example.yml` do `application.yml` i uzupełnij dane:
```bash
cp src/main/resources/application-example.yml src/main/resources/application.yml
```

3. Uruchom aplikację:
```bash
./mvnw spring-boot:run
```

## Przykładowe endpointy

### Rejestracja
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"password123","displayName":"Jan"}'
```

### Tworzenie grupy
```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Chorwacja 2026","memberIds":[2,3]}'
```

### Sprawdzenie sald
```bash
curl -X GET http://localhost:8080/api/groups/1/balances \
  -H "Authorization: Bearer TOKEN"
```

## Testy

```bash
./mvnw test
```

## Autor

Konrad Ciepielowski