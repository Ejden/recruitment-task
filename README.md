# Allegro summer e-xperience - zadanie rekrutacyjne nr 3

## Spis treści
<details open="open">
  <ol>
    <li><a href="#język-Implementacji">Język Implementacji</a></li>
    <li><a href="#wykorzystane-biblioteki">Wykorzystane biblioteki</a></li>
    <li><a href="#live-demo">Live demo</a></li>
    <li>
      <a href="#lokalna-instalacja-serwera">Lokalna instalacja serwera</a>
      <ul>
        <li><a href="#docker">Docker</a></li>
        <li><a href="#gradlew">Gradlew</a></li>
        <li><a href="#intellij-idea">Intellij IDEA</a></li>
      </ul>
    </li>
    <li><a href="#endpointy">Endpointy</a>
      <ul>
        <li><a href="#rest-api">Rest Api</a></li>
        <li><a href="#graphql">Graphql</a></li>
      </ul>
    </li>
    <li><a href="#dalszy-rozwój-aplikacji">Dalszy rozwój aplikacji</a></li>
  </ol>
</details>

## Język Implementacji
* Kotlin
## Wykorzystane biblioteki
* [Spring Boot](https://spring.io/)
* [Dgs-framework](https://netflix.github.io/dgs/)
* [WireMock](http://wiremock.org/)
* [Junit 5](https://junit.org/junit5/)

## Live demo
Pod tym adresem znajduje się najnowsza wersja aplikacji z branch master.

https://allegro-recruitment-task.herokuapp.com/

## Lokalna instalacja serwera

Przed instalacją serwera do jego poprawnego działania wymagane jest wygenerowanie personal token w serwisie github.
W tym celu należy wejść w ustawienia profilu na [github](https://github.com/settings/profile). Następnie wejść w
zakładkę [Developer Seetings](https://github.com/settings/apps). Na liście należy wybrać [Personal access tokens](https://github.com/settings/tokens)
oraz wygenerować nowy kod. W scopes należy wybrać scope repo -> public_repo. 

### Docker
Aby uruchomić serwer przy pomocy dockera należy wykonać poniższe kroki w terminalu

* Pobranie aplikacji serwerowej
```
docker pull ejden/allegro-recruitment-task:latest
```
* Stworzenie i uruchomienie pobranego obrazu. 
  
Zapisany zostanie on pod nazwą server, aczkolwiek można ją zmieniać wedle własnego uznania.
Domyślnie aplikacja zostanie uruchomiona na porcie 8080. Aby to zmienić i mieć dostęp do serwera na przykład na 
porcie 7070, należy podmienić -p 8080:8080 na -p 7070:8080.
```
docker run -p 8080:8080 -e GITHUB_TOKEN='TU_WKLEIC_PERSONAL_TOKEN_Z_GITHBUBA' --name server ejden/allegro-recruitment-task
```

### Gradlew
Aby uruchomić aplikację przy pomocy gradle'a należy skopiować kod przy pomocy komendy
```
git clone https://github.com/Ejden/recruitment-task.git
```
Następnie należy przejść do folderu z utworzonym projektem i zbudować projekt przy użyciu komendy
```
gradlew.bat build
```
Aby uruchomić serwer należy wywołać komendę
```
gradlew.bat bootRun --args='--GITHUB_TOKEN=TU_WKLEIC_PERSONAL_TOKEN_Z_GITHBUBA'
```
Aplikacja będzie dostępna pod adresem localhost:8080

### Intellij IDEA

W celu uruchomienia serwera w środowisku Intellij IDEA należy wybrać opcję New->Project from Version Control
i w polu url wkleić link do repozytorium - https://github.com/Ejden/recruitment-task.git.
Przed uruchomieniem należy zedytować konfigurację uruchomieniową. W zakładce environment należy wkleić poniższy env w polu
environment variables
```
GITHUB_TOKEN=TU_WKLEIC_PERSONAL_TOKEN_Z_GITHBUBA
```

## Endpointy
Aplikacja pozwala na wyciągnięcie danych na dwa sposoby. Za pomocą Rest Api oraz graphql
### Rest Api
* GET ```/api/users/{nazwa uzytkownika}/repositories``` - Pobieranie listingu repozytoriów

Dozwolone Parametry

| nazwa     | typ     | W     | Opis                                          | Dozwolone wartości                  | Domyślna wartość                                                 |
|-----------|---------|-------|-----------------------------------------------|-------------------------------------|------------------------------------------------------------------|
| username  | String  | Path  | Określa nazwę użytkownika                     |                                     |                                                                  |
| type      | String  | Query | Typ repozytoriów                              | all, owner, member                  |                                                                  |
| sort      | String  | Query | Określa pole po którym ma nastąpić sortowanie | created, updated, pushed, full_name |                                                                  |
| direction | String  | Query | Określa sposób sortowania                     | desc, asc                           | asc przy sortowaniu według full_name, desc w przeciwnym wypadku  |
| per_page  | Integer | Query | Określa ilość repozytoriów na jedną stronę    | od 1 do 100                         | 30                                                               |
| page      | Integer | Query | Określa stronę którą chcemy pobrać            | od 1                                | 1                                                                |    


#### Przykładowe zapytanie: GET ``` http://localhost:8080/api/users/allegro/repositories?per_page=3&page=2 ```


Odpowiedź:
```json
{
  "content": [
    {
      "name": "allegro-tech-labs-microservices",
      "stargazersCount": 6
    },
    {
      "name": "allegro.tech",
      "stargazersCount": 20
    },
    {
      "name": "allRank",
      "stargazersCount": 289
    }
  ],
    "page": {
    "currentPage": 2,
    "totalPages": 29,
    "perPage": 3,
    "sortBy": null,
    "sortDirection": null
  }
}
```


* GET ```/api/users/{nazwa uzytkownika}/stargazers``` - Pobranie sumy gwiazdek ze wszystkich repozytoriów dla danego użytkownika

#### Przykładowe zapytanie: GET ```http://localhost:8080/api/users/allegro/stargazers```
Odpowiedź:
```json
{
  "totalStargazers": 2
}
```

### Graphql
* POST ```/graphql```

Graphql udostępnia tylko jeden endpoint, pod który wysyłane są różne zapytania w body.

#### Schemat zapytania
```graphql endpoint
{
  user(username: String!) {
    username
    repositories(page: Int, perPage: Int, type: String, sort: String, direction: String) {
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
  
#### Przykładowe zapytanie: POST ```http://localhost:8080/graphql```
```graphql
{
  user(username: "allegro") {
    repositories(perPage: 5) {
      totalStargazers
      nodes {
        name
      }
    }
  }
}
```
Odpowiedź:
```json
{
  "data": {
    "user": {
      "repositories": {
        "totalStargazers": 13104,
        "nodes": [
          {
            "name": "akubra"
          },
          {
            "name": "allegro-api"
          },
          {
            "name": "allegro-tech-labs-iot"
          },
          {
            "name": "allegro-tech-labs-microservices"
          },
          {
            "name": "allegro.tech"
          }
        ]
      }
    }
  }
}
```

## Dalszy rozwój aplikacji
Aplikacja została napisana w sposób pozwalający na łatwe rozbudowanie zarówno w kierunku graphql jak i rest api.

* Jedną z możliwości dalszego rozwoju jest wprowadzenie obsługi błędów typu i zwracanie odpowiedniej odpowiedzi użytkownikowi.
  Wymagałoby to zaimplementowania np. ControllerAdvice pozwalającego na obsługę błędów i możliwość zwrócenia przyjaznej dla użytkownika końcowego
  informacji na temat tego co poszło nie tak.
  
* Api githuba nie udostępnia bezpośrednio informacji o sumie gwiazdek z repozytoriów danego użytkownika.
  Wymaga to iteracji po wszystkich repozytoriach, a dla użytkowników o dużej liczbie repozytoriów potrzebna jest duża ilość wykonanych requestów.
  Przykładem jest firma microsoft, która wymaga wykonania około 40 requestów w celu obliczenia sumy gwiazdek. Jest to zadanie
  obciążające dla aplikacji i czas odpowiedzi znacznie się wydłuża. Z tego powodu możnaby zaimplementować cache, który trzymałby
  dane na temat ilości gwiadek, aby nie musieć ich sumy przy każdym zapytaniu


