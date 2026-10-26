# Networking Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| Anycast | Same IP announced from many locations; routed to nearest |
| ARP | Maps IP → MAC on local network |
| ASN / BGP | Autonomous System number / protocol routing between networks on the internet |
| Bandwidth vs latency vs throughput | Capacity / delay / actual achieved rate |
| CDN | Edge caches serving content near users |
| CIDR | IP range notation `10.0.0.0/16` |
| Circuit (direct connect / ExpressRoute) | Dedicated private link to cloud |
| Connection pooling | Reuse open connections instead of new per request |
| DDoS | Flood of traffic to take service down |
| DHCP | Auto-assigns IP config to hosts |
| DNS | Name → IP resolution |
| DNS records | A (IPv4), AAAA (IPv6), CNAME (alias), MX (mail), TXT, NS, SRV, PTR (reverse) |
| DNS TTL | How long resolvers cache a record |
| East-west vs north-south traffic | Service-to-service inside DC vs client-to-DC |
| Egress / ingress | Outbound / inbound traffic |
| Firewall (stateful/stateless) | Filters traffic; stateful tracks connections |
| GeoDNS | DNS answer depends on client location |
| GRE / VXLAN / IPsec | Tunneling / overlay / encrypted tunnel protocols |
| Hairpin NAT | Internal client reaching internal server via public IP |
| Head-of-line blocking | One slow packet/request stalls those behind it |
| HTTP/1.1, HTTP/2, HTTP/3 | Text keep-alive / binary multiplexed / over QUIC (UDP) |
| ICMP | Control messages (ping, traceroute) |
| IPv4 / IPv6 | 32-bit / 128-bit addresses |
| Jitter | Variation in latency |
| Keep-alive | Reuse TCP connection for multiple requests |
| L4 vs L7 load balancer | Routes by IP/port vs by HTTP content (path, header) |
| Load balancing algorithms | Round robin, least connections, consistent hashing, weighted |
| MAC address | Hardware address of NIC |
| MTU / MSS | Max packet size / max TCP payload |
| mTLS | Both client and server present certificates |
| NAT / SNAT / DNAT | Rewrite addresses: source (outbound) / destination (port forward) |
| NAT gateway | Lets private subnets reach internet outbound |
| OSI model | 7 layers: physical, data link, network, transport, session, presentation, application |
| Overlay network | Virtual network on top of physical (VXLAN) |
| Packet loss | Packets that never arrive |
| Peering | Direct connection between two networks/VPCs |
| Port | Number identifying service on host (443 HTTPS, 22 SSH) |
| Proxy / reverse proxy | Client-side intermediary / server-side front (Nginx, Envoy) |
| Public / private subnet | Has route to internet gateway / doesn't |
| QUIC | UDP-based transport with built-in TLS (HTTP/3) |
| Rate limiting | Cap requests per client/time |
| RTT | Round-trip time |
| Security group / NACL | Stateful instance firewall / stateless subnet firewall |
| SNI | TLS extension carrying hostname → many certs on one IP |
| Split-horizon DNS | Different answers for internal vs external clients |
| SSL termination / offloading | Decrypt TLS at LB/proxy |
| Subnet | Slice of a network's IP range |
| TCP vs UDP | Reliable ordered connection vs fire-and-forget datagrams |
| TCP 3-way handshake | SYN, SYN-ACK, ACK |
| TIME_WAIT | TCP state after close; too many = port exhaustion |
| TLS handshake | Negotiate keys + verify cert before encrypted traffic |
| VLAN | Logical L2 segment on shared switch |
| VPN | Encrypted tunnel into private network |
| WAF | Web Application Firewall: blocks HTTP attacks (SQLi, XSS) |
| WebSocket | Full-duplex persistent connection over HTTP upgrade |
| Zero Trust networking | Never trust network location; authenticate every request |
