import concurrent.futures
import requests
import time

URL = "http://localhost:8080/hello"

TOTAL_REQUESTS = 30
CONCURRENT_REQUESTS = 10


def make_request(request_id):
    try:
        start = time.time()

        response = requests.get(URL)

        elapsed = (time.time() - start) * 1000

        print(
            f"[{request_id:02}] "
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