# Eruplan Server

<p align="center"><img src='https://i.postimg.cc/G3YX9jYR/logo-eruplan.png' alt="Eruplan Logo" height="400"></p>


## 👥 Authors

- **Angelo Antonio Prisco** - [AngeloAntonioPrisco](https://github.com/AngeloAntonioPrisco) as PM.
- **Cristian Ranieri** - [CristianRanieri](https://github.com/CristianRanieri) as PM.
- **Christian Comiato** - [ChristianComiato](https://github.com/christiancomiato) as Developer & Tester.
- **Salvatore Mastellone** - [SalvatoreMastellone](https://github.com/Salvatore-Mastellone) as Developer & Tester .
- **Camilla Piceda** - [CamillaPiceda](https://github.com/camilla554) as Developer & Tester.
- **Manuel Giordano** - [ManuelGiordano](https://github.com/MGiordano202) as Developer & Tester.
- **Francesco Antonio Di Flumeri** - [FrancescoDiFlumeri](https://github.com/FreeKaraZero) as Developer & Tester.
- **Alessandro Ferrara** - [AlessandroFerrara](https://github.com/aferrara206-alt) as Developer & Tester.
- **Salvatore Grimaldi** - [SalvatoreGrimaldi](https://github.com/salvatoregrimaldi03) as Tester.
- **Ciro Esposito** - [CiroEsposito](https://github.com/ciroesposito04) as Tester.
- **Lorenzo Di Riso** - [LorenzoDiRiso](https://github.com/ldiriso4) as Tester.
- **Gennaro Francesco Coppola** - [GennaroFrancescoCoppola](https://github.com/valufra) as Tester.
- **Alfonso Lamberti** - [AlfonsoLamberti](https://github.com/alfonsoobit) as Tester.


We are all students at **University of Salerno (UNISA)**. PMs are currently enrolled in the Master's program in **Software Engineering**, while all testers are enrolled in the Bachelor’s degree program in Computer Science.

## 💡 What is it?
**Eruplan Server** is the official backend system for the Eruplan ecosystem. It is a RESTful API built with **Java** and **Spring Boot** that manages data consistency, business logic, and communication between the database and the client applications.

The architecture is divided into specific modules to handle different aspects of the civil protection domain:
- **GNF (Family Unit Management):** Handles family members, invitations, housing, and vehicles.
- **GPE (Evacuation Plan Management):** Manages danger zones (polygons), safe zones, and generates evacuation plans.
- **GSE (Historical Evacuation Management):** Provides data on past volcanic eruptions and evacuation logs.
- **GUM (Mobile User Management):** Handles citizen registration and authentication.
- **GUW (Web User Management):** Handles civil protection operator authentication.
- **Notification System:** Integrated with Firebase Admin SDK to send broadcast alerts.

## ⚙️ Local Configuration
The application requires specific **Environment Variables** to run. These are referenced in `src/main/resources/application-local.properties` and `FirebaseConfig.java`.

| Variable | Description | Example Value |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Defines the active profile. | `local` |
| `FIREBASE_CREDENTIALS` | JSON content of the Firebase Service Account. | `{"type": "service_account", ...}` |
| `LOCAL_DATASOURCE_URL` | JDBC URL for the local MySQL database. | `jdbc:mysql://localhost:3306/eruplan_db` |
| `LOCAL_DATASOURCE_ROOT` | Local database username. | `root` |
| `LOCAL_DATASOURCE_PASSWORD` | Local database password. | `password` |

## 🚀 How to try it
The server is a Java application managed by Maven.

### Prerequisites
- **Java Development Kit (JDK) 21**
- **Maven**
- **MySQL** (for local development)

### Run locally
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/T-R-M-V-spin-off/eruplanserver.git](https://github.com/T-R-M-V-spin-off/eruplanserver.git)
2. **Database Setup: Ensure MySQL is running locally.**


3. **Configure Environment: Set the environment variables listed in the Configuration section.**


4. **Build and Run:**
    ```bash
    mvn clean install
    mvn spring-boot:run

### API Endpoints Structure
The application defines a global prefix for all endpoints.
* **Base URL:** `http://localhost:8080/utenti/`

**Key Routes:**
* **Mobile Users:** `/utenti/gestoreUtentiMobile/login`, `/utenti/gestoreUtentiMobile/registra`
* **Web Operators:** `/utenti/gestoreUtentiWeb/login`
* **Family Management:** `/utenti/gestoreNucleo/membri`, `/utenti/gestoreNucleo/invita`
* **Plans:** `/utenti/gestorePiani/genera`

## ☁️ Deployment
The project is configured for deployment on **Azure Web Apps** via GitHub Actions workflow. The configuration file is located at `.github/workflows/main_eruplanserver.yml`.

## 🛠 Built With
- [Java 21](https://www.oracle.com/java/) - Core language.
- [Spring Boot 3.2.5](https://spring.io/projects/spring-boot) - Backend framework.
- [Maven](https://maven.apache.org/) - Build tool.
- [MySQL](https://www.mysql.com/) - Local Database.
- [SQL Server](https://www.microsoft.com/sql-server) - Azure Database.
- [Firebase Admin SDK](https://firebase.google.com/) - Push notifications.
- [JSON Simple](https://github.com/fangyidong/json-simple) - JSON processing.
- [Lombok](https://projectlombok.org/) - Boilerplate code reduction.

## 🔗 Related resources
- [Eruplan Web Client](https://github.com/T-R-M-V-spin-off/eruplanwebclient) - The official web frontend.
- [Eruplan Mobile Client](https://github.com/T-R-M-V-spin-off/eruplanmobileclient) - The official mobile app.