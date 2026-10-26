# Distributed Systems Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| At-most / at-least / exactly-once | Delivery guarantees: may lose / may duplicate / neither (needs idempotency + transactions) |
| Byzantine fault | Node behaves maliciously/arbitrarily |
| CAP theorem | During a network partition choose Consistency or Availability |
| PACELC | If Partition: A or C; Else: Latency or Consistency |
| Clock skew | Machines' clocks disagree |
| Consensus | Nodes agreeing on a value (Raft, Paxos) |
| Consistent hashing | Hash ring so adding nodes moves few keys |
| CRDT | Data types that merge without conflicts |
| Eventual consistency | Replicas converge given no new writes |
| Fencing token | Monotonic token preventing stale leader writes |
| Gossip protocol | Nodes spread state by random peer chatter |
| Heartbeat | Periodic "I'm alive" signal |
| Idempotency | Repeating operation has same effect |
| Lamport / vector clocks | Logical ordering of events without wall time |
| Leader election | Choosing one coordinator node |
| Linearizability | Operations appear instantaneous, in real-time order (strongest) |
| Network partition | Nodes can't talk to each other |
| Quorum | Majority needed to read/write (N/2+1) |
| Raft | Understandable consensus algorithm (etcd, Consul) |
| Read-your-writes | You always see your own updates |
| Replication factor | Number of copies of data |
| Two-phase commit (2PC) | Coordinator prepares then commits across nodes; blocking |
| Two generals problem | Impossibility of guaranteed agreement over unreliable link |
| Fallacies of distributed computing | Network reliable, latency zero, bandwidth infinite... (all false) |
| Tail latency | Slowest percentiles (p99) dominate user experience at scale |
| Thundering herd | Many clients wake/retry simultaneously |
| Tombstone | Marker for deleted data in replicated stores |
| Split brain | Partition leads to two active leaders |
| Sticky sessions | LB routes user to same backend |
| Distributed lock | Lock across machines (Redis Redlock, etcd, ZooKeeper) |
| ZooKeeper | Coordination service (config, locks, leader election) |
