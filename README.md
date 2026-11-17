# TP BPEL — Student Enrollment (orchestration and choreography)

Two implementations of the same enrollment scenario (University, Bank, Residence,
Transport):

- **Part A — Orchestration:** four JAX-WS services plus a WS-BPEL 2.0 process deployed
  on Apache ODE. The process contains an if/else branch, a parallel flow, a fault
  handler and compensation.
- **Part B — Choreography:** the same services driven by four Spring Boot / JMS adapters
  that communicate through ActiveMQ Artemis topics. There is no central controller;
  a completion aggregator acts as the last participant.

The business logic is kept in `services/service-core` and is reused by both parts.

---

## Tools

| Tool | Version |
|---|---|
| IntelliJ IDEA | Community 2024.3+ |
| JDK | Temurin 11 LTS |
| Maven | 3.9.x |
| Tomcat | 9.0.x |
| Apache ODE | 1.3.8 (`ode.war`) |
| Apache CXF | 3.5.x |
| ActiveMQ Artemis | 2.33.x |
| SoapUI | 5.7.x |

Check the expected versions:

```powershell
java -version                            # openjdk version "11.0.xx"
mvn -version                             # Apache Maven 3.9.x, Java 11
& "$env:CATALINA_HOME\bin\version.bat"   # Server version: Apache Tomcat/9.0.x
& "$env:ARTEMIS_HOME\bin\artemis" version
```

The project compiles to Java 11 bytecode. Building with a newer JDK also works,
but deployment must be done on Java 11.

---

## Build

From the `tp-bpel-enrollment` root:

```powershell
mvn clean package
```

This produces:

```
services/bank-service/target/bank-service.war
services/university-service/target/university-service.war
services/residence-service/target/residence-service.war
services/transport-service/target/transport-service.war
```

---

## Part A

### Install ODE

1. Download `apache-ode-war-1.3.8.zip` from the Apache Archives.
2. Put `ode.war` into `$CATALINA_HOME/webapps/`.
3. Start Tomcat: `& "$CATALINA_HOME\bin\startup.bat"`.
4. Open <http://localhost:8080/ode>.

### Deploy the four services

```powershell
Copy-Item services\bank-service\target\bank-service.war              $env:CATALINA_HOME\webapps\
Copy-Item services\university-service\target\university-service.war  $env:CATALINA_HOME\webapps\
Copy-Item services\residence-service\target\residence-service.war    $env:CATALINA_HOME\webapps\
Copy-Item services\transport-service\target\transport-service.war    $env:CATALINA_HOME\webapps\
```

Check that the WSDLs are reachable:

- <http://localhost:8080/bank-service/BankService?wsdl>
- <http://localhost:8080/university-service/UniversityService?wsdl>
- <http://localhost:8080/residence-service/ResidenceService?wsdl>
- <http://localhost:8080/transport-service/TransportService?wsdl>

### Deploy the BPEL process

Copy the entire `bpel-orchestrator` folder (its `partner-wsdls` subfolder holds the
partner contracts):

```powershell
$dest = "$env:CATALINA_HOME\webapps\ode\WEB-INF\processes\enrollment"
New-Item -ItemType Directory -Force -Path $dest | Out-Null
Copy-Item bpel-orchestrator\* $dest -Recurse -Force
```

The Tomcat console should print `Process {...}EnrollmentProcess deployed`.

Orchestrator WSDL to import into SoapUI:
<http://localhost:8080/ode/processes/EnrollmentService?wsdl>

### Tests

Open `enrollment-tests.xml` in SoapUI and run the `EnrollmentSuite` test suite.

| Test | amount | Expected result |
|---|---|---|
| TC01 | 350 000 | `ENROLLED` plus roomNumber, passId, receipt |
| TC02 | 900 000 | `REFUSED` |
| TC03 | 500 000 | `ENROLLED` (the threshold is inclusive) |
| TC04 | 900 000 | `/university-service/status` shows `CANCELLED` |

Manual call:

```powershell
curl.exe -X POST http://localhost:8080/ode/processes/EnrollmentService `
  -H "Content-Type: text/xml; charset=utf-8" -H "SOAPAction: enroll" `
  -d @soap-request.xml
```

---

## Part B

### Artemis topics

```powershell
.\choreography\artemis-setup.ps1
```

The script creates the four multicast addresses (`EnrollmentRequested`,
`StudentRegistered`, `PaymentDecided`, `EnrollmentCompleted`) and then starts the
broker. Console: <http://localhost:8161> (admin / admin).

### Start the four adapters

```powershell
mvn -pl choreography/university-event-adapter -am spring-boot:run
mvn -pl choreography/bank-event-adapter       -am spring-boot:run
mvn -pl choreography/residence-event-adapter  -am spring-boot:run
mvn -pl choreography/transport-event-adapter  -am spring-boot:run
```

HTTP ports: 8081, 8082, 8083, 8084.

### Trigger a flow

```powershell
.\choreography\trigger-flows.ps1
```

The first event uses `amount = 350000` (success), the second one `900000` (refusal).
The traces are visible in the Artemis console.

---

## Common problems

| Symptom | Cause |
|---|---|
| ODE: "Process failed to deploy — namespace mismatch" | The namespaces in `deploy.xml`, the BPEL file and the WSDLs must be identical. |
| CXF: "No binding for operation" | `<soap:operation soapAction="...">` is missing from the binding. |
| BPEL `<assign>`: XPath "No matching node" | The `payload` wrapper is missing from the variable path. |
| SoapUI 500 "InvocationFailure" | The partner service is down, or the WSDL address does not match the Tomcat port. |
| Silent JMS listener | Broker stopped, wrong destination name, or wrong credentials. |

---

