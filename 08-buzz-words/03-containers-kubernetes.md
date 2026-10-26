# Containers & Kubernetes Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| Admission controller | Intercepts K8s API requests to validate/mutate (Kyverno, OPA Gatekeeper) |
| Affinity / anti-affinity | Rules to co-locate or spread pods |
| Argo CD / Flux | GitOps controllers syncing Git to clusters |
| Argo Rollouts / Flagger | Progressive delivery controllers |
| Base image | Image you build FROM |
| BuildKit / Buildah / Kaniko | Image builders (Kaniko/Buildah daemonless) |
| cgroups | Linux feature limiting resources per process group |
| Cluster autoscaler / Karpenter | Add/remove nodes based on pending pods |
| CNI | Container Network Interface plugins (Calico, Cilium) |
| ConfigMap / Secret | K8s objects for config / sensitive data |
| containerd / CRI-O | Container runtimes used by K8s |
| Control plane | API server, etcd, scheduler, controller manager |
| Controller / reconciliation loop | Watches desired vs actual state, acts to converge |
| CRD | Custom Resource Definition: extend K8s API |
| CRI / CSI / CNI | Runtime / storage / network plugin interfaces |
| DaemonSet | One pod per node (agents, log shippers) |
| Deployment | Manages stateless replica pods + rolling updates |
| Distroless | Minimal image with app + runtime only, no shell |
| Docker-in-Docker (DinD) | Docker daemon inside a container |
| eBPF | Run sandboxed programs in Linux kernel (Cilium, Falco) |
| etcd | Consistent key-value store holding K8s state |
| Helm | K8s package manager (charts, templated values) |
| HPA / VPA | Horizontal (replicas) / vertical (resources) pod autoscaler |
| Image digest | Immutable content hash `sha256:...` of image |
| Image layer | Filesystem diff stacked to form image |
| Ingress / Gateway API | HTTP routing into cluster (Gateway API = newer standard) |
| Init container | Runs before main containers start |
| Job / CronJob | Run-to-completion pod / scheduled job |
| KEDA | Event-driven autoscaling (queue length, etc.) |
| Kubelet | Node agent running pods |
| kube-proxy | Programs node networking for Services |
| Kustomize | Overlay-based YAML customization |
| Liveness / readiness / startup probe | Restart if dead / remove from traffic if not ready / wait for slow start |
| Namespace (K8s) | Logical isolation scope in a cluster |
| Namespaces (Linux) | Kernel isolation of PID, net, mount, user, etc. |
| Node pool | Group of nodes with same config |
| OCI | Open Container Initiative: image/runtime standards |
| Operator | Controller encoding app ops knowledge (DB operators) |
| PDB | PodDisruptionBudget: min pods available during voluntary disruption |
| Pod | Smallest K8s unit: one or more containers sharing network/storage |
| Pod Security Admission | Enforce privileged/baseline/restricted pod standards |
| PV / PVC / StorageClass | Volume / claim for it / provisioner type |
| QoS class | Guaranteed / Burstable / BestEffort from requests/limits |
| RBAC | Role-based access: Role/ClusterRole + bindings |
| Registry | Image storage (ECR, GHCR, Harbor) |
| ReplicaSet | Keeps N identical pods running |
| Requests / limits | Guaranteed resources for scheduling / hard cap |
| Rootless container | Runs without root on host |
| runc | Low-level OCI runtime creating containers |
| Service (ClusterIP/NodePort/LoadBalancer) | Stable virtual IP/DNS for pods |
| Service account | Identity for pods |
| Service mesh | Sidecar/ambient proxies for mTLS, retries, traffic split (Istio, Linkerd) |
| Sidecar | Helper container alongside main container |
| StatefulSet | Pods with stable identity + storage (DBs) |
| Taints / tolerations | Node repels pods unless pod tolerates |
| Topology spread | Spread pods across zones/nodes |
| Velero | Backup/restore K8s resources + volumes |
