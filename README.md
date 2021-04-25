# allegro summer e-xperience - zadanie rekrutacyjne nr 3

### Język Implementacji
* Kotlin
### Wykorzystane biblioteki
* Spring Boot
* WireMock
* Junit 5
* graphql-dgs

### Instalacja serwera lokalnie

#### Docker

#### 

### Endpointy
Aplikacja pozwala na wyciągnięcie danych na dwa sposoby. Za pomocą Rest Api oraz graphql
#### REST API
* GET```/api/users/{nazwa uzytkownika}/repositories``` - Pobieranie listingu repozytoriów

Dozwolone Parametry

| nazwa     | typ     | W     | Opis                                          | Dozwolone wartości                  | Domyślna wartość                                                 |
|-----------|---------|-------|-----------------------------------------------|-------------------------------------|------------------------------------------------------------------|
| username  | String  | Path  | Określa nazwę użytkownika                     |                                     |                                                                  |
| type      | String  | Query | Typ repozytoriów                              | all, owner, member                  |                                                                  |
| sort      | String  | Query | Określa pole po którym ma nastąpić sortowanie | created, updated, pushed, full_name |                                                                  |
| direction | String  | Query | Określa sposób sortowania                     | desc, asc                           | asc przy sortowaniu według full_name, desc w przeciwnym wypadku  |
| per_page  | Integer | Query | Określa ilość repozytoriów na jedną stronę    | od 1 do 100                         | 30                                                               |
| page      | Integer | Query | Określa stronę którą chcemy pobrać            | od 1                                | 1                                                                |    

* GET```/api/users/{nazwa uzytkownika}/stargazers``` - Pobranie sumy gwiazdek ze wszystkich repozytoriów dla danego użytkownika


#### GRAPHQL
* POST```/graphql```

Schemat zapytania
```{
  user(username: String!) {
    username
    repositories(page: Int, perPage: Int) {
      totalStargazers
      nodes {
        name
        stargazersCount
      }
    }
  }
}
```
* Pola z wykrzyknikiem oznaczają, że są wymagane.
* Repositories określa repozytoria
    * Pole totalStargazers określa sumę gwiazdek ze wszystkich repozytoriów
* Node określa obiekt pojedyńczego repozytorium. 
    * Pole `name` określa nazwę repozytorium
    * Pole `stargazersCount` określa liczbę gwiazdek dla pojedyńczego repozytorium
  
### 
