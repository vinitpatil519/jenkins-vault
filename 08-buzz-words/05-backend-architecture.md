# Backend & Architecture Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| ACL (anti-corruption layer) | Translation layer protecting your model from a legacy/external one |
| ADR | Architecture Decision Record: short doc of a decision + why |
| Aggregate (DDD) | Cluster of objects treated as one consistency unit |
| Anemic domain model | Objects with data but no behavior |
| API gateway | Single entry for APIs: auth, routing, rate limits |
| Backend for Frontend (BFF) | API tailored per client type (web, mobile) |
| Big ball of mud | System with no discernible structure |
| Bounded context (DDD) | Boundary where one domain model applies |
| Bulkhead | Isolate resources so one failure doesn't sink all |
| Caching strategies | Cache-aside, read-through, write-through, write-behind |
| Cache invalidation | Deciding when cached data is stale |
| Cache stampede / thundering herd | Many requests hit backend at once when cache expires |
| Circuit breaker | Stop calling failing dependency; fail fast; retry later |
| Clean / hexagonal architecture | Business core independent of frameworks; ports & adapters |
| Cohesion / coupling | Things inside belong together / dependence between modules |
| Command / query (CQRS) | Separate write model from read model |
| Conway's law | System design mirrors org communication structure |
| Cron job | Scheduled background task |
| DDD | Domain-Driven Design: model software around business domain |
| Dead letter queue (DLQ) | Where unprocessable messages go |
| Debounce / throttle | Collapse rapid calls into one / cap call rate |
| Dependency injection | Pass dependencies in rather than create inside |
| Distributed monolith | Microservices so coupled they deploy together |
| DRY / KISS / YAGNI | Don't repeat yourself / keep it simple / you ain't gonna need it |
| Event-driven architecture | Services communicate by emitting/consuming events |
| Event sourcing | Store state as sequence of events, rebuild by replay |
| Exponential backoff + jitter | Retry with growing randomized delays |
| Fan-out / fan-in | One message to many consumers / combine many results |
| Graceful degradation | Reduced functionality instead of total failure |
| Graceful shutdown | Finish in-flight work before exit (handle SIGTERM) |
| Hot path | Code executed most often / latency critical |
| Idempotency key | Client-provided ID so retries don't duplicate effects |
| Inversion of control | Framework calls your code |
| Job queue / worker | Background task processing (Sidekiq, Celery) |
| Loose coupling | Components change independently |
| Message broker | Middleware routing messages (RabbitMQ, Kafka) |
| Microservices | Small independently deployable services per capability |
| Modular monolith | One deployable with strict internal module boundaries |
| Monolith | Single deployable application |
| N+1 query | One query per item instead of a join/batch |
| Outbox pattern | Write event to DB table in same transaction, publish later |
| Polyglot persistence | Different DBs for different needs |
| Pub/sub | Publishers emit to topics; subscribers receive |
| Rate limiter algorithms | Token bucket, leaky bucket, fixed/sliding window |
| Retry storm | Retries amplifying overload |
| Saga | Distributed transaction as steps + compensating actions |
| Separation of concerns | Each module handles one responsibility |
| Serverless | Functions/services where provider manages scaling/servers |
| Service discovery | Finding service instances dynamically (DNS, Consul) |
| Service registry | Directory of live service instances |
| SOA | Service-Oriented Architecture (pre-microservices, often ESB) |
| SOLID | Single responsibility, open/closed, Liskov, interface segregation, dependency inversion |
| Stateless vs stateful | No session kept in process vs keeps state |
| Strangler fig | Gradually replace legacy by routing features to new system |
| Technical debt | Cost of shortcuts that must be repaid later |
| Timeout budget | Total time allowed split across downstream calls |
| Twelve-factor | Cloud app principles: config in env, stateless, logs as streams, etc. |
| Webhook | HTTP callback to notify another system |
| Worker pool | Fixed set of workers processing tasks concurrently |
