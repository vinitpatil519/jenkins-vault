# APIs & Protocols Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| AMQP | Messaging protocol (RabbitMQ) |
| API contract | Agreed request/response shape (OpenAPI, protobuf) |
| API versioning | `/v1/` path, header, or media-type versions |
| Async API | Spec for event-driven APIs |
| Backpressure | Consumer signals producer to slow down |
| Batch API | Many operations in one request |
| Breaking change | Change forcing clients to update |
| Content negotiation | `Accept` header picks response format |
| Contract testing | Verify consumer/provider agree on API (Pact) |
| CORS | Browser rule controlling cross-origin requests |
| Cursor vs offset pagination | Opaque pointer (stable) vs page numbers (drift) |
| ETag / If-None-Match | Version tag enabling conditional GET / caching |
| gRPC | RPC over HTTP/2 with protobuf; streaming, fast |
| GraphQL | Client specifies exact fields; single endpoint |
| HATEOAS | REST responses include links to next actions |
| HTTP methods | GET read, POST create, PUT replace, PATCH partial, DELETE remove |
| HTTP status classes | 2xx ok, 3xx redirect, 4xx client error, 5xx server error |
| Key codes | 200, 201, 204, 301/302, 304, 400, 401 (unauth), 403 (forbidden), 404, 409, 422, 429, 500, 502, 503, 504 |
| Idempotent methods | GET, PUT, DELETE (and HEAD/OPTIONS) safe to retry |
| JSON Schema | Validates JSON structure |
| JWT | Signed token with claims (header.payload.signature) |
| Long polling | Hold request open until data available |
| MQTT | Lightweight pub/sub protocol for IoT |
| OAuth 2.0 | Delegated authorization via access tokens |
| OIDC | Identity layer on OAuth 2.0 (ID token, who the user is) |
| OpenAPI (Swagger) | REST API description standard |
| Protobuf | Binary schema-based serialization (gRPC) |
| Rate limit headers | `X-RateLimit-*`, `Retry-After` |
| REST | Resource-oriented API over HTTP verbs, stateless |
| RPC | Call remote function as if local |
| SAML | XML-based SSO standard (enterprise) |
| Server-Sent Events (SSE) | Server streams events to browser over HTTP |
| SOAP | XML RPC protocol with WSDL (legacy enterprise) |
| Webhook signature | HMAC header proving webhook sender |
| WebSocket | Bidirectional persistent connection |
