# Cloud & Infrastructure Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| Ansible | Agentless config management over SSH (YAML playbooks) |
| Autoscaling group (ASG) | Fleet of VMs scaling by policy |
| Availability Zone (AZ) | Isolated datacenter(s) within a region |
| Bare metal | Physical server, no hypervisor |
| Bastion / jump host | Hardened entry server into private network |
| BYOC | Bring Your Own Cloud: vendor runs software in your account |
| CAPEX vs OPEX | Upfront hardware spend vs pay-as-you-go |
| CDK / Pulumi | IaC using real programming languages |
| CloudFormation / ARM / Bicep | AWS / Azure native IaC |
| Cloud-native | Built for cloud: containers, microservices, automation, elastic |
| Cold / warm / hot standby | DR replica off / partially running / fully running |
| Control plane vs data plane | Manages/configures vs carries actual traffic/work |
| Crossplane | Provision cloud resources via Kubernetes CRDs |
| Drift detection | Finding infra changed outside IaC |
| Edge computing | Compute near users (CDN PoPs, devices) |
| Egress cost | Fee for data leaving cloud/region |
| Elasticity | Auto scale up and down with demand |
| FinOps | Practice of managing and optimizing cloud cost |
| Golden AMI | Pre-baked approved VM image |
| Hybrid cloud | On-prem + public cloud together |
| Hypervisor | Software running VMs (KVM, ESXi, Xen) |
| IaaS / PaaS / SaaS | Rent VMs / platform runs your code / finished software |
| FaaS / Serverless | Run functions; provider manages servers, pay per invocation |
| IAM | Identity and Access Management: who can do what |
| Instance profile / IRSA / Workload Identity | Give workloads cloud roles without static keys |
| Landing zone | Pre-configured secure multi-account cloud baseline |
| Lift and shift | Move apps to cloud unchanged |
| Managed service | Provider operates it (RDS, EKS) |
| Multi-cloud | Using several cloud providers |
| Multi-tenancy | Many customers share same infrastructure, isolated |
| Noisy neighbor | Co-tenant hogging shared resources |
| On-demand / reserved / spot | Full price / committed discount / spare capacity, can be reclaimed |
| Organization / account vending | Automated creation of cloud accounts with guardrails |
| Packer | Tool to build machine images |
| Pets vs cattle | Hand-nursed servers vs replaceable identical ones |
| Private link / endpoint | Access cloud service privately without internet |
| Region | Geographic cloud location with multiple AZs |
| Right-sizing | Matching resource size to actual usage |
| RPO / RTO | Max data loss tolerated / max downtime tolerated |
| Savings plan | Commitment-based discount on compute |
| Service quota / limit | Cap on resources per account |
| Shared responsibility model | Cloud secures the cloud; you secure what's in it |
| State file (Terraform) | Record mapping code to real resources; lock + store remotely |
| Tagging strategy | Consistent labels for cost/ownership |
| Terraform module / provider | Reusable IaC unit / plugin for an API |
| Terragrunt | Wrapper keeping Terraform DRY across envs |
| Transit gateway | Hub connecting many VPCs/on-prem networks |
| VPC / VNet | Private isolated network in the cloud |
| Vendor lock-in | Hard to leave a provider due to proprietary services |
| Well-Architected Framework | Cloud vendor best-practice pillars (reliability, security, cost, ...) |
| Zero downtime deployment | Release without user-visible outage |
