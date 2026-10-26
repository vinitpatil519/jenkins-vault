# Databases & Storage Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| ACID | Atomicity, Consistency, Isolation, Durability |
| BASE | Basically Available, Soft state, Eventually consistent |
| B-tree / LSM-tree | Read-optimized index structure / write-optimized (RocksDB, Cassandra) |
| Block / file / object storage | Raw disks (EBS) / shared FS (NFS, EFS) / HTTP blobs (S3) |
| Columnar DB | Stores by column; fast analytics (Redshift, ClickHouse) |
| Connection pool (PgBouncer) | Reuse DB connections |
| CDC | Change Data Capture: stream DB changes (Debezium) |
| Data lake / warehouse / lakehouse | Raw files / structured analytics DB / both (Delta, Iceberg) |
| Deadlock | Transactions waiting on each other forever |
| Denormalization | Duplicate data to speed reads |
| Document DB | JSON documents (MongoDB) |
| Expand/contract migration | Add new schema, migrate, remove old → zero-downtime |
| Failover | Promote replica when primary dies |
| Foreign key | Column referencing another table's key |
| Graph DB | Nodes + edges (Neo4j) |
| Hot partition / hot key | One shard getting most traffic |
| Index | Structure to find rows fast; slows writes |
| Isolation levels | Read uncommitted, read committed, repeatable read, serializable |
| Key-value store | Simple get/put by key (Redis, DynamoDB) |
| Materialized view | Stored precomputed query result |
| MVCC | Multi-version concurrency: readers don't block writers |
| Normalization | Remove redundancy across tables (1NF, 2NF, 3NF) |
| NoSQL | Non-relational DB families |
| OLTP vs OLAP | Transactions (many small writes) vs analytics (big scans) |
| ORM | Map objects to tables (Hibernate, SQLAlchemy) |
| Optimistic vs pessimistic locking | Check version at write vs lock upfront |
| Partitioning | Split table by key/range (within one DB) |
| PITR | Point-in-time recovery from WAL/binlog |
| Primary / replica (leader / follower) | Writes go to primary; replicas copy |
| Query plan / EXPLAIN | How DB will execute query |
| RAID | Combine disks for redundancy/performance |
| Read replica | Copy for scaling reads |
| Replication lag | Delay before replica sees writes |
| Sharding | Split data across multiple DB servers |
| Schema migration | Versioned DB schema change (Flyway, Liquibase) |
| Snapshot | Point-in-time copy of volume/DB |
| Split brain | Two nodes both think they're primary |
| Stored procedure | Code running inside DB |
| Time-series DB | Optimized for timestamped data (Prometheus, InfluxDB, Timescale) |
| Transaction | Group of ops all-or-nothing |
| Vacuum (Postgres) | Reclaims space from dead rows |
| Vector DB | Stores embeddings for similarity search (pgvector, Pinecone) |
| WAL | Write-Ahead Log: changes logged before applied |
| Write amplification | One logical write causing many physical writes |
