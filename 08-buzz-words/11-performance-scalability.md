# Performance & Scalability Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| Amdahl's law | Speedup limited by the serial part |
| Back-of-envelope estimate | Quick capacity math (QPS × size × retention) |
| Benchmark | Measured performance under defined conditions |
| Bottleneck | Component limiting overall throughput |
| Capacity planning | Forecasting resources for future load |
| Cold start | Delay starting a new instance/function |
| Concurrency vs parallelism | Many tasks in progress vs running at same instant |
| Connection storm | Many clients connecting at once (after restart) |
| CPU-bound vs IO-bound | Limited by compute vs by waiting on disk/network |
| Flame graph | Visualization of where CPU time goes |
| Horizontal vs vertical scaling | More machines vs bigger machine |
| Hot spot | Uneven load on one node/key |
| Latency budget | Max allowed latency per hop |
| Little's law | L = λ × W (items in system = arrival rate × time in system) |
| Load shedding | Drop excess requests to protect system |
| Load test / stress test / soak test / spike test | Expected load / beyond limit / long duration / sudden burst |
| Memory leak | Memory never freed, grows until crash |
| OOM killer | Kernel kills process when memory runs out |
| Premature optimization | Optimizing before measuring |
| Profiling | Measuring where time/memory goes |
| QPS / RPS / TPS | Queries / requests / transactions per second |
| Queueing theory | Utilization near 100% → latency explodes |
| Read-heavy vs write-heavy | Workload pattern guiding design |
| Scalability | Handle growth by adding resources |
| Scale to zero | No instances when idle |
| Throughput | Work completed per unit time |
| Warm-up / JIT | JVM/runtime gets faster after code is compiled |
| GC pause | Stop-the-world garbage collection delay |
| Head-of-line blocking | Queue stalled behind slow item |
| Tail at scale | Fan-out makes p99 of backends your typical latency |
