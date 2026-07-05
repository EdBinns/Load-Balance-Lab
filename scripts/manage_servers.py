#!/usr/bin/env python3
"""
Levanta y detiene las instancias A, B y C del backend-server.

Uso:
    python3 manage_servers.py start          # levanta A, B y C
    python3 manage_servers.py start A        # levanta solo A
    python3 manage_servers.py stop           # detiene A, B y C
    python3 manage_servers.py stop B         # detiene solo B
    python3 manage_servers.py status         # muestra el estado de cada servidor
"""

import argparse
import os
import signal
import subprocess
import sys
import time

ROOT_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BACKEND_DIR = os.path.join(ROOT_DIR, "backend-server")
JAR_PATH = os.path.join(
    BACKEND_DIR, "target", "backend-server-0.0.1-SNAPSHOT.jar"
)
PID_DIR = os.path.join(ROOT_DIR, "scripts", ".pids")
LOG_DIR = os.path.join(ROOT_DIR, "scripts", ".logs")

SERVERS = {
    "A": 8081,
    "B": 8082,
    "C": 8083,
}


def pid_file(name):
    return os.path.join(PID_DIR, f"server_{name}.pid")


def log_file(name):
    return os.path.join(LOG_DIR, f"server_{name}.log")


def is_running(pid):
    try:
        os.kill(pid, 0)
        return True
    except OSError:
        return False


def read_pid(name):
    path = pid_file(name)
    if not os.path.exists(path):
        return None
    with open(path) as f:
        try:
            return int(f.read().strip())
        except ValueError:
            return None


def start_server(name):
    port = SERVERS[name]

    pid = read_pid(name)
    if pid and is_running(pid):
        print(f"[{name}] ya está corriendo (PID {pid}, puerto {port})")
        return

    if not os.path.exists(JAR_PATH):
        print(f"[{name}] ERROR: no se encontró el jar en {JAR_PATH}")
        print("        Compila el proyecto primero: cd backend-server && ./mvnw clean package -DskipTests")
        return

    os.makedirs(PID_DIR, exist_ok=True)
    os.makedirs(LOG_DIR, exist_ok=True)

    log_path = log_file(name)
    log_fh = open(log_path, "a")

    env = os.environ.copy()
    env["APP_SERVER_NAME"] = f"backend-server-{name}"

    process = subprocess.Popen(
        [
            "java",
            "-jar",
            JAR_PATH,
            f"--server.port={port}",
            f"--app.server-name=backend-server-{name}",
        ],
        stdout=log_fh,
        stderr=subprocess.STDOUT,
        env=env,
        start_new_session=True,
    )

    with open(pid_file(name), "w") as f:
        f.write(str(process.pid))

    print(f"[{name}] iniciado (PID {process.pid}, puerto {port}), logs en {log_path}")


def stop_server(name):
    pid = read_pid(name)

    if not pid:
        print(f"[{name}] no tiene PID registrado (¿ya está detenido?)")
        return

    if not is_running(pid):
        print(f"[{name}] no está corriendo (PID {pid} inactivo)")
        os.remove(pid_file(name))
        return

    os.kill(pid, signal.SIGTERM)

    for _ in range(20):
        if not is_running(pid):
            break
        time.sleep(0.5)
    else:
        print(f"[{name}] no respondió a SIGTERM, enviando SIGKILL")
        os.kill(pid, signal.SIGKILL)

    os.remove(pid_file(name))
    print(f"[{name}] detenido (PID {pid})")


def status_server(name):
    port = SERVERS[name]
    pid = read_pid(name)

    if pid and is_running(pid):
        print(f"[{name}] corriendo (PID {pid}, puerto {port})")
    else:
        print(f"[{name}] detenido (puerto {port})")


def resolve_names(arg_name):
    if arg_name is None:
        return list(SERVERS.keys())

    name = arg_name.upper()
    if name not in SERVERS:
        print(f"Servidor desconocido: {arg_name}. Opciones válidas: {', '.join(SERVERS)}")
        sys.exit(1)

    return [name]


def main():
    parser = argparse.ArgumentParser(description="Gestiona las instancias A, B y C del backend-server")
    parser.add_argument("action", choices=["start", "stop", "status", "restart"])
    parser.add_argument("server", nargs="?", help="Nombre del servidor (A, B o C). Si se omite, aplica a todos.")
    args = parser.parse_args()

    names = resolve_names(args.server)

    for name in names:
        if args.action == "start":
            start_server(name)
        elif args.action == "stop":
            stop_server(name)
        elif args.action == "restart":
            stop_server(name)
            start_server(name)
        elif args.action == "status":
            status_server(name)


if __name__ == "__main__":
    main()
