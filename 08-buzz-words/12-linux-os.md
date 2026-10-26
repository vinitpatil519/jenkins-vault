# Linux & OS Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| Bootloader (GRUB) | Loads kernel at boot |
| Capabilities | Split root powers into units (CAP_NET_ADMIN) |
| chroot | Change apparent root directory |
| Context switch | CPU swapping between processes/threads |
| Cron / systemd timer | Scheduled tasks |
| Daemon | Background service process |
| File descriptor | Handle to open file/socket; limits via `ulimit -n` |
| Inode | Metadata record of a file; can run out before disk space |
| Kernel space vs user space | Privileged kernel vs normal programs |
| Load average | Runnable + waiting processes over 1/5/15 min |
| Mount / fstab | Attach filesystem / boot-time mount config |
| Page cache | RAM caching disk data |
| PID 1 / init | First process; reaps zombies (systemd, tini) |
| Process vs thread | Own memory space vs shared within process |
| SELinux / AppArmor | Mandatory access control frameworks |
| seccomp | Restrict which syscalls a process may make |
| Signals (SIGTERM, SIGKILL, SIGHUP) | Graceful stop / force kill / reload |
| Swap | Disk used as overflow memory |
| Syscall | Program request to kernel |
| systemd / journald | Init + service manager / its log store |
| Zombie process | Exited child not reaped by parent |
| Exit codes | 0 ok, 1 error, 126 not executable, 127 not found, 130 Ctrl-C, 137 SIGKILL/OOM, 143 SIGTERM |
| iowait | CPU idle waiting on disk |
| Steal time | VM CPU taken by hypervisor for others |
| strace / lsof / tcpdump | Trace syscalls / list open files / capture packets |
| Umask | Default permission mask for new files |
| SUID / sticky bit | Run as file owner / only owner deletes in shared dir |
