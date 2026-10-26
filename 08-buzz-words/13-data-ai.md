# Data, Streaming & AI Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| Batch vs stream processing | Process data in chunks on schedule vs continuously as it arrives |
| Consumer group (Kafka) | Consumers sharing partitions of a topic |
| Data contract | Agreed schema/semantics between data producer and consumers |
| Data mesh | Domain teams own their data as products |
| Data pipeline / DAG | Ordered processing steps (Airflow DAG) |
| dbt | SQL-based transformations in the warehouse |
| ETL vs ELT | Transform before load vs load raw then transform in warehouse |
| Event streaming | Durable ordered log of events (Kafka, Kinesis, Pulsar) |
| Kafka topic / partition / offset | Named stream / ordered shard / position in shard |
| Lambda vs Kappa architecture | Batch + speed layers vs stream-only |
| Lineage | Where data came from and how it changed |
| Schema registry | Central store of event schemas + compatibility rules |
| Watermark | Stream-processing marker for event-time progress |
| Window (tumbling/sliding/session) | Grouping stream events by time |
| Agent (AI) | LLM that plans and calls tools in a loop |
| Embedding | Vector representing meaning of text/image |
| Fine-tuning | Further training a model on specific data |
| Guardrails | Checks constraining AI inputs/outputs |
| Hallucination | Model output that is confident but false |
| Inference | Running a trained model to get predictions |
| LLM | Large language model |
| LLMOps / MLOps | Practices to deploy, monitor, version models |
| MCP | Model Context Protocol: standard for connecting AI to tools/data |
| Model registry | Versioned store of trained models |
| Prompt injection | Malicious input hijacking model instructions |
| RAG | Retrieval-Augmented Generation: fetch relevant docs, give to LLM |
| Token / context window | Text unit / max tokens model can consider |
| Feature store | Shared store of ML features for training and serving |
| Drift (model/data) | Real-world data diverging from training data |
| GPU scheduling | Allocating GPUs to workloads (K8s device plugins) |
