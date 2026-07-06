import concurrent.futures
import requests
import time

URL = "http://localhost:8080/hello"

TOTAL_REQUESTS = 30
CONCURRENT_REQUESTS = 10

CLIENT_IPS = [
    "192.168.1.10",
    "192.168.1.11",
    "192.168.1.12",
    "192.168.1.13",
    "192.168.1.14",
]


def make_request(request_id):
    try:
        # Siempre la misma IP para el mismo "cliente"
        client_ip = CLIENT_IPS[(request_id - 1) % len(CLIENT_IPS)]

        headers = {
            "X-Forwarded-For": client_ip
        }

        start = time.time()

        response = requests.get(URL, headers=headers)

        elapsed = (time.time() - start) * 1000

        print(
            f"[{request_id:02}] "
            f"IP={client_ip} "
            f"Status={response.status_code} "
            f"Time={elapsed:.0f}ms "
            f"Body={response.text}"
        )

    except Exception as e:
        print(f"[{request_id:02}] ERROR -> {e}")


def main():
    with concurrent.futures.ThreadPoolExecutor(max_workers=CONCURRENT_REQUESTS) as executor:
        futures = [
            executor.submit(make_request, i)
            for i in range(1, TOTAL_REQUESTS + 1)
        ]

        concurrent.futures.wait(futures)


if __name__ == "__main__":
    main()