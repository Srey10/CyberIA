import re, sys, hashlib
from urllib.parse import unquote

IOC = re.compile(
    r'\$\{[^}\n]{0,40}?(jndi|ldap|rmi|dns|java:|env:|sys:|lower:|upper:|::-|\$\{)',
    re.I
)

def scan(path):
    print(f"\n== {path}")
    print("SHA-256:", hashlib.sha256(open(path, 'rb').read()).hexdigest())
    hits = 0

    with open(path, errors='ignore') as f:
        for n, line in enumerate(f, 1):
            decoded = unquote(line)

            for m in IOC.finditer(decoded):
                hits += 1
                s = max(0, m.start() - 30)
                print(f"  line {n}: ...{decoded[s:m.end()+60].strip()}")

    print(f"  {hits} indicator(s) found")

for p in sys.argv[1:]:
    scan(p)
