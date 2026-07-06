# Load Balancer Lab

A lab for experimenting with different load balancing strategies, built from scratch in Java/Spring Boot. It includes a configurable **load balancer** and a test **backend-server** that can be run as multiple instances, plus Python scripts to orchestrate the environment and generate test traffic.

## What does the project do?

The `load-balancer` receives HTTP requests on `/hello`, picks a healthy backend instance according to the configured strategy, forwards the request, and returns the response to the client. Meanwhile, a periodic health check monitors every registered server and removes it from the pool if it stops responding.

The `backend-server` is a test app that simulates different response times depending on the instance name (A, B, or C), which makes it easy to visually observe how each strategy distributes the load.

## Architecture

The repo is made up of two independent Spring Boot applications plus a set of support scripts:

```
load-balancer-lab/
├── load-balancer/        # The load balancer (port 8080)
├── backend-server/       # Test backend server (instances A, B, C)
└── scripts/              # Python utilities
```

### `load-balancer`

- **`LoadBalancerController`** — exposes `GET /hello` (forwards to the chosen backend) and `GET /health`.
- **`LoadBalancerServiceImpl`** — orchestrates the flow: picks the active strategy (by name, via Spring), asks the `ServerRegistry` for a server, forwards the request with `RestClient`, and releases the connection when done.
- **`ServerRegistry` / `InMemoryServerRegistry`** — keeps the list of configured servers, filters the healthy ones, and uses a lock so server selection is thread-safe.
- **`HealthCheckServiceImpl`** — every 5 seconds (`@Scheduled`) hits `/health` on each server and updates its status (`UP`/`DOWN`).
- **`LoadBalancingStrategy`** — common interface for all strategies (`selectServer(servers, request)`), implemented as Spring `@Component` beans and injected as `Map<String, LoadBalancingStrategy>` (the key is the bean name).
- **`LoadBalancerProperties`** — maps the `loadbalancer` section of `application.yaml`: which strategy to use and the list of servers (name, URL, weight).

### `backend-server`

A minimal app with a single controller (`HelloController`) that responds on `/hello`, simulating different latency depending on the instance name (`app.server-name` property), and `/health` for the health check. It's started multiple times on different ports to simulate a pool of real servers.

### Request flow

```
client → load-balancer:8080/hello
              │
              ├─ HealthCheckService keeps the list of healthy servers up to date
              ├─ LoadBalancingStrategy picks one of those servers
              ├─ ServerRegistry increments its active connections
              ├─ RestClient forwards the request to the chosen backend
              └─ active connections are decremented once it responds
                              │
                              ▼
                 backend-server:808X/hello (A, B, or C)
```

## Load balancing strategies

All of them are configured via the `loadbalancer.strategy` property in `application.yaml`, using the bean name (`@Component("...")`):

| Name (`strategy`)            | Class                             | How it picks the server |
|------------------------------|------------------------------------|--------------------------|
| `roundRobin`                 | `RoundRobinStrategy`               | Cycles through servers in order using an atomic counter. |
| `random`                     | `RandomStrategy`                   | Picks a random server on every request. |
| `leastConnections`           | `LeastConnectionsStrategy`         | Selects the server with the fewest active connections at that moment. |
| `weightedRoundRobin`         | `WeightRoundRobinStrategy`         | Smooth weighted round robin: each server accumulates its weight every round, and the one with the highest accumulated weight is picked, then has the total weight subtracted from it — favors higher-`weight` servers while still rotating through all of them. |
| `weightedLeastConnections`   | `WeightLeastConnectionsStrategy`   | Like `leastConnections`, but normalizes active connections by each server's weight (`activeConnections / weight`), so higher-weight servers can carry more simultaneous connections. |
| `ipHash`                     | `IPHashStrategy`                   | Hashes the client's IP (`X-Forwarded-For` header or remote address) and always maps it to the same server, giving simple sticky sessions. |

Each server's weight (`weight`) is defined in `application.yaml` and only matters for the weighted strategies.

## Configuration

`load-balancer/src/main/resources/application.yaml`:

```yaml
loadbalancer:
  strategy: ipHash        # roundRobin | random | leastConnections | weightedRoundRobin | weightedLeastConnections | ipHash

  servers:
    - name: A
      url: http://localhost:8081
      weight: 9
    - name: B
      url: http://localhost:8082
      weight: 5
    - name: C
      url: http://localhost:8083
      weight: 3
```

To try a different strategy, change the `strategy` value and restart the load balancer.

## How to run it

### 1. Requirements

- Java 17+ (or whichever version the `pom.xml` files require)
- Python 3 (for the scripts in `scripts/`, dependency: `requests`)

### 2. Build the test backend

```bash
cd backend-server
./mvnw clean package -DskipTests
cd ..
```

### 3. Start backend instances A, B, and C

The `scripts/manage_servers.py` script manages the processes (using the jar built in the previous step):

```bash
python3 scripts/manage_servers.py start          # starts A (8081), B (8082), and C (8083)
python3 scripts/manage_servers.py status         # check each instance's status
python3 scripts/manage_servers.py stop           # stop all of them
python3 scripts/manage_servers.py start A        # start a single instance
python3 scripts/manage_servers.py restart B
```

Logs are written to `scripts/.logs/` and PIDs to `scripts/.pids/`.

### 4. Start the load balancer

```bash
cd load-balancer
./mvnw spring-boot:run
```

By default it listens on `http://localhost:8080`.

### 5. Test the balancing

Manual request:

```bash
curl http://localhost:8080/hello
```

Test script with concurrent traffic (simulates different clients via `X-Forwarded-For`, especially useful for seeing `ipHash` in action):

```bash
pip install requests   # if needed
python3 scripts/test_lb.py
```

The script fires 30 requests (10 concurrent) spread across 5 simulated client IPs and prints which server responded, the status, and the time for each one.

## Notes

- The health check runs every 5 seconds and marks any server whose `/health` doesn't respond `2xx` as unavailable.
- If all servers are down, the load balancer responds with an error (`IllegalStateException: No healthy servers available`).
- The strategies are stateless with respect to the server registry except for their own internal counters (round robin index, accumulated weights), so adding/removing servers at runtime is not supported — it requires a restart.
